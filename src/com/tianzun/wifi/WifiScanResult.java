package com.tianzun.wifi;

import com.tianzun.util.Config;

import android.net.wifi.ScanResult;

public class WifiScanResult {

	
	protected ScanResult mScanResult;
	
	private int missTime;
	
	WifiScanResult(ScanResult scanResult){
		this.mScanResult = scanResult;
	}
	public void missOnce(){
		missTime++;
	}
	public int getMissTime(){
		return missTime;
	}
	public void clearMissTime(){
		missTime = 0;
	}
	public void setScanResult(ScanResult scanResult){
		this.mScanResult = scanResult;
	}
	public ScanResult getScanResult(){
		return mScanResult;
	}
	public Config.WifiCipherType getWifiCipherType(){
		if(mScanResult.capabilities.contains("WEP"))
			return Config.WifiCipherType.WIFICIPHER_WEP;
		else if(mScanResult.capabilities.contains("PSK"))
			return Config.WifiCipherType.WIFICIPHER_WPA;
		return Config.WifiCipherType.WIFICIPHER_NOPASS;
	}
}
