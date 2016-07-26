package com.tianzun.control;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tele.control.R;
import com.tianzun.util.LampTimer;

public class MainActivity extends Activity implements OnClickListener {
TextView dengkong_iv,jiadian_iv,sign_iv;
TextView english,chinese;
private Gson gson = new Gson();

	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		dengkong_iv=(TextView)findViewById(R.id.zhaomingclick);
		jiadian_iv=(TextView)findViewById(R.id.jiadianclick);
		sign_iv=(TextView)findViewById(R.id.signclick);
		
		dengkong_iv.setOnClickListener(this);
		jiadian_iv.setOnClickListener(this);
		sign_iv.setOnClickListener(this);
		
		SharedPreferences sp = getSharedPreferences("autostart", Context.MODE_MULTI_PROCESS);
		String auto = sp.getString("auto", "");
		english=(TextView)findViewById(R.id.languageEnglish);
		english.setOnClickListener(this);
		chinese=(TextView)findViewById(R.id.languageChinese);
		chinese.setOnClickListener(this);
		if ("0".equals(auto)) {//默认开机自动启动定时 如果取消了则在这里每次运行APP时启动定时		
			sendAlarms();
			Log.i("NNN","===>首页开始启动定时");
			Toast.makeText(MainActivity.this,  getString(R.string.app_timer_startup), Toast.LENGTH_SHORT).show();
		}
		
		
	}
	
	private void switchLocale(String language){

	    // 本地语言设置  
	    //Locale myLocale = new Locale(language);  
	    Resources res = getResources();  
	    DisplayMetrics dm = res.getDisplayMetrics();  
	    Configuration conf = res.getConfiguration();
	    
	    //conf.locale = myLocale;
	    if(language.equals("en")){
	    	conf.locale = Locale.ENGLISH;
	    }else{
	    	conf.locale = Locale.CHINA;
	    }
	    res.updateConfiguration(conf, dm);  
	    Intent intent = new Intent(this, MainActivity.class);
	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    
	    this.finish();
	    overridePendingTransition(0,0);
	    startActivity(intent);  
	    overridePendingTransition(0,0);
	}
	
	@Override
	public void onClick(View v) {
		String device_id = MyApplication.sp_device_id.getString("device_id", "");

		switch (v.getId()) {
		case R.id.zhaomingclick:
			if(device_id.equals("")){
				Toast.makeText(MainActivity.this,getResources().getString(R.string.onenetsign_un_registed), Toast.LENGTH_SHORT).show();
				return;
			}
			startActivity(new Intent(MainActivity.this,ActivityLampList.class));
			break;
		case R.id.jiadianclick:
			if(device_id.equals("")){
				Toast.makeText(MainActivity.this,getResources().getString(R.string.onenetsign_un_registed), Toast.LENGTH_SHORT).show();
				return;
			}
			startActivity(new Intent(MainActivity.this,ActivityJDList.class));
			break;
		case R.id.signclick:
			if(!device_id.equals("")){
				Toast.makeText(MainActivity.this,getResources().getString(R.string.onenetsign_already_registed), Toast.LENGTH_SHORT).show();
			}else{
				Intent intent3 = new Intent(MainActivity.this,ActivitySign.class);
				startActivity(intent3);
			}
			break;
		case R.id.languageEnglish:
			switchLocale("en");
			break;
		case R.id.languageChinese:
			switchLocale("zh");
			break;
		default:
			break;
		}
		
	}
//	@Override
//	public void onDestroy(){
//		//System.exit(0);//退出App
//		super.onDestroy();
//	}

	
	
	@SuppressLint("InlinedApi")
	private void sendAlarms(){
		SharedPreferences sp = getSharedPreferences("gs_timerlist", Context.MODE_MULTI_PROCESS);
		String timers = sp.getString("gson", "");
		if (!timers.equals("")) {
			
			List<LampTimer> gs = gson.fromJson(timers,new TypeToken<List<LampTimer>>() {}.getType());
			
			for (int i = 0; i < gs.size(); i++) {
				LampTimer r = gs.get(i);

				String s=r.getTime();
				int pos=s.indexOf(":");
				String h=s.substring(0,pos);
				String m=s.substring(pos+1);
				
				setSysAlarm(MainActivity.this,Integer.valueOf(h),Integer.valueOf(m));
				
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
	 		am.setRepeating(AlarmManager.RTC_WAKEUP,firstTime, 24*60*60*1000, pi);	

	}

	
}
