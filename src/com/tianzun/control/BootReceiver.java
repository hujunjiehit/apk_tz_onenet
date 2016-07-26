package com.tianzun.control;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.SystemClock;
//import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tianzun.util.LampTimer;

public class BootReceiver extends BroadcastReceiver {
    	   

    static final String action_boot="android.intent.action.BOOT_COMPLETED";
    private Gson gson = new Gson();


@SuppressLint("InlinedApi")
@Override
public void onReceive(Context context, Intent arg1) {	

	final ComponentName receiver = new ComponentName(context,BootReceiver.class);
	final PackageManager pm = context.getPackageManager();
	SharedPreferences sp = context.getSharedPreferences("autostart", Context.MODE_MULTI_PROCESS);
	String auto = sp.getString("auto", "");
	if ("0".equals(auto)) {
	pm.setComponentEnabledSetting(receiver,
	PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
	PackageManager.DONT_KILL_APP);
	return;
	}
	//Toast.makeText(context, "开机已经启动定时", Toast.LENGTH_SHORT).show();

	if (arg1.getAction().equals(action_boot)){ 
		sendAlarms(context);		
	}
	
}

@SuppressLint("InlinedApi")
private void sendAlarms(Context context){
	SharedPreferences sp = context.getSharedPreferences("gs_timerlist", Context.MODE_MULTI_PROCESS);
	String timers = sp.getString("gson", "");
	if (!timers.equals("")) {
		
		List<LampTimer> gs = gson.fromJson(timers,new TypeToken<List<LampTimer>>() {}.getType());
		
		for (int i = 0; i < gs.size(); i++) {
			LampTimer r = gs.get(i);

			String s=r.getTime();
			int pos=s.indexOf(":");
			String h=s.substring(0,pos);
			String m=s.substring(pos+1);			
			setSysAlarm(context,Integer.valueOf(h),Integer.valueOf(m));
			
		}
	}
}

private void setSysAlarm(Context context,int Hour, int Minute ) {
	
	long firstTime = SystemClock.elapsedRealtime(); 
	long systemTime = System.currentTimeMillis();

        Calendar calendar =Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.setTimeInMillis(System.currentTimeMillis());
	 	calendar.set(Calendar.MINUTE, Minute);
	 	calendar.set(Calendar.HOUR_OF_DAY, Hour);
	 	calendar.set(Calendar.SECOND, 0);
	 	calendar.set(Calendar.MILLISECOND, 0);	
	 	
	long selectTime = calendar.getTimeInMillis();  
	if(systemTime > selectTime) {  
	calendar.add(Calendar.DAY_OF_MONTH, 1);  
	selectTime = calendar.getTimeInMillis();  
	long time = selectTime - systemTime;  
	firstTime += time;
	}else{
		firstTime=selectTime;
	}
 
	
 		String key=Hour+":"+Minute;
		String settime=key.replace(":", ""); 					
		int timeorder=Integer.valueOf(settime);

 	 	Intent intent = new Intent(context,AlarmReceiver.class);
 	 	PendingIntent pi = PendingIntent.getBroadcast(context, timeorder, intent, 0);
 	 	AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
 		am.setRepeating(AlarmManager.RTC_WAKEUP,firstTime, 24*60*60*1000, pi); //24小时重复 		

}

}
