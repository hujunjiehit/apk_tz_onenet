package com.tianzun.control;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import com.tianzun.control.ActivityTest.BroadCastUdp;
import com.tianzun.util.Config;
import com.tianzun.wifi.WifiAdmin;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class MyApplication extends Application{

	public static SharedPreferences sp_device_id;
	public static String device_id;
	public static String desDevId;
	public static MyApplication mApplication;
	private static final int MAX_DATA_PACKET_LENGTH = 40;
	private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];
	
	private WifiManager wifiManager;
	private String broadcast_ip = "";
	
	public static SocketManager mSocketManager;
	
	public static final String ModuleSetting = "MyPrefsFile";
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		Log.d("onenet", "applicatin oncreate");
		sp_device_id = getSharedPreferences("sp_device_id", Activity.MODE_PRIVATE);
		device_id = sp_device_id.getString("device_id","");
		desDevId = sp_device_id.getString("desDevId","");
		
		Log.d("onenet", "device_id = " + device_id);
		Log.d("onenet", "desDevId = " + desDevId);
		
		mApplication = this;
		
		if(get_configstatus() && desDevId.equals("")){
			getDesDevId();
		}

		//启动连接请求
		if(!device_id.equals("")){
			mSocketManager = SocketManager.getInstance();
			mSocketManager.runConnect();
			//start the heart beat service
			Intent serviceIntent = new Intent("HeartbeatService");  
			startService(serviceIntent);  
		}
	}
	
	public void getDesDevId() {
		// TODO Auto-generated method stub
		String ModuleBssid = get_preference();
		Log.d("onenet", "getDesDevId  ModuleBssid = " + ModuleBssid);
		new BroadCastUdp(ModuleBssid).start();
	}

	public Boolean get_configstatus() {
		SharedPreferences settings = getSharedPreferences(ModuleSetting, 0);
		Boolean isconfig = settings.getBoolean("isconfig", false);
		return isconfig;
	}
	
	// get preference
	public String get_preference() {
		// TODO Auto-generated method stub
		SharedPreferences settings = getSharedPreferences(ModuleSetting, 0);
		String ModuleBssid;
		ModuleBssid = settings.getString("BSSID", "null");
		return ModuleBssid;
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
				
				//udpSocket = new DatagramSocket(Config.BROADCAST_PORT_DEFAULT);
				if(udpSocket == null){
					udpSocket = new DatagramSocket(null);
					udpSocket.setReuseAddress(true);
					udpSocket.bind(new InetSocketAddress(Config.BROADCAST_PORT_DEFAULT));
				}

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
				Log.e("onenet", e.toString());
			}
			

			try {
				udpSocket.send(dataPacket);
				sleep(500);
			} catch (Exception e) {
				Log.e("onenet", e.toString());
			}

			
			while(true){
				try {
					udpSocket.receive(recvPacket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("test", e.toString());
				}
				
				String ipaddr = recvPacket.getAddress().toString().trim();
				ipaddr = ipaddr.substring(1, ipaddr.length());
				
				Log.i("onenet", "recvPacket.getAddress():"+ipaddr);
				Log.i("onenet", "getLocalHostIp2:"+getLocalHostIp2().toString().trim());
				
				Log.i("onenet", "result:"+ipaddr.equals(getLocalHostIp2().toString().trim()));
				
				if(!ipaddr.equals(getLocalHostIp2().toString().trim())){
					Log.i("onenet", "close the socket");
					String rusult = new String(recvPacket.getData(),0,recvPacket.getLength()).trim();
					Log.i("onenet", "dresult data:"+ rusult);
					desDevId = rusult;
					sp_device_id.edit().putString("desDevId",rusult).commit();
					udpSocket.close();
					break;
				}
			}
		}
	}
	
	private String getBroadcastIp() {
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		String routerIp = WifiAdmin.convertToIp(wifiInfo.getIpAddress());

		broadcast_ip = routerIp.substring(0, routerIp.lastIndexOf("."))
				+ ".255";
		return broadcast_ip;
	}
	
	public String getLocalHostIp2(){
		WifiManager wifiMan = (WifiManager) getSystemService(ActivityTest.WIFI_SERVICE);  
		WifiInfo info = wifiMan.getConnectionInfo();  
		String mac = info.getMacAddress();// 获得本机的MAC地址  
		String ssid = info.getSSID();// 获得本机所链接的WIFI名称  
		  
		int ipAddress = info.getIpAddress();  
		String ipString = "";// 本机在WIFI状态下路由分配给的IP地址  
		if (ipAddress != 0) {  
		       ipString = ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."   
		        + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));  
		}  
		return ipString;
	}

}
