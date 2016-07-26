package com.tianzun.control;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tele.control.R;
import com.tianzun.iot.activity.SmartconfigActivity;
import com.tianzun.util.CheckUtil;
import com.tianzun.util.Command;
import com.tianzun.util.Config;
import com.tianzun.util.Router;
import com.tianzun.util.VibratorUtil;
import com.tianzun.util.Wifi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.text.BoringLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ActivityLampList extends Activity implements View.OnClickListener {

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

	ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

	ImageView dengkong_iv, jiadian_iv;

	private TextView controlcenter, pz;
	private boolean controlcenterOnline = false;

	private String centercontrolIp = "";

	private TextView dingshi;

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
		setContentView(R.layout.activity_lamplist);

		netWorkErrorReceiver = new NetworkErrorReceiver();
		intentFilter = new IntentFilter();
		intentFilter.addAction("com.tele.control.network.error");
		
		dingshi = (TextView) findViewById(R.id.dingshi);
		dingshi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ActivityLampList.this,AllTimeActivity.class);
				startActivity(intent);
			}
		});

		controlcenter = (TextView) findViewById(R.id.controlcenter);
		pz = (TextView) findViewById(R.id.pz);

		gridview = (GridView) findViewById(R.id.gridView1);

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
	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0,// The AdapterView where the click happened
				View arg1,// The view within the AdapterView that was clicked
				int arg2,// The position of the view in the adapter
				long arg3// The row id of the item that was clicked
		) {
			if (arg2 == index || jdlist.size() == 0) {
				startActivity(new Intent(ActivityLampList.this,ActivityLampAdd.class));
			} else {
				// 如果不在线
				controlcenterOnline = true;////to do !!!!!!!!!!!!!!!!!!!!!!!
				if (!controlcenterOnline) {
					new AlertDialog.Builder(ActivityLampList.this)
							.setMessage(getString(R.string.message_cf_offline))
							.setPositiveButton(getString(R.string.ok), null).show();
				} else {
					if(jdlist.get(arg2) == null){
						return;
					}
					Intent intent = new Intent(ActivityLampList.this,ActivityCT.class);
					jdlist.get(arg2).setIp(centercontrolIp);
					intent.putExtra("wifi", jdlist.get(arg2));
					int indexme = arg2 + 101;
					intent.putExtra("index", indexme);
					startActivity(intent);
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
		
		Log.i("onenet", "isConnected:"+SocketManager.getInstance().isConnected());
		if(SocketManager.getInstance().isConnected()){
			controlcenter.setText(R.string.cofig_center_online);
		}else{
			controlcenter.setText(R.string.cofig_center_offline);
		}
		
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo mInfo = wifiManager.getConnectionInfo();
		Gson gson = new Gson();
		SharedPreferences sp1 = getSharedPreferences("MyPrefsFile", 0);
		Boolean isconfig = sp1.getBoolean("isconfig", false);
		if (isconfig) {
			String BSSID = sp1.getString("BSSID", "");
			//controlcenter.setText(getString(R.string.cofig_center_online));
			if(CheckUtil.checkWifiStatus(this)){
				info.setText(getString(R.string.jdlist_conntected_wifi_label) + " BSSID:" + BSSID);
			}else{
				info.setText(getString(R.string.jdlist_conntected_wifi_label) + " BSSID:" + BSSID + "  not wifi status");
			}
		}else{
			info.setText(getString(R.string.jdlist_connecting_wifi) + mInfo.getSSID() + getString(R.string.jdlist_connecting_wifi_notconfig));
			//controlcenter.setText(getString(R.string.cofig_center));
			return;
		}


		//bind sucess
		jdlist = new ArrayList<Wifi>();
		jdlist.clear();
		SharedPreferences sp2 = getSharedPreferences("gs_lamplist", 0);
		String wifis = sp2.getString("gson", "");

		//gsinfo.setText(routers + "\n\n" + wifis);
		new ArrayList<HashMap<String, Object>>();
		index = 0;
		
		//to do:  currentRouter is needed!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		currentRouter = new Router();//
		
		if (!wifis.isEmpty() && currentRouter != null) {
			List<Wifi> gs = gson.fromJson(wifis, new TypeToken<List<Wifi>>() {}.getType());
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
		String[] logoonline = res.getStringArray(R.array.lamponline);
		for (int i = 0; i < jdlist.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();

			map.put("ItemImage", getLogo(logoonline[Integer.valueOf(jdlist.get(
					i).getCatid())]));

			map.put("ItemText", jdlist.get(i).getName());
			list.add(map);
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.tianjia);
		map.put("ItemText", getString(R.string.jdlist_add_device));
		list.add(map);

		nineAdapter = new SimpleAdapter(this, list, R.layout.ninex_item,new String[] {"ItemImage", "ItemText" }, new int[] {
						R.id.ItemImage, R.id.ItemText });
		gridview.setAdapter(nineAdapter);
		
		gridview.setOnItemClickListener(new ItemClickListener());
		
		gridview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (arg2 == index)
					return true;
				VibratorUtil.Vibrate(ActivityLampList.this,Config.BTN_PRESS_VIBE);
				Intent intent = new Intent(ActivityLampList.this,DialogWifiMenu.class);
				intent.putExtra("op", "lamp");
				intent.putExtra("wifi", jdlist.get(arg2));
				intent.putExtra("listWifi", (Serializable) jdlist);
				startActivity(intent);
				return true;
			}
		});

		/*new Thread(new Runnable() {
			@Override
			public void run() {
				if (CheckUtil.isLocal(getApplicationContext(),null)) {
					Command.commandRouter("sysinfo", handler,
							currentRouter.getBroadcastIP(), true);
				} else {
					
					Command.commandRemote(currentRouter.centermac, Config.CMD_SYSINFO, true, handler,  Config.WHAT_DEFAULT);
				}
				//Command.commandRouter("sysinfo", handler,currentRouter.getBroadcastIP(), true);
			}
		}).start();*/
		
		
		//refresh();

	}
	
	private int tryCount = 0;
	
	private void refresh(){
		stopRunningDialog();
		startDialog();
		final Wifi wifi= jdlist.size() !=0 && jdlist.size()>index-1 ? jdlist.get(index-1 <0 ? 0 :index-1) : null;
		new Thread(new Runnable() {
			@Override
			public void run() {
//				if (CheckUtil.isLocal(getApplicationContext(),wifi)) {
//					Command.commandRouter("sysinfo", handler,
//							currentRouter.getBroadcastIP(), true);
//				} else {
//					
//					Command.commandRemote(currentRouter.centermac, Config.CMD_SYSINFO, true, handler,  Config.WHAT_DEFAULT);
//				}
				//Command.commandRouter("sysinfo", handler,currentRouter.getBroadcastIP(), true);
			}
		}).start();
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
			intent = new Intent(new Intent(ActivityLampList.this,SmartconfigActivity.class));
			intent.putExtra("op", "lamp");
			startActivity(intent);
			break;
		case R.id.controlcenter:
			if (controlcenter.getText().toString().equals(getString(R.string.cofig_center))) {
				intent = new Intent(new Intent(ActivityLampList.this,ActivityWifiConfig.class));
				intent.putExtra("op", "lamp");
				startActivity(intent);
			}
			break;
		case R.id.shuaxin:
			if (currentRouter != null) {
				VibratorUtil.Vibrate(ActivityLampList.this, 200);
				refresh();
				/*new Thread(new Runnable() {

					@Override
					public void run() {
						if (CheckUtil.isLocal(getApplicationContext(),null)) {
							Command.commandRouter("sysinfo", handler,
									currentRouter.getBroadcastIP(), true);
						} else {
							
							Command.commandRemote(currentRouter.centermac, Config.CMD_SYSINFO, true, handler,  Config.WHAT_DEFAULT);
						}
						
						//Command.commandRouter("sysinfo", handler,currentRouter.getBroadcastIP(), true);

					}
				}).start();*/
			}
			break;

		case R.id.help:

			String info = getString(R.string.jdlist_help);
			new AlertDialog.Builder(ActivityLampList.this).setTitle(getString(R.string.jdlist_guideline))
					.setMessage(info).setPositiveButton(getString(R.string.ok), null).show();
			break;
		default:
			break;
		}

	}

	private void handleResult(Message msg) {
		Bundle bundle = msg.getData();
		String result = bundle.getString("result");
		bundle.getSerializable("wifi");

		if (result==null && "".equals(result)) {
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
				macAddress = obj.optJSONObject("Device")
						.getString("macaddress");
				ipAddress = obj.optJSONObject("Device").optString("ipaddress");

				if (macAddress.equals(currentRouter.getCentermac())) {
					controlcenterOnline = true;
					controlcenter.setText(getString(R.string.cofig_center_online));
					centercontrolIp = ipAddress;
					stopRunningDialog();
					tryCount=0;
				} else {
					while(tryCount ++ < 3 && !controlcenterOnline){//失败重试3次
						refresh();
					}
					
					tryCount=0;
					controlcenterOnline = false;
					controlcenter.setText(getString(R.string.cofig_center_offline));
					centercontrolIp = "";
					stopRunningDialog();
				}

			} catch (Exception e) {
				e.printStackTrace();
				
				while(tryCount ++ < 3 && !controlcenterOnline){//失败重试3次
					refresh();
				}
				//tryCount=0;
				stopRunningDialog();
				//Toast.makeText(this,getString(R.string.operation_fail) , Toast.LENGTH_SHORT).show();
			}
		}
	}

}
