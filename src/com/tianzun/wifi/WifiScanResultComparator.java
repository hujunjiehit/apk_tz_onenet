package com.tianzun.wifi;
import java.util.Comparator;

public class WifiScanResultComparator implements Comparator<WifiScanResult> {

	private static WifiScanResultComparator singleton = new WifiScanResultComparator();

	private WifiScanResultComparator() {
	}
	public static WifiScanResultComparator getInstance(){
		return singleton;
	}

	@Override
	public int compare(WifiScanResult lhs, WifiScanResult rhs) {
		int lValue = lhs.mScanResult.level;
		int rValue = rhs.mScanResult.level;
		if (lValue < rValue)
			return 1;
		else if (lValue == rValue)
			return 0;
		else
			return -1;
	}
}
