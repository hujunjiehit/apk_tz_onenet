package com.tianzun.control;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;

import com.tele.control.R;
import com.tele.control.R.id;
import com.tianzun.iot.activity.SmartconfigActivity;
import com.tianzun.iot.activity.SmartconfigActivity.BroadCastUdp;
import com.tianzun.util.Config;
import com.tianzun.wifi.WifiAdmin;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityTest extends Activity {
	
	public static final String ModuleSetting = "MyPrefsFile";
	private static final int MAX_DATA_PACKET_LENGTH = 40;
	private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];

	private WifiManager wifiManager;
	private String broadcast_ip = "";

	private Button btn_lighton;
	private Button btn_lightoff;
	private Button btn_lightconfig;
	private Button btn_lightTI;
	private Button btn_lightTD;
	private Button btn_lightLI;
	private Button btn_lightLD;
	private Button btn_lightred;
	private Button btn_lightblue;
	private Button btn_lightgreen;
	private Button btn_lightstatus;
	private Button btn_modulerestart;
	private Button btn_sendudp;
	private Button btn_release;


	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.btn_lighton:

					Toast.makeText(ActivityTest.this, "发送开灯请求",
							Toast.LENGTH_SHORT).show();
					byte[] command1 = { 0x11, 0x01, 0x02, 0x03, (byte)0x01, 0x02,
							(byte)0x11 };
					Log.i("test", Arrays.toString(command1));
					Config.ModuleThread.sendMsg(command1);
				
				break;
			case R.id.btn_lightoff:

					Toast.makeText(ActivityTest.this, "发送关灯请求",
							Toast.LENGTH_SHORT).show();
					byte[] command2 = { 0x11, 0x01, 0x02, 0x03, (byte)0x01, 0x02,
							(byte)0x21 };
					Log.i("test", Arrays.toString(command2));
					Config.ModuleThread.sendMsg(command2);
				
				break;
			
			case R.id.btn_light:

				Toast.makeText(ActivityTest.this, "发送匹配请求",
						Toast.LENGTH_SHORT).show();
				byte[] command3 = { 0x11, 0x01, 0x02, 0x03, (byte)0x01, 0x01,
						(byte)0x21 };
				Log.i("test", Arrays.toString(command3));
				Config.ModuleThread.sendMsg(command3);
			
			break;
			
			case R.id.btn_lightLI:
				Toast.makeText(ActivityTest.this, "亮度增加", Toast.LENGTH_SHORT).show();
				byte[] command4 = { 0x11, 0x01, 0x02, 0x03, (byte)0x01, 0x02,(byte)0x31 };
				Log.i("test", Arrays.toString(command4));
				Config.ModuleThread.sendMsg(command4);
				break;
				
			case R.id.btn_lightLD:
				Toast.makeText(ActivityTest.this, "亮度减少", Toast.LENGTH_SHORT).show();
				byte[] command5 = { 0x11, 0x01, 0x02, 0x03, (byte)0x01, 0x02,(byte)0x41 };
				Log.i("test", Arrays.toString(command5));
				Config.ModuleThread.sendMsg(command5);
				break;

			case R.id.btn_lightTI:
				Toast.makeText(ActivityTest.this, "色温增加", Toast.LENGTH_SHORT).show();
				byte[] command6 = { 0x11, 0x01, 0x02, 0x03, (byte)0x01, 0x02,(byte)0x51 };
				Log.i("test", Arrays.toString(command6));
				Config.ModuleThread.sendMsg(command6);
				break;

			case R.id.btn_lightTD:
				Toast.makeText(ActivityTest.this, "色温减少", Toast.LENGTH_SHORT).show();
				byte[] command7 = { 0x11, 0x01, 0x02, 0x03, (byte)0x01, 0x02,(byte)0x61 };
				Log.i("test", Arrays.toString(command7));
				Config.ModuleThread.sendMsg(command7);
				break;

			case R.id.btn_lightred:
				Toast.makeText(ActivityTest.this, "红色", Toast.LENGTH_SHORT).show();
				byte[] command8 = { 0x11, 0x01, 0x02, 0x03, (byte)0x01, 0x02,(byte)0xa1 };
				Log.i("test", Arrays.toString(command8));
				Config.ModuleThread.sendMsg(command8);
				break;
				
			case R.id.btn_lightblue:
				Toast.makeText(ActivityTest.this, "蓝色", Toast.LENGTH_SHORT).show();
				byte[] command9 = { 0x11, 0x01, 0x02, 0x03, (byte)0x01, 0x02,(byte)0xc1 };
				Log.i("test", Arrays.toString(command9));
				Config.ModuleThread.sendMsg(command9);
				break;
				
			case R.id.btn_lightgreen:
				Toast.makeText(ActivityTest.this, "绿色", Toast.LENGTH_SHORT).show();
				byte[] command10 = { 0x11, 0x01, 0x02, 0x03, (byte)0x01, 0x02,(byte)0xb1 };
				Log.i("test", Arrays.toString(command10));
				Config.ModuleThread.sendMsg(command10);
				break;
				
			case R.id.btn_lightstatus:
				Toast.makeText(ActivityTest.this, "获得模块状态", Toast.LENGTH_SHORT).show();
				byte[] command11 = { 0x22, 0x01, 0x02, 0x03, (byte)0x01, 0x02,(byte)0xb1 };
				Log.i("test", Arrays.toString(command11));
				Config.ModuleThread.sendMsg(command11);
				break;
				
			case R.id.btn_modulerestart:
				Toast.makeText(ActivityTest.this, "模块重启", Toast.LENGTH_SHORT).show();
				byte[] command12 = { 0x33, 0x01, 0x02, 0x03, (byte)0x01, 0x02,(byte)0xb1 };
				Log.i("test", Arrays.toString(command12));
				Config.ModuleThread.sendMsg(command12);
				break;
				
			case R.id.btn_send_udp:
				if (get_configstatus() == false) {
					Toast.makeText(ActivityTest.this, "你还没进行模块的配对",Toast.LENGTH_SHORT).show();
				} else {
					if (Config.modulesocket == null) {
						String ModuleBssid = get_preference();
						new BroadCastUdp(ModuleBssid).start();
						Log.i("test", "发送广播消息");
						Toast.makeText(ActivityTest.this, "发送连接请求成功",Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(ActivityTest.this,"连接成功请返回上一级", Toast.LENGTH_SHORT).show();
					}
				}
				break;
				
			case R.id.btn_release:
				SharedPreferences settings = getSharedPreferences(ModuleSetting, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.clear();
				editor.commit();
				Toast.makeText(ActivityTest.this, "重置配置信息成功",
						Toast.LENGTH_SHORT).show();
				break;
			
			default:
				break;
			}
		}
	};

	private void initview() {
		// TODO Auto-generated method stub
		btn_lighton = (Button) findViewById(R.id.btn_lighton);
		btn_lightoff = (Button) findViewById(R.id.btn_lightoff);
		btn_lighton.setOnClickListener(onClickListener);
		btn_lightoff.setOnClickListener(onClickListener);
		btn_lightconfig = (Button) findViewById(R.id.btn_light);
		btn_lightconfig.setOnClickListener(onClickListener);
		btn_lightLD = (Button) findViewById(R.id.btn_lightLD);
		btn_lightLD.setOnClickListener(onClickListener);
		btn_lightLI = (Button) findViewById(R.id.btn_lightLI);
		btn_lightLI.setOnClickListener(onClickListener);
		btn_lightTD = (Button) findViewById(R.id.btn_lightTD);
		btn_lightTD.setOnClickListener(onClickListener);
		btn_lightTI = (Button) findViewById(R.id.btn_lightTI);
		btn_lightTI.setOnClickListener(onClickListener);
		btn_lightred = (Button) findViewById(R.id.btn_lightred);
		btn_lightred.setOnClickListener(onClickListener);
		btn_lightgreen =(Button) findViewById(R.id.btn_lightgreen);
		btn_lightgreen.setOnClickListener(onClickListener);
		btn_lightblue = (Button) findViewById(R.id.btn_lightblue);
		btn_lightblue.setOnClickListener(onClickListener);
		btn_lightstatus = (Button) findViewById(R.id.btn_lightstatus);
		btn_lightstatus.setOnClickListener(onClickListener);
		btn_modulerestart = (Button) findViewById(R.id.btn_modulerestart);
		btn_modulerestart.setOnClickListener(onClickListener);
		btn_sendudp = (Button) findViewById(R.id.btn_send_udp);
		btn_sendudp.setOnClickListener(onClickListener);
		btn_release = (Button) findViewById(R.id.btn_release);
		btn_release.setOnClickListener(onClickListener);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		initview();
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
		Boolean isconfig = settings.getBoolean("isconfig", false);
		String ModuleBssid;
		if (isconfig == true) {
			ModuleBssid = settings.getString("BSSID", "null");
		} else {
			ModuleBssid = "null";
		}
		return ModuleBssid;
	}

	private String getBroadcastIp() {
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
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
				Log.e("test", e.toString());
			}
			

			try {
				udpSocket.send(dataPacket);
				sleep(500);
			} catch (Exception e) {
				Log.e("test", e.toString());
			}

			
			while(true){
				try {
					udpSocket.receive(recvPacket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("test", e.toString());
				}
				
				Log.i("test", "get msg:"+recvPacket.getAddress());
				Log.i("test", "get msg:"+recvPacket.getPort());
				
				String ipaddr = recvPacket.getAddress().toString().trim();
				ipaddr = ipaddr.substring(1, ipaddr.length());
				Log.i("test", "recvPacket.getAddress():"+ipaddr.substring(1, ipaddr.length()));
				Log.i("test", "getLocalHostIp2:"+getLocalHostIp2().toString().trim());
				
				Log.i("test", "result:"+ipaddr.equals(getLocalHostIp2().toString().trim()));
				
				Log.i("test", "len1:"+ipaddr.length());
				Log.i("test", "len2:"+getLocalHostIp2().length());
				
				if(!ipaddr.equals(getLocalHostIp2().toString().trim())){
					Log.i("test", "close the socket");
					Log.i("test", "dresult data:"+ new String(recvPacket.getData(),0,recvPacket.getLength()).trim());
					udpSocket.close();
					break;
				}
			}
		}
	}
	
	public String getLocalHostIp2(){
		WifiManager wifiMan = (WifiManager) getSystemService(ActivityTest.WIFI_SERVICE);  
		WifiInfo info = wifiMan.getConnectionInfo();  
		String mac = info.getMacAddress();// 获得本机的MAC地址  
		String ssid = info.getSSID();// 获得本机所链接的WIFI名称  
		  
		int ipAddress = info.getIpAddress();  
		String ipString = "";// 本机在WIFI状态下路由分配给的IP地址  
		  
		// 获得IP地址的方法一：  
		if (ipAddress != 0) {  
		       ipString = ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."   
		        + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));  
		}  
		// 获得IP地址的方法二（反射的方法）：  
//		try {  
//		    Field field = info.getClass().getDeclaredField("mIpAddress");  
//		    field.setAccessible(true);  
//		    ipString = (String) field.get(info);  
//		    System.out.println("obj" + ipString);  
//		} catch (Exception e) {  
//		    // TODO Auto-generated catch block  
//		    e.printStackTrace();  
//		} 
		return ipString;
	}
}
