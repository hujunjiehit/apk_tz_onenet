package com.tianzun.control;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tele.control.R;
import com.tianzun.util.LampTimer;
import com.tianzun.util.Wifi;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DialogRename extends Dialog implements View.OnClickListener{
    private Activity activity;
    private Button btn_rename_ok;
    private Wifi wifi;
    private EditText newname;
    private List<Wifi> listWifi;
    private String op;
    
    public DialogRename(Activity activity,Wifi wifi,List<Wifi> list,String op) {
		this(activity, false, null);
		this.activity=activity;
        this.wifi = wifi;
        this.listWifi = list;
        this.op = op;
    }
    public DialogRename(Context context, int theme){
        super(context, theme);
    }


    private DialogRename(Activity activity, boolean cancelable, OnCancelListener cancelListener) {
        super(activity, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rename);// 設置佈局文件
        btn_rename_ok = (Button) findViewById(R.id.btn_rename_ok);
        newname = (EditText)findViewById(R.id.sp_rename_room_list);
        btn_rename_ok.setOnClickListener(this);
        newname.setText(wifi.getName());
       //// new AlertDialog.Builder(getContext()).setMessage(op).show();
        
    }

    @Override
    public void show() {
    	
        super.show();

    }




  
	
	
    @Override
    public void onClick(View v) {
    	
    	String name = newname.getText().toString();

    	if("".equals(name)){
    		Toast.makeText(activity,"不能为空", Toast.LENGTH_SHORT).show();
    		return;
    	}
        switch (v.getId()) {
            case R.id.btn_rename_ok:
            	
    			String gsname="";
    			if("jd".equals(op)){
    				gsname="gs_jdlist";
    			}else if("lamp".equals(op)){
    				gsname="gs_lamplist";
    			}
            	
            	
            	if(!wifi.getName().equals(name)){
            	for(int i=0;listWifi !=null && i<listWifi.size();i++){
            		if(listWifi.get(i).getName().equals(wifi.getName())){
            			listWifi.get(i).setName(name);
            			
            			if("lamp".equals(op)){
            				Gson gson1 = new Gson();
            				List<LampTimer> timerlist = new ArrayList<LampTimer>();
            				SharedPreferences sp1 = activity.getSharedPreferences("gs_timerlist", 0);
            				String timers = sp1.getString("gson", "");
            				if (!timers.equals("")) {
            					
            					List<LampTimer> gs = gson1.fromJson(timers,new TypeToken<List<LampTimer>>() {}.getType());
            					
            					for (int ii = 0; ii < gs.size(); ii++) {
            						LampTimer r = gs.get(ii);
            						if(r.getName().equals(wifi.getName())){
            							r.setName(name);			
            						}
            						timerlist.add(r);
            					}
            					
            					Collections.sort(timerlist);
            					
            					String str = gson1.toJson(timerlist);
            					SharedPreferences sp2 = activity.getSharedPreferences("gs_timerlist", 0);
            					SharedPreferences.Editor editor = sp2.edit();  
            					editor.clear();
            					editor.putString("gson", str);  
            					editor.commit(); 
            				}	
            				
            				
            			}
            			
            			
            			break;
            		}
            	}
            	            	
            	Gson gson = new Gson();
    			String str = gson.toJson(listWifi);
    			SharedPreferences sp = activity.getSharedPreferences(gsname, 0);
    			SharedPreferences.Editor editor = sp.edit();  
    			editor.clear();
    			editor.putString("gson", str);  
    			editor.commit();
            	}

            	
            	Intent intent = null;
    			if("jd".equals(op)){
    				intent = new Intent(v.getContext(),ActivityJDList.class);
                    v.getContext().startActivity(intent);            	
                	dismiss();
                	activity.finish();
    			}else if("lamp".equals(op)){
    				intent = new Intent(v.getContext(),ActivityLampList.class);
                    v.getContext().startActivity(intent);            	
                	dismiss();
                	activity.finish();
    			}

                break;
           
            default:
                break;
        }
    }


    
}
