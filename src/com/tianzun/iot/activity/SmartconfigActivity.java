package com.tianzun.iot.activity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tele.control.R;
import com.tianzun.clientview.RoundProgressView;
import com.tianzun.control.ActivityLampAdd;
import com.tianzun.control.ActivityLampList;
import com.tianzun.control.ActivityTest;
import com.tianzun.control.ModuleThread;
import com.tianzun.control.MyApplication;
import com.tianzun.iot.esptouch.EsptouchTask;
import com.tianzun.iot.esptouch.IEsptouchListener;
import com.tianzun.iot.esptouch.IEsptouchResult;
import com.tianzun.iot.esptouch.IEsptouchTask;
import com.tianzun.iot.task.__IEsptouchTask;
import com.tianzun.util.Config;
import com.tianzun.util.Router;
import com.tianzun.util.VibratorUtil;
import com.tianzun.util.Wifi;
import com.tianzun.wifi.WifiAdmin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class SmartconfigActivity extends Activity implements OnClickListener,
		Callback {

	private Context mContext = SmartconfigActivity.this;
	public static final String ModuleSetting = "MyPrefsFile";
	private static final int MAX_DATA_PACKET_LENGTH = 40;
	private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];

	private WifiManager wifiManager;
	private String broadcast_ip = "";

	private static final String TAG = "SmartconfigActivity";
	//private TextView mTvApSsid;
	private Spinner sp_wifi_config_router_list = null;

	private EditText mEdtApPassword;
	//private Switch mSwitchIsSsidHidden;
	private EspWifiAdminSimple mWifiAdmin;
	private Spinner mSpinnerTaskCount;
	private RoundProgressView rpv_wifi_config_ok;
	private Button btn_wifi_config_back;
	private Button btn_wifi_config_clear;
	private ImageView eye_iv;

	private String router_mac = "";
	private CheckBox checkBox;
	
	private void initView() {
		// TODO Auto-generated method stub
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		mWifiAdmin = new EspWifiAdminSimple(this);
		
		//mTvApSsid = (TextView) findViewById(R.id.tvApSssidConnected);
		sp_wifi_config_router_list = (Spinner)findViewById(R.id.sp_wifi_config_router_list);
		mEdtApPassword = (EditText) findViewById(R.id.edtApPassword);
		checkBox = (CheckBox) findViewById(R.id.checkBox1);
		initWifiAccount();
		
		
		rpv_wifi_config_ok = (RoundProgressView) this
				.findViewById(R.id.rpv_wifi_config_ok);
		rpv_wifi_config_ok.setOnClickListener(this);
		if (get_configstatus() == false) {
			rpv_wifi_config_ok.setMax(100f);
			rpv_wifi_config_ok.setText(getString(R.string.config_lable1));
		} else {
			rpv_wifi_config_ok.setProgress(0f);
			rpv_wifi_config_ok.setMax(100f);
			rpv_wifi_config_ok.setText(getString(R.string.config_lable1));
		}
//		mSwitchIsSsidHidden = (Switch) findViewById(R.id.switchIsSsidHidden);
		btn_wifi_config_back = (Button) this
				.findViewById(R.id.btn_wifi_config_back);
		btn_wifi_config_back.setOnClickListener(this);
		eye_iv = (ImageView) findViewById(R.id.eye_iv);
		eye_iv.setOnClickListener(this);
		initSpinner();
	}

	private void initWifiAccount() {
		// TODO Auto-generated method stub
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		String ssid = wifiInfo.getSSID();
		if (ssid == null) {
			ssid = "";
		}
		if (wifiInfo != null) {
			router_mac = wifiInfo.getBSSID();

			List<Wifi> controllerWifi = new ArrayList<Wifi>();
			List<Wifi> routerWifi = new ArrayList<Wifi>();
			List<ScanResult> list = wifiManager.getScanResults();

			Wifi currentWifi = null;
			if (list != null) {
				for (ScanResult sr : list) {
					String srSsid = sr.SSID.replace("\"", "");
					Wifi wifi = new Wifi(sr.BSSID, srSsid);
					if (srSsid.contains("ctunite_")) {
						controllerWifi.add(wifi);
					} else {
						if (srSsid.equals(ssid)) {
							currentWifi = new Wifi(sr.BSSID, srSsid);
						} else {
							routerWifi.add(wifi);
						}
					}
				}
				if (currentWifi != null) {
					routerWifi.add(0, currentWifi);
				}
			}
			
			ArrayAdapter<Wifi> radapter = new ArrayAdapter<Wifi>(this,R.layout.item_sp_wifi_list_item, routerWifi);
			sp_wifi_config_router_list.setAdapter(radapter);
			sp_wifi_config_router_list.setOnItemSelectedListener(new OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							// 从缓存里找到改路由信息，如果有，就看是否上次要记住密码并设置密码
							String selectedssid = arg0.getSelectedItem().toString();
							Gson gson = new Gson();
							SharedPreferences sp1 = getSharedPreferences("gs_routerlist", 0);
							String routers = sp1.getString("gson", "");
							if (!routers.isEmpty()) {
								List<Router> gs = gson.fromJson(routers,
										new TypeToken<List<Router>>() {
										}.getType());
								for (int i = 0; i < gs.size(); i++) {
									Router r = gs.get(i);
									if (r.getSSID().equals(selectedssid)) {
										if (r.getRemember() == 1) {
											mEdtApPassword.setText(r.getPassword());
											checkBox.setChecked(true);
										} else {
											mEdtApPassword.setText("");
											checkBox.setChecked(false);
										}
									} else {// 列表默认的，或者自己选的，如果在ROUTER表上没有都置空
										mEdtApPassword.setText("");
										checkBox.setChecked(false);
									}
								}
							}
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {

						}
					});

		}
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.smartconfig_activity);
		initView();

	}

	private void initSpinner() {
//		mSpinnerTaskCount = (Spinner) findViewById(R.id.spinnerTaskResultCount);
//		int[] spinnerItemsInt = getResources().getIntArray(
//				R.array.taskResultCount);
//		int length = spinnerItemsInt.length;
//		Integer[] spinnerItemsInteger = new Integer[length];
//		for (int i = 0; i < length; i++) {
//			spinnerItemsInteger[i] = spinnerItemsInt[i];
//		}
//		ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,
//				android.R.layout.simple_list_item_1, spinnerItemsInteger);
//		mSpinnerTaskCount.setAdapter(adapter);
//		mSpinnerTaskCount.setSelection(1);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// display the connected ap's ssid
//		String apSsid = mWifiAdmin.getWifiConnectedSsid();
//		if (apSsid != null) {
//			mTvApSsid.setText(apSsid);
//		} else {
//			mTvApSsid.setText("");
//		}
//		// check whether the wifi is connected
//		boolean isApSsidEmpty = TextUtils.isEmpty(apSsid);
//		rpv_wifi_config_ok.setEnabled(!isApSsidEmpty);
	}

	@Override
	public void onClick(View v) {
		Wifi wf = (Wifi) sp_wifi_config_router_list.getSelectedItem();
		VibratorUtil.Vibrate(this, Config.BTN_PRESS_VIBE);
		switch (v.getId()) {
		case R.id.rpv_wifi_config_ok:
				if (mEdtApPassword.getText().toString().equals("")) {
					Toast.makeText(this, getString(R.string.input_password),Toast.LENGTH_SHORT).show();
					return;
				} else {
					if (rpv_wifi_config_ok.getText().equals(getString(R.string.cancel))) {
						rpv_wifi_config_ok.setText(getString(R.string.config_lable1));
						rpv_wifi_config_ok.invalidate();
					} else {

						String apSsid = wf.getSSID();
						//String apSsid = mTvApSsid.getText().toString();
						
						String apPassword = mEdtApPassword.getText().toString();
						String apBssid = mWifiAdmin.getWifiConnectedBssid();
//						Boolean isSsidHidden = mSwitchIsSsidHidden.isChecked();
//						String isSsidHiddenStr = "NO";
//						String taskResultCountStr = Integer.toString(mSpinnerTaskCount.getSelectedItemPosition());
//						if (isSsidHidden) {
//							isSsidHiddenStr = "YES";
//						}
						Log.d(TAG, "hujunjie apSsid: " + apSsid);
						Log.d(TAG, "hujunjie apBssid: " + apBssid);
						Log.d(TAG, "hujunjie ap-pwd: " + apPassword);
						if (__IEsptouchTask.DEBUG) {
							Log.d(TAG, "mBtnConfirm is clicked, mEdtApSsid = "
									+ apSsid + ", " + " mEdtApPassword = "
									+ apPassword);
						}
						new EsptouchAsyncTask3().execute(apSsid, apBssid, apPassword,"NO","1");
						rpv_wifi_config_ok.setProgress(0f);
						rpv_wifi_config_ok.setText(getString(R.string.cancel));
						handler.removeMessages(Config.WHAT_UPDATE_OK);
						handler.sendEmptyMessage(Config.WHAT_UPDATE_OK);
					}
				}
			break;
		case R.id.btn_wifi_config_back:
//			String op = getIntent().getStringExtra("op").toString();
//			Intent intent = null;
//			intent = new Intent(SmartconfigActivity.this, ActivityTest.class);
//			// if ("lamp".equals(op)) {
//			// intent = new Intent(v.getContext(), ActivityLampList.class);
//			// } else if ("jd".equals(op)) {
//			// intent = new Intent(v.getContext(), ActivityJDList.class);
//			// }
//			startActivity(intent);
			SmartconfigActivity.this.finish();
			break;

		case R.id.eye_iv:
			int inputtype = mEdtApPassword.getInputType();
			if (inputtype == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
				mEdtApPassword.setInputType(InputType.TYPE_CLASS_TEXT);
			} else {
				mEdtApPassword
						.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			}
			break;

		}
	}

	private class EsptouchAsyncTask2 extends
			AsyncTask<String, Void, IEsptouchResult> {

		private ProgressDialog mProgressDialog;

		private IEsptouchTask mEsptouchTask;
		// without the lock, if the user tap confirm and cancel quickly enough,
		// the bug will arise. the reason is follows:
		// 0. task is starting created, but not finished
		// 1. the task is cancel for the task hasn't been created, it do nothing
		// 2. task is created
		// 3. Oops, the task should be cancelled, but it is running
		private final Object mLock = new Object();

		@Override
		protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(SmartconfigActivity.this);
			mProgressDialog
					.setMessage("Esptouch is configuring, please wait for a moment...");
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					synchronized (mLock) {
						if (__IEsptouchTask.DEBUG) {
							Log.i(TAG, "progress dialog is canceled");
						}
						if (mEsptouchTask != null) {
							mEsptouchTask.interrupt();
						}
					}
				}
			});
			mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE,
					"Waiting...", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			mProgressDialog.show();
			mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
					.setEnabled(false);
		}

		@Override
		protected IEsptouchResult doInBackground(String... params) {
			synchronized (mLock) {
				String apSsid = params[0];
				String apBssid = params[1];
				String apPassword = params[2];
				String isSsidHiddenStr = params[3];
				boolean isSsidHidden = false;
				if (isSsidHiddenStr.equals("YES")) {
					isSsidHidden = true;
				}
				mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword,
						isSsidHidden, SmartconfigActivity.this);
			}
			IEsptouchResult result = mEsptouchTask.executeForResult();
			return result;
		}

		@Override
		protected void onPostExecute(IEsptouchResult result) {
			mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
					.setEnabled(true);
			mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(
					"Confirm");
			// it is unnecessary at the moment, add here just to show how to use
			// isCancelled()
			if (!result.isCancelled()) {
				if (result.isSuc()) {
					String bssid = result.getBssid();
					String InetAddress = result.getInetAddress()
							.getHostAddress();
					mProgressDialog.setMessage("Esptouch success, bssid = "
							+ result.getBssid() + ",InetAddress = "
							+ result.getInetAddress().getHostAddress());
					Log.i(TAG, "BSSID = " + bssid + " InetAddress = "
							+ InetAddress);
					set_preference(bssid);
				} else {
					mProgressDialog.setMessage("Esptouch fail");
				}
			} else {
				Log.i(TAG, "send Brocast");
				String ModuleBssid = get_preference();
				Intent intent = new Intent("control.ModuleRecriver");
				intent.putExtra("BSSID", ModuleBssid);
				intent.putExtra("msg", "发送广播请求"); // 添加附加信息
				sendBroadcast(intent); // 发送Intent
				Log.i(TAG, "" + intent);
				mContext.sendBroadcast(intent);
			}
		}
	}

	private void onEsptoucResultAddedPerform(final IEsptouchResult result) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				String text = result.getBssid() + " is connected to the wifi";
				Toast.makeText(SmartconfigActivity.this, text,
						Toast.LENGTH_LONG).show();
			}

		});
	}

	private IEsptouchListener myListener = new IEsptouchListener() {

		@Override
		public void onEsptouchResultAdded(final IEsptouchResult result) {
			onEsptoucResultAddedPerform(result);
		}
	};

	private class EsptouchAsyncTask3 extends
			AsyncTask<String, Void, List<IEsptouchResult>> {

		private ProgressDialog mProgressDialog;

		private IEsptouchTask mEsptouchTask;
		// without the lock, if the user tap confirm and cancel quickly enough,
		// the bug will arise. the reason is follows:
		// 0. task is starting created, but not finished
		// 1. the task is cancel for the task hasn't been created, it do nothing
		// 2. task is created
		// 3. Oops, the task should be cancelled, but it is running
		private final Object mLock = new Object();

		@Override
		protected void onPreExecute() {
//			mProgressDialog = new ProgressDialog(SmartconfigActivity.this);
//			mProgressDialog.setMessage("Esptouch is configuring, please wait for a moment...");
//			mProgressDialog.setCanceledOnTouchOutside(false);
//			mProgressDialog.setOnCancelListener(new OnCancelListener() {
//				@Override
//				public void onCancel(DialogInterface dialog) {
//					synchronized (mLock) {
//						if (__IEsptouchTask.DEBUG) {
//							Log.i(TAG, "progress dialog is canceled");
//						}
//						if (mEsptouchTask != null) {
//							mEsptouchTask.interrupt();
//						}
//					}
//				}
//			});
//			mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE,
//					"Waiting...", new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//						}
//					});
//			mProgressDialog.show();
//			mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
//					.setEnabled(false);
		}

		@Override
		protected List<IEsptouchResult> doInBackground(String... params) {
			int taskResultCount = -1;
			synchronized (mLock) {
				String apSsid = params[0];
				String apBssid = params[1];
				String apPassword = params[2];
				String isSsidHiddenStr = params[3];
				String taskResultCountStr = params[4];
				boolean isSsidHidden = false;
				if (isSsidHiddenStr.equals("YES")) {
					isSsidHidden = true;
				}
				taskResultCount = Integer.parseInt(taskResultCountStr);
				mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword,
						isSsidHidden, SmartconfigActivity.this);
				mEsptouchTask.setEsptouchListener(myListener);
			}
			List<IEsptouchResult> resultList = mEsptouchTask
					.executeForResults(taskResultCount);
			return resultList;
		}

		@Override
		protected void onPostExecute(List<IEsptouchResult> result) {
//			mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
//			mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText("Confirm");
			IEsptouchResult firstResult = result.get(0);
			// check whether the task is cancelled and no results received
			if (!firstResult.isCancelled()) {
				int count = 0;
				// max results to be displayed, if it is more than
				// maxDisplayCount,
				// just show the count of redundant ones
				final int maxDisplayCount = 5;
				// the task received some results including cancelled while
				// executing before receiving enough results
				if (firstResult.isSuc()) {
					StringBuilder sb = new StringBuilder();
					for (IEsptouchResult resultInList : result) {
						String bssid = resultInList.getBssid();
						String InetAddress = resultInList.getInetAddress()
								.getHostAddress();
						sb.append("Esptouch success, bssid = "
								+ resultInList.getBssid()
								+ ",InetAddress = "
								+ resultInList.getInetAddress()
										.getHostAddress() + "\n");
						count++;
						set_preference(bssid);
						if (count >= maxDisplayCount) {
							break;
						}
					}
					if (count < result.size()) {
						sb.append("\nthere's " + (result.size() - count)
								+ " more result(s) without showing\n");
					}
					//mProgressDialog.setMessage(sb.toString());
					Log.d(TAG,"hujunjie bind success");
					handler.sendEmptyMessage(Config.WHAT_SUCESS);
				} else {
					Log.d(TAG,"hujunjie bind fail");
					handler.sendEmptyMessage(Config.WHAT_FAIL);
					//mProgressDialog.setMessage("Esptouch fail");
				}
			}
		}
	}

	private Handler handler = new Handler(this);

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Config.WHAT_UPDATE_OK:
			if (rpv_wifi_config_ok.getProgress() < rpv_wifi_config_ok.getMax()
					&& rpv_wifi_config_ok.getText().equals(getString(R.string.cancel))) {
				rpv_wifi_config_ok.setProgress(rpv_wifi_config_ok.getProgress() + 1f);
				handler.sendEmptyMessageDelayed(Config.WHAT_UPDATE_OK, 400);
//				if (isFirst) {
//					ConnectWifiRunnable r = new ConnectWifiRunnable();
//					Thread t = new Thread(r);
//					t.start();
//				}
			} else {
				rpv_wifi_config_ok.setText(getString(R.string.config_lable1));
				rpv_wifi_config_ok.setProgress(0f);
//				isFirst = true;
//				resetWifiConnect();
			}
			break;

		case Config.WHAT_SUCESS:
			rpv_wifi_config_ok.setProgress(100f);
			rpv_wifi_config_ok.setText("配置成功");
			rpv_wifi_config_ok.invalidate();
			
			Log.d("onenet", "config sucess, now ready to get DesDevId");
			MyApplication.mApplication.getDesDevId();
			
//			Intent intent = new Intent(new Intent(SmartconfigActivity.this,ActivityLampAdd.class));
//			startActivity(intent);
			finish();
			break;
		case Config.WHAT_FAIL:
			rpv_wifi_config_ok.setProgress(0f);
			rpv_wifi_config_ok.setText("配置失败");
			rpv_wifi_config_ok.invalidate();
			break;
		case Config.WHAT_DEFAULT:

			// configResult(msg);
			break;
		case Config.WHAT_ERROR:
			Toast.makeText(this, getString(R.string.config_fail),
					Toast.LENGTH_SHORT).show();
			// resetWifiConnect();
			break;
		case Config.WHAT_CHECK_WIFI_ONLINE:
			Bundle bundle = msg.getData();
			String result = bundle.getString("result");
			try {
				result = new JSONObject(result).getString("data");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			bundle.putString("result", result);
			msg.setData(bundle);
			// configResult(msg);
			break;

		}
		return false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	// set preference
	private void set_preference(String ModuleBssid) {
		// TODO Auto-generated method stub
		SharedPreferences settings = getSharedPreferences(ModuleSetting, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("BSSID", ModuleBssid);
		editor.putBoolean("isconfig", true);
		editor.commit();
		Log.i(TAG, "set_preference : BSSID = " + ModuleBssid);
	}

	// get preference
	public String get_preference() {
		// TODO Auto-generated method stub
		SharedPreferences settings = getSharedPreferences(ModuleSetting, 0);
		Boolean isconfig = settings.getBoolean("isconfig", false);
		String ModuleBssid;
		if (isconfig == true){
			ModuleBssid = settings.getString("BSSID", "null");
		} else {
			ModuleBssid = "null";
		}
		return ModuleBssid;
	}

	public Boolean get_configstatus() {
		SharedPreferences settings = getSharedPreferences(ModuleSetting, 0);
		Boolean isconfig = settings.getBoolean("isconfig", false);
		return isconfig;
	}

	private String getBroadcastIp() {
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		String routerIp = WifiAdmin.convertToIp(wifiInfo.getIpAddress());

		broadcast_ip = routerIp.substring(0, routerIp.lastIndexOf("."))
				+ ".255";
		return broadcast_ip;
	}

	public class BroadCastUdp extends Thread {
		private String dataString;
		private DatagramSocket udpSocket;

		public BroadCastUdp(String dataString) {
			this.dataString = dataString;
		}

		public void run() {
			DatagramPacket dataPacket = null;
			DatagramPacket recvPacket = null;

			try {
				udpSocket = new DatagramSocket(Config.BROADCAST_PORT_DEFAULT);

				dataPacket = new DatagramPacket(buffer, MAX_DATA_PACKET_LENGTH);
				recvPacket = new DatagramPacket(buffer, MAX_DATA_PACKET_LENGTH);
				byte[] data = dataString.getBytes();
				dataPacket.setData(data);
				dataPacket.setLength(data.length);
				dataPacket.setPort(Config.BROADCAST_PORT_DEFAULT);

				InetAddress broadcastAddr;

				broadcastAddr = InetAddress.getByName(getBroadcastIp());
				dataPacket.setAddress(broadcastAddr);
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
			Config.ModuleThread = new ModuleThread();
			Config.ModuleThread.start();

			while (Config.modulesocket == null) {
				try {
					udpSocket.send(dataPacket);
					sleep(500);
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}

			}
			
			while(recvPacket.getAddress() == null){
				try {
					udpSocket.receive(recvPacket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e(TAG, e.toString());
				}
			}
			
			Log.i(TAG, "get msg:"+recvPacket.getAddress());
			Log.i(TAG, "get msg:"+recvPacket.getPort());
			
			udpSocket.close();

		}
	}
}
