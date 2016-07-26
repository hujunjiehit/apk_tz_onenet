package com.tianzun.wifi;

import java.util.ArrayList;
import java.util.List;

import com.tianzun.util.Config;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

public class WifiAdmin {
	
	private WifiManager mWifiManager;
	private ConnectivityManager mConnectivityManager;
	private WifiInfo mWifiInfo;
	private List<WifiConfiguration> mWifiConfigurations;
	private WifiLock mWifiLock;

	private WifiApManager wifiApManager;
	
	private static final String TAG = "WifiAdmin";

	private Context mContext;
	private static WifiAdmin instance;
	
	private WifiAdmin(){
	}
	
	public static void init(Context context){
		instance = new WifiAdmin();
		instance.mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		instance.mConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		instance.mWifiInfo = instance.mWifiManager.getConnectionInfo();

		instance.mContext = context;

		instance.wifiApManager = new WifiApManager(context);
	}
	
	public static WifiAdmin getInstance(){
		if(instance==null){
			throw new RuntimeException("the context of the WifiAdmin hasn't been initiated.");
		}
		return instance;
	}
	
	public void openHot(String ssid,String password){
		WifiConfiguration config = wifiApManager.getConfig(ssid,
		password, SoftApPasswordType.WPA);
		wifiApManager.closeSoftAp();
		wifiApManager.setWifiApEnabled(config, true);
		
	}
	
	public boolean openWifi() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
			for (int i = 0; i < Config.WIFI_RETRY_TIMES; i++) {
				if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
					return true;
				}
				try {
					Thread.sleep(Config.WIFI_OPEN_SLEEP_MILLISECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			return true;
		}
		return false;
	}

	public boolean closeWifi() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
			for (int i = 0; i < Config.WIFI_RETRY_TIMES; i++) {
				if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
					return true;
				}
				try {
					Thread.sleep(Config.WIFI_CLOSE_SLEEP_MILLISECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			return true;
		}
		return false;
	}

	public int getWifiState() {
		return mWifiManager.getWifiState();
	}
	
	public boolean isWifiEnabled() {
		return mWifiManager.isWifiEnabled();
	}

	public void acquireWifiLock() {
		mWifiLock.acquire();
	}

	public void releaseWifiLock() {
		if (mWifiLock.isHeld()) {
			mWifiLock.release();
		}
	}

	public void createWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("test");
	}

	public List<WifiConfiguration> getConfigurationList() {
		return mWifiConfigurations;
	}

	public void connetionConfiguration(int index) {
		if (index > mWifiConfigurations.size()) {
			return;
		}
		mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId,
				true);
	}

	private WifiConfiguration createWifiInfo(String SSID, String password,
			Config.WifiCipherType type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";
		switch (type) {
		case WIFICIPHER_NOPASS:
			config.wepKeys[0] = "\"" + "\"";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
			break;
		case WIFICIPHER_WEP:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
			if (password.length() != 0) {
				int length = password.length();
				if ((length == 10 || length == 26 || length == 58)
						&& password.matches("[0-9A-Fa-f]*")) {
					config.wepKeys[0] = password;
				} else {
					config.wepKeys[0] = '"' + password + '"';
				}
			}
			break;
		case WIFICIPHER_WPA:
			config.preSharedKey = "\"" + password + "\"";
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			break;
		default:
			return null;
		}
		return config;
	}

	public void disconnectSyn() {
		mWifiManager.disconnect();
		while(isConnect()){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	

	private int indexOfSSID(String SSID){
		boolean isExist = false;
		String ssid = "\"" + SSID + "\"";
		int index;
		mWifiConfigurations = mWifiManager.getConfiguredNetworks();
		
		for (index = 0;mWifiConfigurations != null && index<mWifiConfigurations.size();index++){
			WifiConfiguration wifiConfiguration = mWifiConfigurations.get(index);
			if (wifiConfiguration.SSID != null
					&& wifiConfiguration.SSID.equals(ssid)) {
				isExist = true;
				break;
			}
		}
		if(isExist)
			return mWifiConfigurations.get(index).networkId;
		else
			return -1;
	}
	
	
	
	public boolean connect( String SSID, String password, Config.WifiCipherType type) {

		if (mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED)
			return false;
		int netId = indexOfSSID(SSID);
		if(netId!=-1){
			boolean	bRemove = mWifiManager.removeNetwork(netId);
			if(!bRemove)
				Log.e(TAG, "old netId " + netId + " is removed " + "failed");
			else
				Log.d(TAG, "old netId " + netId + " is removed " + "succeed");
		}
		
		WifiConfiguration wifiConfig = createWifiInfo(SSID, password, type);
		if (wifiConfig == null)
			return false;
		
		netId = mWifiManager.addNetwork(wifiConfig);
		mWifiManager.updateNetwork(wifiConfig);
		return mWifiManager.enableNetwork(netId, true);
	}

	public boolean connect(String SSID, boolean isNoPassword) {
		int netId = indexOfSSID(SSID);
		if (netId==-1) {
			if (isNoPassword) {
				WifiConfiguration wifiConfig = createWifiInfo(SSID, "",
						Config.WifiCipherType.WIFICIPHER_NOPASS);
				if (wifiConfig == null) {
					return false;
				}
				netId = mWifiManager.addNetwork(wifiConfig);
				return mWifiManager.enableNetwork(netId, true);
			}
			else {
				return false;
			}
		}
		else{
			boolean result =  mWifiManager.enableNetwork(netId, true);
			return result;
		}
	}

	public boolean isConnect() {
		mConnectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = mConnectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		boolean result = mWifi.isConnected();
		return result;
	}


	public boolean isEnabled(String SSID) {
		String ssid = "\"" + SSID + "\"";

		for (WifiConfiguration wifiConfiguration : mWifiConfigurations) {
			if (wifiConfiguration.SSID != null
					&& wifiConfiguration.SSID.equals(ssid)) {
				if (wifiConfiguration.status == WifiConfiguration.Status.DISABLED)
					return true;
				else
					return false;
			}
		}
		return false;
	}
	

	public WifiInfo getConnectionInfo(){
		return mWifiManager.getConnectionInfo();
	}
	

	public synchronized List<ScanResult> scan() {
		List<ScanResult>mWifiList = null;
		List<ScanResult>wifiListExceptEmpty = new ArrayList<ScanResult>();
		mWifiManager.startScan();
		for (int i = 0; i < Config.WIFI_RETRY_TIMES; i++) {
			mWifiList = mWifiManager.getScanResults();
			if (mWifiList != null && !mWifiList.isEmpty()){
				for(ScanResult scanResult: mWifiList){
					if(!scanResult.SSID.equals("")){
						wifiListExceptEmpty.add(scanResult);
					}
				}
				break;
			}
			else
				try {
					Thread.sleep(Config.WIFI_SCAN_SLEEP_MILLISECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
		if (wifiListExceptEmpty != null && !wifiListExceptEmpty.isEmpty()) {
			return wifiListExceptEmpty;
		} else {
			return null;
		}
	}

	public String getSSID() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
	}

	public String getMacAddress() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	public String getBSSID() {
		mWifiInfo = mWifiManager.getConnectionInfo();
		return (mWifiInfo == null) ? null : mWifiInfo.getBSSID();
	}

	public int getIpAddress() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	public int getNetWorkId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	public String getWifiInfo() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}


	
	public static String convertToIp(int ipAddress)
	{
		return ((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."+(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));  
	}

}