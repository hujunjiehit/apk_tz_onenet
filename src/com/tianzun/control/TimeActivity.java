package com.tianzun.control;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tele.control.R;
import com.tianzun.util.LampTimer;
import com.tianzun.util.Wifi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class TimeActivity extends Activity implements View.OnClickListener {
	
    private static final int SHOW_TIMEPICK = 2;
    private static final int TIME_DIALOG_ID = 3;
    
    private EditText showTime = null;
    private Button pickTime = null;
    private int mHour;
    private int mMinute;
    
    private CheckBox ch0,ch1,ch2,ch3,ch4,ch5,ch6,ch7,chOpen,chClose;
    private Button saveButton;
    
    private String lampname,ip;
    private int lampid;
    private TextView tv_lampname,back;
    
    private Gson gson = new Gson();
    private ListView listView;
    
    private Calendar c=null;
    
    private int op = 0;
    private String day ="";
    
    TimerAdapter adapter;
    List<LampTimer> gslist;
    AlarmManager am;
    
    TextView setauto;
    
    private Wifi wifi;
    
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settime);
		
		am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		
		Bundle bundle=getIntent().getExtras();
		lampname=bundle.getString("lampname");
		lampid=bundle.getInt("lampid");
		ip=bundle.getString("ip");		
		wifi = (Wifi)bundle.get("wifi");
        initializeViews();
        
        c = Calendar.getInstance();       
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);        
        showTime.setText(new StringBuilder().append(mHour < 10 ? "0"+mHour : mHour).append(":").append((mMinute < 10) ? "0" + mMinute : mMinute)); 
	
}

    @SuppressLint("InlinedApi")
	private void initializeViews(){
    	setauto=(TextView)findViewById(R.id.setauto);
    	setauto.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				//该功能可以让用户选择非默认开机即启动定时，而是每天必须打开一次APP后才启动定时，客户确定不需要该功能
				//startActivity(new Intent(TimeActivity.this,AutoActivity.class));				
			}
		});

        showTime = (EditText)findViewById(R.id.showtime);
        pickTime = (Button)findViewById(R.id.picktime);       
        pickTime.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View v) {
               Message msg = new Message(); 
               if (pickTime.equals((Button) v)) {  
                  msg.what = TimeActivity.SHOW_TIMEPICK;  
               }  
               TimeActivity.this.dateandtimeHandler.sendMessage(msg); 
            }
        });
        
        tv_lampname=(TextView)findViewById(R.id.lampname);
        tv_lampname.setText(lampname);
        
        ch0=(CheckBox)findViewById(R.id.checkBox1);
        ch1=(CheckBox)findViewById(R.id.checkBox2);
        ch2=(CheckBox)findViewById(R.id.checkBox3);
        ch3=(CheckBox)findViewById(R.id.checkBox4);
        ch4=(CheckBox)findViewById(R.id.checkBox5);
        ch5=(CheckBox)findViewById(R.id.checkBox6);
        ch6=(CheckBox)findViewById(R.id.checkBox7);
        ch7=(CheckBox)findViewById(R.id.checkBox8);
        
        chOpen=(CheckBox)findViewById(R.id.checkBox9);
        chClose=(CheckBox)findViewById(R.id.checkBox10);
        saveButton=(Button)findViewById(R.id.savetime);
		back=(TextView)findViewById(R.id.back);

        ch0.setOnClickListener(this);
        ch1.setOnClickListener(this);
        ch2.setOnClickListener(this);
        ch3.setOnClickListener(this);
        ch4.setOnClickListener(this);
        ch5.setOnClickListener(this);
        ch6.setOnClickListener(this);
        ch7.setOnClickListener(this);
        chOpen.setOnClickListener(this);
        chClose.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        back.setOnClickListener(this);
               
        listView=(ListView)findViewById(R.id.listView1);
        
    	SharedPreferences sp0 = getSharedPreferences("gs_timerlist", Context.MODE_MULTI_PROCESS);
    	String timers0 = sp0.getString("gson", "");
    	if (!timers0.equals("")) {
    		
    		gslist = gson.fromJson(timers0,new TypeToken<List<LampTimer>>() {}.getType());
    		adapter=new TimerAdapter(TimeActivity.this, gslist,lampid);
    		listView.setAdapter(adapter);

    	}
    }
    
    
   /**
    * 更新时间显示
    */
   private void updateTimeDisplay(){
      showTime.setText(new StringBuilder().append(mHour < 10 ? "0"+mHour : mHour).append(":")
              .append((mMinute < 10) ? "0" + mMinute : mMinute)); 
   }
    
   /**
    * 时间控件事件
    */
   private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        
       @Override
       public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
           mHour = hourOfDay;
           mMinute = minute;
            
           updateTimeDisplay();
       }
   };
   @Override 
   protected Dialog onCreateDialog(int id) {  
      switch (id) {  
      case TIME_DIALOG_ID:
          return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute, true);
      }
           
      return null;  
   }  
   @Override 
   protected void onPrepareDialog(int id, Dialog dialog) {  
      switch (id) {  
      case TIME_DIALOG_ID:
          ((TimePickerDialog) dialog).updateTime(mHour, mMinute);
          break;
      }
   }  
  
   /** 
    * 处理日期和时间控件的Handler 
    */ 
   @SuppressLint("HandlerLeak")
Handler dateandtimeHandler = new Handler() {
  
    @SuppressWarnings("deprecation")
	@Override 
      public void handleMessage(Message msg) {  
          switch (msg.what) {  
          case TimeActivity.SHOW_TIMEPICK:
              showDialog(TIME_DIALOG_ID);
              break;
          }  
      }  
  
   };

@Override
public void onClick(View v) {
switch(v.getId()){
case R.id.checkBox1://选择每天
	if(ch0.isChecked()){
		ch1.setChecked(true);ch2.setChecked(true);ch3.setChecked(true);ch4.setChecked(true);ch5.setChecked(true);
		ch6.setChecked(true);ch7.setChecked(true);
	}else{
		ch1.setChecked(false);ch2.setChecked(false);ch3.setChecked(false);ch4.setChecked(false);ch5.setChecked(false);
		ch6.setChecked(false);ch7.setChecked(false);	
	}
	break;
case R.id.checkBox2://选择周一
	if(!ch1.isChecked()){
		ch0.setChecked(false);
	}else if(ch2.isChecked() && ch3.isChecked() && ch4.isChecked() && ch5.isChecked() && ch6.isChecked() && ch7.isChecked()){
		ch0.setChecked(true);
	}
	break;
case R.id.checkBox3://选择周二
	if(!ch2.isChecked()){
		ch0.setChecked(false);
	}else if(ch1.isChecked() && ch3.isChecked() && ch4.isChecked() && ch5.isChecked() && ch6.isChecked() && ch7.isChecked()){
		ch0.setChecked(true);
	}
	break;
case R.id.checkBox4://选择周三
	if(!ch3.isChecked()){
		ch0.setChecked(false);
	}else if(ch1.isChecked() && ch2.isChecked() && ch4.isChecked() && ch5.isChecked() && ch6.isChecked() && ch7.isChecked()){
		ch0.setChecked(true);
	}
	break;
case R.id.checkBox5://选择周四
	if(!ch4.isChecked()){
		ch0.setChecked(false);
	}else if(ch1.isChecked() && ch2.isChecked() && ch3.isChecked() && ch5.isChecked() && ch6.isChecked() && ch7.isChecked()){
		ch0.setChecked(true);
	}
	break;
case R.id.checkBox6://选择周五
	if(!ch5.isChecked()){
		ch0.setChecked(false);
	}else if(ch1.isChecked() && ch2.isChecked() && ch3.isChecked() && ch4.isChecked() && ch6.isChecked() && ch7.isChecked()){
		ch0.setChecked(true);
	}
	break;
case R.id.checkBox7://选择周六
	if(!ch6.isChecked()){
		ch0.setChecked(false);
	}else if(ch1.isChecked() && ch2.isChecked() && ch3.isChecked() && ch4.isChecked() && ch5.isChecked() && ch7.isChecked()){
		ch0.setChecked(true);
	}
	break;
case R.id.checkBox8://选择周日
	if(!ch7.isChecked()){
		ch0.setChecked(false);
	}else if(ch1.isChecked() && ch2.isChecked() && ch3.isChecked() && ch4.isChecked() && ch5.isChecked() && ch6.isChecked()){
		ch0.setChecked(true);
	}
	break;
case R.id.checkBox9://选择 开
	if(chOpen.isChecked()){
		chClose.setChecked(false);
	}else{
		chClose.setChecked(true);
	}
	break;
case R.id.checkBox10://选择 开
	if(chClose.isChecked()){
		chOpen.setChecked(false);
	}else{
		chOpen.setChecked(true);
	}
	break;
	
case R.id.savetime:
	if(!ch1.isChecked() && !ch2.isChecked() && !ch3.isChecked() && !ch4.isChecked() && !ch5.isChecked() && !ch6.isChecked() && !ch7.isChecked()){
	new AlertDialog.Builder(TimeActivity.this).setMessage(getString(R.string.message_must_havedate)).setPositiveButton(getString(R.string.ok), null).show();	
	return;
	}
	if(!chOpen.isChecked() && !chClose.isChecked()){
	new AlertDialog.Builder(TimeActivity.this).setMessage(getString(R.string.message_must_havedate)).setPositiveButton(getString(R.string.ok), null).show();	
	return;
	}
	savetime();
	break;
	
case R.id.back:
	finish();
	break;
default:
	break;
}
}
@SuppressLint("InlinedApi")
private void savetime(){

	day ="";
	String time =showTime.getText().toString();
	if(ch0.isChecked()) {
		day="0";
	}else{
		if(ch1.isChecked())day += "1";
		if(ch2.isChecked())day += "2";
		if(ch3.isChecked())day += "3";
		if(ch4.isChecked())day += "4";
		if(ch5.isChecked())day += "5";
		if(ch6.isChecked())day += "6";
		if(ch7.isChecked())day += "7";
	}
	
	
	if(chOpen.isChecked()){
		op=1;
	}else {
		op=0;
	}
	
	
	
	
	LampTimer newtimer = new LampTimer();
	newtimer.setId(lampid);
	newtimer.setName(lampname);
	newtimer.setDay(day);
	newtimer.setTime(time);
	newtimer.setOp(op);
	newtimer.setIp(ip);
	newtimer.setWifi(wifi);

	
	int index=-1;
	List<LampTimer> timerlist = new ArrayList<LampTimer>();
	SharedPreferences sp1 = getSharedPreferences("gs_timerlist", Context.MODE_MULTI_PROCESS);
	String timers = sp1.getString("gson", "");
	if (!timers.equals("")) {
		
		List<LampTimer> gs = gson.fromJson(timers,new TypeToken<List<LampTimer>>() {}.getType());
		
		for (int i = 0; i < gs.size(); i++) {
			LampTimer r = gs.get(i);
			if(r.getTime().equals(time) && r.getId()==lampid){//同一个LAMPNAME和时间重复设置，覆盖。
				index=i;			
			}
			timerlist.add(r);
		}
	}

	if(index>-1){
		Toast.makeText(TimeActivity.this,getString(R.string.message_repeater_datasetting), Toast.LENGTH_SHORT).show();  
		timerlist.set(index, newtimer);
	}else{
		timerlist.add(newtimer);
	}
	
	Collections.sort(timerlist);
	
	String str = gson.toJson(timerlist);
	SharedPreferences sp2 = getSharedPreferences("gs_timerlist", Context.MODE_MULTI_PROCESS);
	SharedPreferences.Editor editor2 = sp2.edit();  
	editor2.clear();
	editor2.putString("gson", str);  
	editor2.commit(); 


	
	setSysAlarm();//APP启动时会重新发送每一条命令，将会覆盖ALARM，没有关系
	

	gslist=timerlist;
	adapter=new TimerAdapter(TimeActivity.this, gslist,lampid);
	listView.setAdapter(adapter);

	
}
private void setSysAlarm() {
	
	long firstTime = SystemClock.elapsedRealtime();  
	long systemTime = System.currentTimeMillis();


        Calendar calendar =Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
	 	calendar.setTimeZone(TimeZone.getTimeZone("GMT+8")); 
	 	calendar.set(Calendar.MINUTE, mMinute);
	 	calendar.set(Calendar.HOUR_OF_DAY, mHour);
	 	calendar.set(Calendar.SECOND, 0);
	 	calendar.set(Calendar.MILLISECOND, 0);

 
	long selectTime = calendar.getTimeInMillis();  
	if(systemTime > selectTime) {  
	Toast.makeText(TimeActivity.this,getString(R.string.message_lessthennow), Toast.LENGTH_SHORT).show();  
	calendar.add(Calendar.DAY_OF_MONTH, 1);  
	selectTime = calendar.getTimeInMillis();  
	long time = selectTime - systemTime;  
	firstTime += time;
	}else{
		firstTime=selectTime;
	}

 		String key=showTime.getText().toString();
		String settime=key.replace(":", ""); 					
		int timeorder=Integer.valueOf(settime);
		Log.i("NNND","===>timeorder:"+timeorder);

 	 		Intent intent = new Intent(TimeActivity.this, AlarmReceiver.class);
 	 		PendingIntent pi = PendingIntent.getBroadcast(TimeActivity.this, timeorder, intent, 0);
 	 		AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
	 		am.setRepeating(AlarmManager.RTC_WAKEUP,firstTime, 24*60*60*1000, pi); //24小时重复 	 		

}



}
