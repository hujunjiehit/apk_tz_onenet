package com.tianzun.wifi;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.tianzun.util.Config;

import android.net.wifi.WifiInfo;

public class WifiScanResultManager {
	
	private List<WifiScanResult> mList = new CopyOnWriteArrayList<WifiScanResult>();
	
	private WifiInfo mWifiInfo;
	
	private static WifiScanResultManager instance = new WifiScanResultManager();
	
	private static WifiScanResultComparator comparator = WifiScanResultComparator.getInstance();
	
	private WifiScanResultManager(){
	}
	public static WifiScanResultManager getInstance(){
		return instance;
	}
	
	public void setWifiInfo(WifiInfo wifiInfo){
		this.mWifiInfo = wifiInfo;
	}
	
	public WifiInfo getWifiInfo(){
		return mWifiInfo;
	}
	
	public synchronized List<WifiScanResult> getWifiScanResultList(){
		return mList;
	}
	

	public synchronized void sortRssiDescend(){

		List<WifiScanResult> tempList = new ArrayList<WifiScanResult>(mList);
		Collections.sort(tempList, comparator);
		mList.clear();
		mList.addAll(tempList);
	}

	public synchronized void checkAddWifiScanResultList(List<WifiScanResult> list){
		for(WifiScanResult element:mList){
			boolean isContained = false;
			for(WifiScanResult elementOther:list){
				if((elementOther.getScanResult().BSSID).
						equals(element.getScanResult().BSSID)){
					isContained = true;
					element.setScanResult(elementOther.getScanResult());
					element.clearMissTime();
					list.remove(elementOther);
					break;
				}
			}
			if(!isContained){
				element.missOnce();
				if(element.getMissTime() >= Config.AP_MAX_MISS_TIME){
					mList.remove(element);
				}
			}
		}
		for(WifiScanResult elementOther:list){
			mList.add(elementOther);
			elementOther.clearMissTime();
		}
	}
	
}
