package com.tianzun.util;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CheckUtil {
	private static final String TAG = "CheckUtil";
	
	public static boolean isEditTextBlank(EditText et)
	{
		boolean flag = true;
		if(et==null)
		{
			flag = true;
		}else if("".equals(et.getText().toString().trim()))
		{
			flag = true;
		}else if(!"".equals(et.getText().toString().trim()))
		{
			flag = false;
		}
		
		return flag;
	}
	
	
	public static boolean isBlank(String str)
	{
		boolean flag = true;
		if(str==null)
		{
			flag = true;
		}else if("".equals(str.trim()))
		{
			flag = true;
		}else if(!"".equals(str.trim()))
		{
			flag = false;
		}
		
		return flag;
	}
	
	public static Router findRouterById(Context context, int routerid){
		Gson gson = new Gson();
		int myid=0;
		
		int index=-1;
		SharedPreferences sp1 = context.getSharedPreferences("gs_routerlist", 0);
		String routers = sp1.getString("gson", "");
		if (!routers.equals("")) {
			
			List<Router> gs = gson.fromJson(routers,new TypeToken<List<Router>>() {}.getType());
			myid=gs.size();
			if(routerid >=0 && routerid< myid){
				return gs.get(routerid);
			}
			/*for (int i = 0; i < gs.size(); i++) {
				Router r = gs.get(i);
				if(r.getSSID().equals(ssid))return gs.get(i);
				//routerlist.add(r);
			}*/
		}
		
		return null;
		
		
		
	}
	
	/**
	 * 当知道当前的选中的wifi时
	 * @param context
	 * @param wifi
	 * @param handler
	 * @param what
	 */
	public static boolean isLocal(Context context,Wifi wifi)
	{
		if(checkWifiStatus(context))
		{
			if(wifi==null){
				return false;
			}
			//WIFI状态继续判断是否为配置的哪个路由器环境
			Router router = findRouterById(context,wifi.getRouterid());
			if(router == null){
				Log.i(TAG, "WIFI检测远程网络设备是否在线!router=null");
				return false;
			}
			WifiManager wifiManager = (WifiManager)context.getSystemService(context.WIFI_SERVICE); 
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			if(router.getMac().equals(wifiInfo.getBSSID()))
			{
				Log.i(TAG, "WIFI检测本地网络设备是否在线!");
				return true;
			}else
			{
				Log.i(TAG, "WIFI检测远程网络设备是否在线!");
				return false;
			}
		}else
		{
			Log.i(TAG, "3G检测远程网络设备是否在线!");
			//3G环境状态用远程判断

			return false;
		}

	}
	/**
	 * 检查当前是否为WIFI状态
	 * @param context
	 * @return
	 */
	public static boolean checkWifiStatus(Context context)
	{
		ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectMgr.getActiveNetworkInfo();
		if(info ==null)
		{
			//没有网络状态
			return false;
		}else
		{
			if(info.getType() == ConnectivityManager.TYPE_WIFI)
			{
				//WIFI网络状态
				return true;
			}else
			{
				//3G网络状态
				return false;
			}
		}
	}
	
}
