package com.tianzun.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import onenet.edp.ConnectMsg;
import onenet.edp.ConnectRespMsg;
import onenet.edp.EdpKit;
import onenet.edp.EdpMsg;
import onenet.edp.PushDataMsg;
import onenet.edp.Common.MsgType;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tele.control.R;
import com.tianzun.clientview.SlipButton;
import com.tianzun.clientview.SlipButton.OnBChangedListener;
import com.tianzun.control.ActivityLampList.NetworkErrorReceiver;
import com.tianzun.iot.activity.SmartconfigActivity;
import com.tianzun.util.CheckUtil;
import com.tianzun.util.Command;
import com.tianzun.util.Config;
import com.tianzun.util.DataUtil;
import com.tianzun.util.Router;
import com.tianzun.util.VibratorUtil;
import com.tianzun.util.Wifi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ActivityJDList extends Activity implements View.OnClickListener {

	private Router currentRouter = null;
	private boolean routerConnected = false;
	private TextView info, gsinfo;
	private GridView gridview;
	private SimpleAdapter nineAdapter;
	private List<Wifi> jdlist;

	private Handler handler;
	private int index = 0;
	private ImageView shuaxin;
	private ImageView help;

	ArrayList<HashMap<String, Object>> list;

	ImageView dengkong_iv, jiadian_iv;

	SlipButton slipButton;

	private TextView controlcenter, pz;
	private boolean controlcenterOnline = false;

	private Wifi wifi;
	private String centercontrolIp = "";

	Gson gson = new Gson();
	
	byte[] packet;
	
	private WifiManager wifiManager;
	
	private ProgressDialog progressDlg;

	private NetworkErrorReceiver netWorkErrorReceiver;
	private IntentFilter intentFilter;
	
	public class NetworkErrorReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.i("onenet", "receive broadcast*************  action:" + intent.getAction());
			if(controlcenter != null && intent.getBooleanExtra("msg",false)){
				Log.i("onenet", "update the text");
				controlcenter.setText(R.string.cofig_center_offline);
			}
		}
	}
	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jdlist);

		netWorkErrorReceiver = new NetworkErrorReceiver();
		intentFilter = new IntentFilter();
		intentFilter.addAction("com.tele.control.network.error");
		
		list = new ArrayList<HashMap<String, Object>>();

		controlcenter = (TextView) findViewById(R.id.controlcenter);
		pz = (TextView) findViewById(R.id.pz);

		gridview = (GridView) findViewById(R.id.gridView1);

		slipButton = (SlipButton) findViewById(R.id.zong);
		slipButton.SetBOnChangedListener(new OnBChangedListener() {
			@Override
			public void OnChanged(boolean isChgLsnOn) {

				if (isChgLsnOn) {// 总开关开

					for (int i = 1; i < list.size(); i++) {
						wifi = jdlist.get(i - 1);
						wifi.setIp(centercontrolIp);
						byte myid1 = Byte.valueOf(Integer.toHexString(i));
						byte[] command = {0x11, 0x01, 0x02, 0x03, myid1, 0x02, 0x11 };
						cmd(command, wifi);
					}

				} else {// 总开关关

					for (int ii = 1; ii < list.size(); ii++) {
						wifi = jdlist.get(ii - 1);
						wifi.setIp(centercontrolIp);
						byte myid2 = Byte.valueOf(Integer.toHexString(ii));
						byte[] command = {0x11, 0x01, 0x02, 0x03, myid2, 0x02, 0x21 };
						cmd(command, wifi);

					}
				}
			}
		});

		info = (TextView) findViewById(R.id.info);
		gsinfo = (TextView) findViewById(R.id.gsinfo);

		shuaxin = (ImageView) findViewById(R.id.shuaxin);
		shuaxin.setOnClickListener(this);
		help = (ImageView) findViewById(R.id.help);
		help.setOnClickListener(this);
		controlcenter.setOnClickListener(this);
		pz.setOnClickListener(this);

		handler = new Handler() {
			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 0:
					handleResult(msg);
					break;

				default:
					break;
				}
			}

		};

		initjdlist();
	}

	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if (arg2 == index || (arg2==0 && jdlist.size()==0)) {
				startActivity(new Intent(ActivityJDList.this,
						ActivityJDAdd.class));
			} else {
				// 如果不在线
				controlcenterOnline = true;////to do !!!!!!!!!!!!!!!!!!!!!!!
				if (!controlcenterOnline) {
					new AlertDialog.Builder(ActivityJDList.this)
							.setMessage(getString(R.string.message_cf_offline))
							.setPositiveButton(getString(R.string.ok), null).show();
				} else {

					wifi = jdlist.get(arg2);
					wifi.setIp(centercontrolIp);

					final byte mmyid = Byte.valueOf(Integer.toHexString(arg2+1));

					new AlertDialog.Builder(ActivityJDList.this)
							.setTitle(wifi.getName() +" "+ getString(R.string.switch_tip))
							.setMessage(getString(R.string.jdlist_pleasechoose))
							.setPositiveButton(getString(R.string.on), new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									byte[] command = {0x11, 0x01, 0x02, 0x03, mmyid,
											0x02, 0x11 };
									cmd(command, wifi);
								}
							}).setNegativeButton(getString(R.string.off), new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									byte[] command = {0x11,0x01, 0x02, 0x03, mmyid,
											0x02, 0x21 };
									cmd(command, wifi);
								}
							}).setNeutralButton(getString(R.string.cancel), null).show();

				}
			}
		}
	}

	public int getLogo(String id_name) {
		try {
			Field field = R.drawable.class.getField(id_name);
			int i = field.getInt(new R.drawable());
			return i;
		} catch (Exception e) {
			return R.drawable.ic_launcher;
		}
	}

	public void initjdlist() {
    	wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);  
    	WifiInfo mInfo = wifiManager.getConnectionInfo();  
    	
    	
		SharedPreferences sp1 = getSharedPreferences("gs_routerlist", 0);
		String routers = sp1.getString("gson", "");
		
		SharedPreferences sp3= getSharedPreferences("MyPrefsFile", 0);
		Boolean isconfig = sp3.getBoolean("isconfig", false);
		
		Log.i("onenet", "isConnected:"+SocketManager.getInstance().isConnected());
		if(SocketManager.getInstance().isConnected()){
			controlcenter.setText(R.string.cofig_center_online);
		}else{
			controlcenter.setText(R.string.cofig_center_offline);
		}
		
		if (isconfig) {
//			if(CheckUtil.checkWifiStatus(this)){
//				info.setText("wifi status");
//			}else{
//				info.setText("not wifi status");
//			}
		}else{
			//info.setText("not config");
			return;
		}
		
//		if (!routers.isEmpty()) {
//			List<Router> gs = gson.fromJson(routers,
//					new TypeToken<List<Router>>() {
//					}.getType());
//			String jdlist_conntected_wifi_label = this.getString(R.string.jdlist_conntected_wifi_label);
//			for (int i = 0; i < gs.size(); i++) {
//				Router r = gs.get(i);
//				if (r.getMac().equals(mInfo.getBSSID())) {
//					currentRouter = r;
//					routerConnected = true;
//					info.setText(jdlist_conntected_wifi_label + mInfo.getSSID().replace("\"", ""));
//				}
//			}
//			if(!routerConnected && gs.size() >0 ){
//				currentRouter = gs.get(0);
//				routerConnected = true;
//				info.setText(jdlist_conntected_wifi_label + mInfo.getSSID().replace("\"", ""));
//			}
//		}
//
//		if (routerConnected == false) {
//			String text = this.getString(R.string.jdlist_connecting_wifi)+mInfo.getSSID()+ this.getString(R.string.jdlist_connecting_wifi_notconfig);
//			//info.setText("当前已连接WIFI：" + mInfo.getSSID() + "未配置");
//			info.setText(text);
//			//controlcenter.setText("配置控制中心");
//			controlcenter.setText(this.getString(R.string.cofig_center));
//			return;
//		}


		
		jdlist = new ArrayList<Wifi>();
		jdlist.clear();
		SharedPreferences sp2 = getSharedPreferences("gs_jdlist", 0);
		String wifis = sp2.getString("gson", "");

		//gsinfo.setText(routers + "\n\n" + wifis);
		index = 0;
		
		//to do:  currentRouter is needed!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		currentRouter = new Router();//
		
		if (!wifis.isEmpty() && currentRouter != null) {
			List<Wifi> gs = gson.fromJson(wifis, new TypeToken<List<Wifi>>() {
			}.getType());
			for (int i = 0; i < gs.size(); i++) {
				Wifi w = gs.get(i);
//				if (w.getRouterid() == currentRouter.getId()) {
//					jdlist.add(w);
//					index = i;
//				}
				jdlist.add(w);
				index = i;
			}
			index++;
		}
		init();

	}

	public void init() {

		list.clear();

		Resources res = getResources();
		String[] logoonline = res.getStringArray(R.array.jdonline);
		for (int i = 0; i < jdlist.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", getLogo(logoonline[Integer.valueOf(jdlist.get(
					i).getCatid())]));
			map.put("ItemText", jdlist.get(i).getName());
			list.add(map);
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.tianjia);
		map.put("ItemText", this.getString(R.string.jdlist_add_device));
		list.add(map);

		nineAdapter = new SimpleAdapter(this, list, R.layout.ninex_item,
				new String[] { "ItemImage", "ItemText" }, new int[] {
						R.id.ItemImage, R.id.ItemText });
		gridview.setAdapter(nineAdapter);
		gridview.setOnItemClickListener(new ItemClickListener());
		gridview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (arg2 == index)
					return true;
				
				if(jdlist == null && jdlist.size()>arg2){
					return true;
				}
				VibratorUtil
						.Vibrate(ActivityJDList.this, Config.BTN_PRESS_VIBE);
				Intent intent = new Intent(ActivityJDList.this,
						DialogWifiMenu.class);
				intent.putExtra("op", "jd");
				intent.putExtra("wifi", jdlist.get(arg2));
				intent.putExtra("listWifi", (Serializable) jdlist);
				startActivity(intent);

				return true;
			}
		});
		//refresh();

	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		unregisterReceiver(netWorkErrorReceiver);
		super.onPause();
	}
	
	@Override
	public void onResume() {
		registerReceiver(netWorkErrorReceiver, intentFilter);
		initjdlist();
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.pz:
			intent = new Intent(new Intent(ActivityJDList.this,SmartconfigActivity.class));
			intent.putExtra("op", "jd");
			startActivity(intent);
			break;
		case R.id.controlcenter:
			if (controlcenter.getText().toString().equals(this.getString(R.string.cofig_center))) {
				intent = new Intent(new Intent(ActivityJDList.this,SmartconfigActivity.class));
				intent.putExtra("op", "jd");
				startActivity(intent);
			}
			break;
		case R.id.shuaxin:
			if (currentRouter != null) {
				VibratorUtil.Vibrate(ActivityJDList.this, 200);
				refresh();
			}
			break;

		case R.id.help:

			String info = this.getString(R.string.jdlist_help);
			new AlertDialog.Builder(ActivityJDList.this).setTitle(this.getString(R.string.jdlist_guideline))
					.setMessage(info).setPositiveButton(this.getString(R.string.ok), null).show();
			break;
		default:
			break;
		}

	}
	
	private void refresh(){
		stopRunningDialog();
		startDialog();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				if (CheckUtil.isLocal(getApplicationContext(),null)) {
					Command.commandRouter("sysinfo", handler,
							currentRouter.getBroadcastIP(), true);
				} else {
					
					Command.commandRemote(currentRouter.centermac, Config.CMD_SYSINFO, true, handler,  Config.WHAT_DEFAULT);
				}
			/*	Command.commandRouter("sysinfo", handler,
						currentRouter.getBroadcastIP(), true);*/

			}
		}).start();
	}
	private int tryCount= 0;
	private void handleResult(Message msg) {
		Bundle bundle = msg.getData();
		String result = bundle.getString("result");
		bundle.getSerializable("wifi");

		if ("".equals(result)) {

			return;

		} else {
			String macAddress = "";
			String ipAddress = "";
			JSONObject obj;
			try {
				if (result.contains("response")) {
					obj = new JSONObject(result).getJSONObject("response");
				} else if (result.contains("Response")) {
					obj = new JSONObject(result).getJSONObject("Response");
				} else {
					obj = new JSONObject(result);
				}
				if(obj.has("Response")){
					obj = obj.getJSONObject("Response");
				}
				macAddress = obj.getJSONObject("Device")
						.getString("macaddress");
				ipAddress = obj.getJSONObject("Device").getString("ipaddress");

				if (macAddress.equals(currentRouter.getCentermac())) {
					controlcenterOnline = true;
					controlcenter.setText(this.getString(R.string.cofig_center_online));
					centercontrolIp = ipAddress;
					tryCount=0;
					stopRunningDialog();
				} else {
					while(tryCount ++ < 3 && !controlcenterOnline){//失败重试3次
						refresh();
					}
					tryCount=0;
					stopRunningDialog();
					controlcenterOnline = false;
					controlcenter.setText(this.getString(R.string.cofig_center_offline));
					centercontrolIp = "";
				}

			} catch (JSONException e) {
				while(tryCount ++ < 3 && !controlcenterOnline){//失败重试3次
					refresh();
				}
				//tryCount=0;
				e.printStackTrace();
			}
		}
	}
	
	private void startDialog(){
		if(progressDlg ==null){
			progressDlg = new ProgressDialog(this);
		}
			progressDlg.setMessage("请稍等...");
			progressDlg.setCancelable(false);
			progressDlg.show();
			new Timer().schedule(new TimerTask() {
				public void run() {
					stopRunningDialog(); // when the task active then close
					this.cancel(); // also just top the timer thread,
				}
			}, 10000);
	}
	 private void stopRunningDialog(){
	    	if (progressDlg != null && !progressDlg.isShowing()) {
				return;
			}

			if (progressDlg != null) {
				progressDlg.dismiss();
			}
	    }
	private Router findRouterById(int id){
		return CheckUtil.findRouterById(this,wifi.getRouterid());
		
	}
	public void cmd(final byte[] code, final Wifi wifi) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				int desDevId = Integer.parseInt(MyApplication.desDevId);
				PushDataMsg pushDataMsg1 = new PushDataMsg();
				try {
					packet = pushDataMsg1.packMsg(desDevId,code);
					if(SocketManager.getInstance().isConnected()){
						SocketManager.getInstance().getOutStream().write(packet);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}).start();
	}
}
