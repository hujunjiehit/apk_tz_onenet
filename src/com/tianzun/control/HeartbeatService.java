package com.tianzun.control;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class HeartbeatService extends Service implements Runnable{

	private Thread mThread;
	public int count = 0;
	private boolean isTip = true;  
	private SocketManager mSocketManager;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			if(count > 1){
				Log.i("onenet", "HeartbeatService offline");
				count = 1;
				if(isTip){
					//todo
					 Log.i("onenet", "HeartbeatService handle the error***********************");
					 ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);  
                     List<RunningTaskInfo> list = am.getRunningTasks(3);  
                     for (RunningTaskInfo info:list){  
                         if (info.topActivity.getPackageName().equals("com.tele.control")){
                        	 Log.i("onenet", "HeartbeatService app is running,send broadcast:com.tele.control.network.error");
                             Intent intent = new Intent("com.tele.control.network.error");  
                             intent.putExtra("msg", true);
                             sendBroadcast(intent);
                             break;  
                         }  
                     }  
					isTip = false;
				}
			}
			
			//send heart beat
			sendHeartBeatPackage();
			count += 1;
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}
	}
	
	private void sendHeartBeatPackage() {
		// TODO Auto-generated method stub
		Log.i("onenet", "HeartbeatService start to ping");
		mSocketManager.sendPingMsg();


		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(mSocketManager.getPingStatus()){  //ping sucess
			Log.i("onenet", "HeartbeatService ping sucess");
		     count = 0;  
	         isTip = true;  
		}else{ //ping failed
			Log.i("onenet", "HeartbeatService ping failed, isConnected = " + mSocketManager.isConnected());
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		Log.i("onenet", "HeartbeatService service start");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mThread = new Thread(this);
		mSocketManager = SocketManager.getInstance();
	    mThread.start();  
	    count = 0;  
		super.onStart(intent, startId);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	

}
