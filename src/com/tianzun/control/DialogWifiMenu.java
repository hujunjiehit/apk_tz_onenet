package com.tianzun.control;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tele.control.R;
import com.tianzun.util.LampTimer;
import com.tianzun.util.Wifi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DialogWifiMenu extends Activity implements OnClickListener{

	private Button  btn_wifi_edit, btn_wifi_delete, btn_wifi_cancel;

	private Wifi wifi=null;
	private List<Wifi> listWifi = null;
	private DialogRename renameDialog =null;
	private String op="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_wifi_menu);

		btn_wifi_edit = (Button) this.findViewById(R.id.btn_wifi_edit);
		btn_wifi_delete = (Button) this.findViewById(R.id.btn_wifi_delete);
		btn_wifi_cancel = (Button)this.findViewById(R.id.btn_wifi_cancel);
		

		btn_wifi_edit.setOnClickListener(this);
		btn_wifi_delete.setOnClickListener(this);
		btn_wifi_cancel.setOnClickListener(this);
		initView();
	}
	
	@SuppressWarnings("unchecked")
	private void initView()
	{
		Intent intent = getIntent();
		op=intent.getStringExtra("op").toString();
		wifi = (Wifi)intent.getSerializableExtra("wifi");
		listWifi =  (List<Wifi>) intent.getSerializableExtra("listWifi");  

		//new AlertDialog.Builder(DialogWifiMenu.this).setMessage(op).show();return;
		
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		//finish();
		return true;
	}


	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {

		case R.id.btn_wifi_edit:
			if("".equals(op))break;
			renameDialog = new DialogRename(this,wifi,listWifi,op);
			renameDialog.show();
		    //finish();//不能FINISH否则WINDOW LEAK
			break;
		case R.id.btn_wifi_delete:


			String gsname="";
			if("".equals(op))break;
			if("jd".equals(op)){
				gsname="gs_jdlist";
			}else if("lamp".equals(op)){
				gsname="gs_lamplist";
			}
        	for(int i=0;listWifi!=null && i<listWifi.size();i++){
        		if(listWifi.get(i).getName().equals(wifi.getName())){
        			listWifi.remove(i);
                	Gson gson = new Gson();
        			String str = gson.toJson(listWifi);
        			SharedPreferences sp = getSharedPreferences(gsname, 0);
        			SharedPreferences.Editor editor = sp.edit();  
        			editor.clear();
        			editor.putString("gson", str);  
        			editor.commit();
        			
        			if("lamp".equals(op)){
        				List<LampTimer> timerlist = new ArrayList<LampTimer>();
        				SharedPreferences sp1 = getSharedPreferences("gs_timerlist", 0);
        				String timers = sp1.getString("gson", "");
        				if (!timers.equals("")) {
        					
        					List<LampTimer> gs = gson.fromJson(timers,new TypeToken<List<LampTimer>>() {}.getType());
        					
        					for (int ii = 0; ii < gs.size(); ii++) {
        						LampTimer r = gs.get(i);
        						if(r.getName().equals(wifi.getName())){
        						}else{
        						timerlist.add(r);
        						}
        					}
        					
        					Collections.sort(timerlist);
        					
        					String str2 = gson.toJson(timerlist);
        					SharedPreferences sp2 = getSharedPreferences("gs_timerlist", 0);
        					SharedPreferences.Editor editor2 = sp2.edit();  
        					editor2.clear();
        					editor2.putString("gson", str2);  
        					editor2.commit(); 
        				}	
        				
        				
        			}
        			
        			
        			
        			
        			if("jd".equals(op)){
        				intent = new Intent(v.getContext(),ActivityJDList.class);
        			}else if("lamp".equals(op)){
        				intent = new Intent(v.getContext(),ActivityLampList.class);
        			}       			
        			v.getContext().startActivity(intent);
        			
        			finish();
        			
        			break;
        		}
        	}
			break;
		case R.id.btn_wifi_cancel:
			finish();
			break;	
		default:
			break;
		}

	}
}
