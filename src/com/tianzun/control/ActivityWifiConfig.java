package com.tianzun.control;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tele.control.R;
import com.tianzun.clientview.RoundProgressView;
import com.tianzun.util.CheckUtil;
import com.tianzun.util.Command;
import com.tianzun.util.Config;
import com.tianzun.util.Router;
import com.tianzun.util.VibratorUtil;
import com.tianzun.util.Wifi;
import com.tianzun.wifi.WifiAdmin;

public class ActivityWifiConfig extends Activity implements
		View.OnClickListener, Callback {

	private RoundProgressView rpv_wifi_config_ok;
	private Button btn_wifi_config_back;
	private EditText et_wifi_config_password;

	private Spinner sp_wifi_config_router_list = null;
	private WifiManager wifiManager;
	private String router_ssid = "";
	private String router_password = "";
	private String router_mac = "";
	private String wifi_mac = "";
	private String wifi_bssid = "";
	private boolean isFirst = true;

	private String config_wifi_ssid = "";
	private String router_broadcast_ip = "";
	private Spinner sp_wifi_config_wifi_list = null;
	private Button btn_wifi_config_refresh;
	private Button btn_wifi_list_refresh;

	private ImageView eye_iv;
	private CheckBox checkBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_config);

		eye_iv = (ImageView) findViewById(R.id.eye_iv);
		eye_iv.setOnClickListener(this);
		checkBox = (CheckBox) findViewById(R.id.checkBox1);

		rpv_wifi_config_ok = (RoundProgressView) this
				.findViewById(R.id.rpv_wifi_config_ok);
		rpv_wifi_config_ok.setOnClickListener(this);
		rpv_wifi_config_ok.setMax(100f);
		rpv_wifi_config_ok.setText(getString(R.string.config_lable1));

		et_wifi_config_password = (EditText) this
				.findViewById(R.id.et_wifi_config_password);
		btn_wifi_config_refresh = (Button) this
				.findViewById(R.id.btn_wifi_config_refresh);
		et_wifi_config_password.setFocusable(true);
		et_wifi_config_password.setText("");

		sp_wifi_config_wifi_list = (Spinner) this
				.findViewById(R.id.sp_wifi_config_wifi_list);
		sp_wifi_config_router_list = (Spinner) this
				.findViewById(R.id.sp_wifi_config_router_list);
		btn_wifi_list_refresh = (Button) this
				.findViewById(R.id.btn_wifi_list_refresh);
		btn_wifi_config_back = (Button) this
				.findViewById(R.id.btn_wifi_config_back);
		btn_wifi_config_back.setOnClickListener(this);
		btn_wifi_config_refresh.setOnClickListener(this);
		btn_wifi_list_refresh.setOnClickListener(this);

		initView();

	}

	private void initView() {
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
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

			ArrayAdapter<Wifi> cadapter = new ArrayAdapter<Wifi>(this,
					R.layout.item_sp_wifi_list_item, controllerWifi);
			sp_wifi_config_wifi_list.setAdapter(cadapter);

			ArrayAdapter<Wifi> radapter = new ArrayAdapter<Wifi>(this,
					R.layout.item_sp_wifi_list_item, routerWifi);
			sp_wifi_config_router_list.setAdapter(radapter);
			sp_wifi_config_router_list
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							// 从缓存里找到改路由信息，如果有，就看是否上次要记住密码并设置密码
							String selectedssid = arg0.getSelectedItem()
									.toString();
							Gson gson = new Gson();
							SharedPreferences sp1 = getSharedPreferences(
									"gs_routerlist", 0);
							String routers = sp1.getString("gson", "");
							if (!routers.isEmpty()) {
								List<Router> gs = gson.fromJson(routers,
										new TypeToken<List<Router>>() {
										}.getType());
								for (int i = 0; i < gs.size(); i++) {
									Router r = gs.get(i);
									if (r.getSSID().equals(selectedssid)) {
										if (r.getRemember() == 1) {
											et_wifi_config_password.setText(r
													.getPassword());
											checkBox.setChecked(true);
										} else {
											et_wifi_config_password.setText("");
											checkBox.setChecked(false);
										}

									} else {// 列表默认的，或者自己选的，如果在ROUTER表上没有都置空
										et_wifi_config_password.setText("");
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
	protected void onResume() {
		super.onResume();

		initView();
		reset();

	}

	private void reset() {
		tryCount = 0;
		connectTryCount = 0;

		isFirst = true;
	}

	@Override
	public void onClick(View v) {
		Wifi wf = (Wifi) sp_wifi_config_wifi_list.getSelectedItem();
		VibratorUtil.Vibrate(this, Config.BTN_PRESS_VIBE);
		switch (v.getId()) {

		case R.id.rpv_wifi_config_ok:

			if (sp_wifi_config_router_list.getSelectedItem() == null) {
				Toast.makeText(this, getString(R.string.input_wifi),
						Toast.LENGTH_SHORT).show();
				return;
			} else if (et_wifi_config_password.getText().toString().equals("")) {
				Toast.makeText(this, getString(R.string.input_password),
						Toast.LENGTH_SHORT).show();
				return;
			} else if (wf == null) {
				Toast.makeText(this, getString(R.string.choose_wifi),
						Toast.LENGTH_SHORT).show();
				return;
			} else {

				config_wifi_ssid = wf.getSSID();
				if (rpv_wifi_config_ok.getText().equals(
						getString(R.string.cancel))) {
					rpv_wifi_config_ok
							.setText(getString(R.string.config_lable1));
					reset();
				} else {
					reset();
					setRouterBroadcastIp();
					Log.i("router_broadcast_ip", "" + router_broadcast_ip);
					router_ssid = sp_wifi_config_router_list.getSelectedItem()
							.toString();
					router_password = et_wifi_config_password.getText()
							.toString();
					handler.removeMessages(Config.WHAT_UPDATE_OK);
					rpv_wifi_config_ok.setProgress(0f);
					rpv_wifi_config_ok.setText(getString(R.string.cancel));
					handler.sendEmptyMessage(Config.WHAT_UPDATE_OK);
				}
			}

			break;
		case R.id.btn_wifi_config_back:
			String op = getIntent().getStringExtra("op").toString();
			Intent intent = null;
			if ("lamp".equals(op)) {
				intent = new Intent(v.getContext(), ActivityLampList.class);
			} else if ("jd".equals(op)) {
				intent = new Intent(v.getContext(), ActivityJDList.class);
			}
			startActivity(intent);
			ActivityWifiConfig.this.finish();
			break;
		case R.id.btn_wifi_config_refresh:
			Toast.makeText(v.getContext(), getString(R.string.refresing),
					Toast.LENGTH_SHORT).show();
			wifiManager.startScan();
			initView();
			break;
		case R.id.sp_wifi_config_wifi_list:

			break;
		case R.id.btn_wifi_list_refresh:
			Toast.makeText(v.getContext(), getString(R.string.refresing),
					Toast.LENGTH_SHORT).show();
			wifiManager.startScan();
			initView();
			break;

		case R.id.eye_iv:
			et_wifi_config_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			break;

		}
	}

	private void setRouterBroadcastIp() {
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		String routerIp = WifiAdmin.convertToIp(wifiInfo.getIpAddress());

		router_broadcast_ip = routerIp.substring(0, routerIp.lastIndexOf("."))
				+ ".255";
		router_mac = wifiInfo.getBSSID();
	}

	private int connectTryCount = 0;

	private class ConnectWifiRunnable implements Runnable {

		public ConnectWifiRunnable() {
			connectTryCount = 0;
		}

		@Override
		public void run() {

			isFirst = false;
			WifiAdmin.init(ActivityWifiConfig.this);
			WifiAdmin.getInstance().connect(config_wifi_ssid, true);

			while (connectTryCount < Config.CONFIG_WIFI_TRY_COUNT) {
				connectTryCount++;
				configWifi(handler);
				try {
					Thread.sleep(600);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}

	private synchronized void configWifi(Handler handler) {

		if (connectTryCount >= Config.CONFIG_WIFI_TRY_COUNT) {
			// handler.sendEmptyMessage(Config.WHAT_ERROR);
			return;
		}
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();

		String wifiIp = WifiAdmin.convertToIp(wifiInfo.getIpAddress());
		Log.i("连接wifi-->ip Address", wifiIp);
		if (wifiIp.contains("192.168.4")) {
			connectTryCount = Config.CONFIG_WIFI_TRY_COUNT;
			wifi_bssid = wifiInfo.getBSSID();
			setWifiConf(handler);
		}

	}

	private Handler handler = new Handler(this);

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Config.WHAT_UPDATE_OK:
			if (rpv_wifi_config_ok.getProgress() < rpv_wifi_config_ok.getMax()
					&& rpv_wifi_config_ok.getText().equals(
							getString(R.string.cancel))) {
				rpv_wifi_config_ok
						.setProgress(rpv_wifi_config_ok.getProgress() + 1f);

				handler.sendEmptyMessageDelayed(Config.WHAT_UPDATE_OK, 400);
				if (isFirst) {

					ConnectWifiRunnable r = new ConnectWifiRunnable();
					Thread t = new Thread(r);
					t.start();
				}
			} else {
				rpv_wifi_config_ok.setText(getString(R.string.config_lable1));
				rpv_wifi_config_ok.setProgress(0f);
				isFirst = true;
				resetWifiConnect();
			}
			break;

		case Config.WHAT_DEFAULT:

			configResult(msg);
			break;
		case Config.WHAT_ERROR:
			Toast.makeText(this, getString(R.string.config_fail),
					Toast.LENGTH_SHORT).show();
			resetWifiConnect();
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

	private void resetWifiConnect() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		// isFirst = true;
		String currentSsid = wifiInfo.getSSID();
		if (!router_ssid.equals(currentSsid)) {
			WifiAdmin.init(this);
			WifiAdmin.getInstance().connect(router_ssid, router_password,
					Config.WifiCipherType.WIFICIPHER_WPA);
		}

	}

	private void setWifiConf(Handler handler) {
		String data = Command.commandWifi(Config.CMD_GETWIFICONF, true);
		if (!"".equals(data)) {
			try {
				/*
				 * if(connectTryCount>=Config.CONFIG_WIFI_TRY_COUNT) {
				 * //handler.sendEmptyMessage(Config.WHAT_ERROR); return; }
				 */
				connectTryCount = Config.CONFIG_WIFI_TRY_COUNT + 1;
				JSONObject obj = new JSONObject(data);
				obj.getJSONObject("Response").remove("Softap");
				obj.getJSONObject("Response").getJSONObject("Station")
						.getJSONObject("Connect_Station")
						.put("password", router_password)
						.put("ssid", router_ssid);

				Command.setNetConn("");
				String sysInfo = Command.commandWifi(Config.CMD_SYSINFO, true);

				if (!CheckUtil.isBlank(sysInfo)) {
					JSONObject sysObj = new JSONObject(sysInfo);
					wifi_mac = sysObj.getJSONObject("Response")
							.getJSONObject("Device").getString("macaddress");
				}
				Command.commandWifi(
						Config.CMD_SETWIFICONF + "=" + obj.toString(), false);

				resetWifiConnect();
				ConfigCompleteRunnable r = new ConfigCompleteRunnable();
				Thread t = new Thread(r);
				t.start();

			} catch (Exception e) {
				handler.sendEmptyMessage(Config.WHAT_ERROR);
			}
		} else {
			handler.sendEmptyMessage(Config.WHAT_ERROR);
		}

	}

	private synchronized void configResult(Message msg) {

		if (tryCount >= Config.CONFIG_WIFI_TRY_COUNT) {
			handler.sendEmptyMessage(Config.WHAT_ERROR);
			return;
		}
		Bundle bundle = msg.getData();

		String result = bundle.getString("result");
		handleResult(result);
	}

	private void handleResult(String result) {
		if ("".equals(result)) {
			return;
		} else {
			String macAddress = "";
			String ipAddress = "";
			result = result.replace("response", "Response");
			Log.i("response test", result);
			JSONObject obj;
			try {
				if (result.contains("response")) {
					obj = new JSONObject(result).getJSONObject("response");
				} else if (result.contains("Response")) {
					obj = new JSONObject(result).getJSONObject("Response");
				} else {
					obj = new JSONObject(result);
				}

				macAddress = obj.getJSONObject("Device")
						.getString("macaddress");
				ipAddress = obj.getJSONObject("Device").getString("ipaddress");
				if (Config.INVALID_IP_ADDRESS.equals(ipAddress)) {
					return;
				} else {
					if (!wifi_mac.equals(macAddress)) {
						return;
					} else {
						tryCount = Config.CONFIG_WIFI_TRY_COUNT;
						// 保存设置家里路由器信息
						String broadcastIp = ipAddress.substring(0,
								ipAddress.lastIndexOf("."))
								+ ".255";
						Router router = new Router(router_mac, router_ssid,
								router_password, broadcastIp);
						Wifi wf = new Wifi(macAddress, config_wifi_ssid,
								ipAddress, router.getId(), wifi_bssid);
						Toast.makeText(ActivityWifiConfig.this,
								getString(R.string.config_success),
								Toast.LENGTH_SHORT).show();
						saveWificonfig(router, wf);
					}

				}

			} catch (JSONException e) {
				handler.sendEmptyMessage(Config.WHAT_ERROR);
			}
		}
	}

	private void saveWificonfig(Router router, Wifi wf) {
		Gson gson = new Gson();
		int myid = 0;

		int index = -1;
		List<Router> routerlist = new ArrayList<Router>();
		SharedPreferences sp1 = getSharedPreferences("routerlist", 0);
		String routers = sp1.getString("gson", "");
		if (!routers.equals("")) {
			List<Router> gs = gson.fromJson(routers,
					new TypeToken<List<Router>>() {
					}.getType());
			myid = gs.size();
			for (int i = 0; i < gs.size(); i++){
				Router r = gs.get(i);
				if (r.getSSID().equals(router.getSSID()))
					index = i;
				routerlist.add(r);
			}
		}

		Router r = new Router();
		r.setId(index);
		r.setSSID(router.getSSID());
		r.setPassword(router_password);
		r.setMac(router.getMac());
		r.setCentermac(wf.getBSSID().replace("1a", "18"));
		r.setBroadcastIP(router_broadcast_ip);

		if (checkBox.isChecked() == true) {// 如果需要保存密码
			r.setRemember(1);
		} else {
			r.setRemember(0);
		}

		if (index > -1) {
			routerlist.set(index, r);
		} else {
			r.setId(myid);
			routerlist.add(r);
		}

		String str = gson.toJson(routerlist);
		SharedPreferences sp2 = getSharedPreferences("gs_routerlist", 0);
		SharedPreferences.Editor editor = sp2.edit();
		editor.clear();
		editor.putString("gson", str);
		editor.commit();
		finish();

	}

	private int tryCount = 0;

	private class ConfigCompleteRunnable implements Runnable {

		public ConfigCompleteRunnable() {
			tryCount = 0;
		}

		@Override
		public void run() {

			while (tryCount < Config.CONFIG_WIFI_TRY_COUNT) {

				if (router_broadcast_ip.contains("0.0")
						|| router_broadcast_ip.contains("192.168.4")) {
					setRouterBroadcastIp();
				}
				tryCount++;
				new Thread(new Runnable() {

					@Override
					public void run() {
						Command.commandRouter(Config.CMD_SYSINFO,
								router_broadcast_ip, handler,
								Config.WHAT_DEFAULT);

					}

				}).start();
				if (tryCount % 4 == 0) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							Command.checkWifiOnlineRemote(wifi_mac, handler,
									Config.WHAT_CHECK_WIFI_ONLINE);
						}

					}).start();
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
