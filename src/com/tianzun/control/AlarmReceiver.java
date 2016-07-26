package com.tianzun.control;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tianzun.util.CheckUtil;
import com.tianzun.util.Command;
import com.tianzun.util.Config;
import com.tianzun.util.DataUtil;
import com.tianzun.util.LampTimer;
import com.tianzun.util.Router;
import com.tianzun.util.Wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
	    private int mHour;
	    private int mMinute;
	    private int day;
	    
	    private Gson gson = new Gson();
	    	   
	
	    static final String action_boot="android.intent.action.BOOT_COMPLETED";
	    private Context context = null;

	@Override
	public void onReceive(Context context, Intent arg1) {

	      Calendar c = Calendar.getInstance();
	      c.setTimeZone(TimeZone.getTimeZone("GMT+8")); 
	      day= c.get(Calendar.DAY_OF_WEEK)-1;if(day==0)day=7;
	      mHour = c.get(Calendar.HOUR_OF_DAY);
	      mMinute = c.get(Calendar.MINUTE);	
	      String hourmunits=new StringBuilder().append(mHour < 10 ? "0"+mHour : mHour).append(":").append((mMinute < 10) ? "0" + mMinute : mMinute).toString();
	      this.context = context;
	     
	    
	  	SharedPreferences sp1 = context.getSharedPreferences("gs_timerlist", Context.MODE_MULTI_PROCESS);
		String timers = sp1.getString("gson", "");
		 Log.i("NNND","===>timers:"+timers);
		if (!timers.equals("")) {			
			List<LampTimer> gs = gson.fromJson(timers,new TypeToken<List<LampTimer>>() {}.getType());
			
			for (int i = 0; i < gs.size(); i++) {
				LampTimer r = gs.get(i);
				if(r.getTime().equals(hourmunits)){
					
					String week=r.getDay();
					if(!"0".equals(week) && week.indexOf(String.valueOf(day)) < 0){
						break;
					}					
					cmd(r.getId()-100, r.getOp(),r.getIp(),r.getWifi());		
					
				}
			}
		}			
	}

	private Router findRouterById(int id){
		return CheckUtil.findRouterById(context,id);
		
	}

	public void cmd(int id,int op,final String ip, final Wifi wifi){
		byte bid=Byte.valueOf(Integer.toHexString(id));
		int bop=0;
		if(op==1){
			bop=0x11;
		}else if(op==0){
			bop=0x21;
		}
		final byte[] code={0x01,0x02,0x03,bid,0x02,(byte) bop};
		new Thread(new Runnable(){			
			@Override
			public void run() {
				if(CheckUtil.isLocal(context, wifi))
				{//局域网直接广播 

					DatagramSocket socket = null;
					DatagramPacket packet = null;
					try {
						byte[] data =code;
						socket = new DatagramSocket();
						socket.setBroadcast(true); 
						packet = new DatagramPacket(data, data.length,InetAddress.getByName(ip),Config.BROADCAST_PORT);
						socket.send(packet);
						socket.close();
					}catch (Exception ex){
					} finally {
						if (socket != null) {
							socket.close();
						}
					}							
				}else{//远程
					 String sixteendata = DataUtil.bytesToHexString(code);
					 Router centerRouter = findRouterById(wifi.getRouterid());
					 if(centerRouter != null && centerRouter.getCentermac() != null){
						 Command.commandRemote(centerRouter.getCentermac(), sixteendata, true,null,Config.WHAT_DEFAULT,2);
					 }
				}
			}
			
		}).start();
	}
	

}