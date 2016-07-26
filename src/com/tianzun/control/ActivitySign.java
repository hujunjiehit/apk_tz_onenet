package com.tianzun.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tele.control.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActivitySign extends Activity {

	private EditText ed_sign_name;
	private EditText ed_sign_info;
	private Button btn_dev_sign;
	
	Socket Httpsocket;
	InputStream HttpInStream;
	OutputStream HttpOutStream;
	
	private static int REGISTER_SUCESS = 0x123;
	private static int REGISTER_FAIL = 0x124;
	
	int HTTPserverPort = 80;
	byte[] Httppacket;
	byte[] HttpreadBuffer = new byte[1024]; // 接收数据的缓存池
	String HTTPserverIp = "183.230.40.33";
	String devKey = "rlDTdgLKiGuLOXfJmF0sIP6gHn0=";
	
	JSONObject httpcmd = new JSONObject();
	JSONObject cmdlocation = new JSONObject();
	JSONArray jsonArray = new JSONArray();
	//JSONArray jsonArray3 = JSONArray.fromObject( "['http']" );
	
	
	String sign_name;
	String sign_info;
	
	private Handler mHandle = new Handler(){
		
		public void handleMessage(Message msg) {
			if(msg.what == REGISTER_SUCESS){
				Toast.makeText(ActivitySign.this,getResources().getString(R.string.onenetsignsucess),
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	};
	
	OnClickListener onClickListener = new OnClickListener() {
		
	
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.onenet_dev_sign:
				if (TextUtils.isEmpty(ed_sign_name.getText())) {
					Toast.makeText(ActivitySign.this,getResources().getString(R.string.onenetsignname),Toast.LENGTH_SHORT).show();
					break;
				}
				sign_name = ed_sign_name.getText().toString();

				if (TextUtils.isEmpty(ed_sign_info.getText())) {
					Toast.makeText(ActivitySign.this, getResources().getString(R.string.onenetsigninfo),
							Toast.LENGTH_SHORT).show();
					break;
				}
				sign_info = ed_sign_info.getText().toString();
				new OnenetThread().start();
				break;

			default:
				break;
			}
		}
	};
	
	private void initUI() {
		// TODO Auto-generated method stub
		btn_dev_sign = (Button) findViewById(R.id.onenet_dev_sign);
		btn_dev_sign.setOnClickListener(onClickListener);
		ed_sign_info = (EditText) findViewById(R.id.ed_dev_info);
		ed_sign_name = (EditText) findViewById(R.id.ed_dev_name);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign);
		initUI();
	}

	public class OnenetThread extends Thread {

		@Override
		public void run() {
			try {
				Log.d("onenet", "hujunjie start run");
				
				// 开启连接请求
				Httpsocket = new Socket(HTTPserverIp, HTTPserverPort);
				Httpsocket.setSoTimeout(60 * 1000);
				HttpInStream = Httpsocket.getInputStream();
				HttpOutStream = Httpsocket.getOutputStream();
				
				
				cmdlocation.put("ele", 370000);
				cmdlocation.put("lat", 17.609997);
				cmdlocation.put("lon", 177.03403);
				httpcmd.put("title", sign_name);
				httpcmd.put("desc", sign_info);
				
				jsonArray.put(0,"http");
				httpcmd.put("tags" ,jsonArray);
				httpcmd.put("location", cmdlocation);
				httpcmd.put("private", true);
				httpcmd.put("protocol", "EDP");
				httpcmd.put("interval", 60);
				
				String HttpMsg = "POST /devices HTTP/1.1\r\napi-key: " + devKey + "\r\nHost:api.heclouds.com\r\nContent-Length:" + httpcmd.toString().length() + "\r\n\n" +httpcmd.toString();
				HttpOutStream.write(HttpMsg.getBytes());
				
				Log.i("onenet", "[connect]packet :" + HttpMsg.toString());
				Log.i("onenet", "[connect]packet size:" + httpcmd.toString().length());
				Thread.sleep(500);
				// 接收服务器的连接响应
				int readSize = HttpInStream.read(HttpreadBuffer);
				Log.i("onenet", "reade size = " + readSize);
				if (readSize > 0) {
					String result = new String(HttpreadBuffer);
					if(result.contains("HTTP/1.1 200 OK")){
						Log.i("onenet","return success");
						String device_id = getDeviceId(result);
						Log.i("onenet","device_id = " + device_id);
						MyApplication.sp_device_id.edit().putString("device_id", device_id).commit();
						MyApplication.device_id = device_id;
						SocketManager.getInstance().runConnect();
						//start the heart beat service
						Intent serviceIntent = new Intent("HeartbeatService");  
						startService(serviceIntent);  
						mHandle.sendEmptyMessage(REGISTER_SUCESS);
					}
				} else {
					
				}

			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // 设置超时时长为一分钟
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		private String getDeviceId(String result) {
			// TODO Auto-generated method stub
			String device_id = "";
			Log.i("onenet","result:" + result);
			if(result.contains("device_id")){
				device_id = result.split("device_id\":\"")[1].split("\"")[0];
			}
			return device_id;
		}

	}
}
