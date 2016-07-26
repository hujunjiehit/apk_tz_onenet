package com.tianzun.wifi;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import android.net.wifi.ScanResult;
public class WifiScanResultListCreater {
	

	private static final String PREFIX_FILTER = "ESP_";
	private static boolean isESPDevice(String SSID){
		if(SSID.length()!=10)
			return false;
		for(int i=0;i<PREFIX_FILTER.length();i++){
			if(i>=SSID.length()||SSID.charAt(i)!=PREFIX_FILTER.charAt(i))
				return false;
		}
		return true;
	}
	
	
	public static List<WifiScanResult> createWifiScanResultList(
			List<ScanResult> scanResultList, boolean isEspDevice) {
		List<WifiScanResult> returnList = new CopyOnWriteArrayList<WifiScanResult>();
		if(scanResultList==null){
		}
		for(int i=0;i<scanResultList.size();i++){
			ScanResult elementSrc = scanResultList.get(i);
			boolean resultIsEspDevice = isESPDevice(elementSrc.SSID);
			if(isEspDevice&&!resultIsEspDevice
					||!isEspDevice&&resultIsEspDevice)
				continue;
			WifiScanResult elementDest = new WifiScanResult(elementSrc);
			returnList.add(elementDest);
		}
		return returnList;
	}
}
