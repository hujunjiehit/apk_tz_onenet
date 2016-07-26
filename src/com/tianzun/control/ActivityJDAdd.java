package com.tianzun.control;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tianzun.util.Wifi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tele.control.R;

public class ActivityJDAdd extends FragmentActivity implements View.OnClickListener{

	private Button config_bt;

	
    
    private Spinner spinner;
    private String catid="0";
    private String catname= "";
    private EditText nameid_ed;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lampadd);


		
		Intent intent = getIntent();

		intent.getStringExtra("action");

		config_bt = (Button) this.findViewById(R.id.button1);
		config_bt.setOnClickListener(this);


		nameid_ed = (EditText)findViewById(R.id.edText1);

		catname= getString(R.string.jd_icebox);
		

		initsp();
	}
	public void initsp(){
		//获取spinner
		spinner=(Spinner)findViewById(R.id.spinner1);
		//获取数据
		
		List<Map<String, Object>> spinnerdata3=new ArrayList<Map<String,Object>>();//Adapter.getspinner3data();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("log", R.drawable.ref_1);
		map.put("listname", getString(R.string.jd_icebox));
		spinnerdata3.add(map);
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("log", R.drawable.pc_1);
		map2.put("listname", getString(R.string.jd_computer));
		spinnerdata3.add(map2);
		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("log", R.drawable.tv_1);
		map3.put("listname", getString(R.string.jd_tele));
		spinnerdata3.add(map3);
		Map<String, Object> map4 = new HashMap<String, Object>();
		map4.put("log", R.drawable.fan_1);
		map4.put("listname", getString(R.string.jd_blower));
		spinnerdata3.add(map4);
		Map<String, Object> map5 = new HashMap<String, Object>();
		map5.put("log", R.drawable.air_1);
		map5.put("listname", getString(R.string.jd_aircon));
		spinnerdata3.add(map5);
		Map<String, Object> map6 = new HashMap<String, Object>();
		map6.put("log", R.drawable.heater_1);
		map6.put("listname", getString(R.string.jd_heater));
		spinnerdata3.add(map6);
		Map<String, Object> map7 = new HashMap<String, Object>();
		map7.put("log", R.drawable.vacuum_1);
		map7.put("listname", getString(R.string.jd_vacuumcleaner));
		spinnerdata3.add(map7);
		Map<String, Object> map8 = new HashMap<String, Object>();
		map8.put("log", R.drawable.washer_1);
		map8.put("listname", getString(R.string.jd_washer));
		spinnerdata3.add(map8);
		Map<String, Object> map9 = new HashMap<String, Object>();
		map9.put("log", R.drawable.audio_1);
		map9.put("listname", getString(R.string.jd_sound));
		spinnerdata3.add(map9);

		
		//设置adapter
	
		SimpleAdapter spinner3adAdapter=new SimpleAdapter(this, spinnerdata3, R.layout.spinner3_item, new String[]{"log","listname"}, new int[]{R.id.image,R.id.text});
		//给spinner添加adapter
		spinner.setAdapter(spinner3adAdapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
				catid=position+"";
				catname =((Map<String,Object>)spinner.getItemAtPosition(position)).get("listname").toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {				
			}
	});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.button1:
			saveWificonfig();
			break;
		}
	}


	private void saveWificonfig(){
		Gson gson = new Gson();
		int savedrouterid=0;		
		int index2=-1;
		List<Wifi> wifilist = new ArrayList<Wifi>();
		SharedPreferences sp3 = getSharedPreferences("gs_jdlist", 0);
		String wifis = sp3.getString("gson", "");
		if (!wifis.equals("")) {
			List<Wifi> gs = gson.fromJson(wifis,new TypeToken<List<Wifi>>() {}.getType());
			for (int i = 0; i < gs.size(); i++) {
				Wifi w = gs.get(i);
				String tmp =catname +nameid_ed.getText().toString();
				if(w.getName().equals(tmp))index2=i;
				wifilist.add(w);
			}
		}
		
		Wifi w= new Wifi();
		w.setRouterid(savedrouterid);
		w.setCatid(catid);
		w.setName(catname +nameid_ed.getText().toString());
		
		if(index2>-1){
			wifilist.set(index2, w);
		}else{
			wifilist.add(w);
		}
		
		
		String str2 = gson.toJson(wifilist);
		SharedPreferences sp4 = getSharedPreferences("gs_jdlist", 0);
		SharedPreferences.Editor editor4 = sp4.edit();  
		editor4.clear();
		editor4.putString("gson", str2);  
		editor4.commit(); 

		finish();
		
	}
	
	

}