package com.tianzun.control;

import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tele.control.R;
import com.tianzun.util.LampTimer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class AllTimeActivity extends Activity {

    private int lampid=0;
    private TextView back;
    
    private Gson gson = new Gson();
    private ListView listView;
    
    TimerAdapter adapter;
    List<LampTimer> gslist;
    AlarmManager am;
    
    TextView setauto;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alltime);	
		
        initializeViews();    
}

    @SuppressLint("InlinedApi")
	private void initializeViews(){
    	setauto=(TextView)findViewById(R.id.setauto);
    	setauto.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
			}
		});

   
		back=(TextView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
                             
               
        listView=(ListView)findViewById(R.id.listView1);        
    	SharedPreferences sp = getSharedPreferences("gs_timerlist", Context.MODE_MULTI_PROCESS);
    	String timers = sp.getString("gson", "");
    	if (!timers.equals("")) {
    		
    		gslist = gson.fromJson(timers,new TypeToken<List<LampTimer>>() {}.getType());
    		adapter=new TimerAdapter(AllTimeActivity.this, gslist,lampid);
    		listView.setAdapter(adapter);

    	}
    }
    


}
