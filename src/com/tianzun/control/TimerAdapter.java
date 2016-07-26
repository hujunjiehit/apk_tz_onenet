package com.tianzun.control;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tele.control.R;
import com.tianzun.util.LampTimer;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TimerAdapter extends BaseAdapter{
    private List<LampTimer> list,listfull;
    private Context context;
    private LayoutInflater inflater; 
    Gson gson = new Gson();



    public TimerAdapter(Context context, List<LampTimer> list,int id) {
        this.context = context;
        this.inflater = LayoutInflater.from(context); 
        this.listfull=list;
        
        List<LampTimer> tmpList=new ArrayList<LampTimer>();
        if(0==id){
            for(LampTimer t:list){
            tmpList.add(t);
            }        	
        }else{
        for(LampTimer t:list){
        	if(t.getId()==id)tmpList.add(t);
        }
        }
        this.list=tmpList;
        
    }

 
    @Override
    public int getCount() {
        return list.size();
    }
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
    

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
       
    	ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_timer, null);
			
			holder.lampname = (TextView) convertView.findViewById(R.id.lampname);
			holder.day = (TextView) convertView.findViewById(R.id.day);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.op = (TextView) convertView.findViewById(R.id.op);
			
			holder.lampid=(TextView)convertView.findViewById(R.id.lampid);
			holder.delete=(TextView)convertView.findViewById(R.id.delete);


			
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		} 
		holder.lampname.setText(list.get(position).getName());
		holder.day.setText(list.get(position).getDay());
		holder.time.setText(list.get(position).getTime());
		holder.op.setText(list.get(position).getOp()+"");
		holder.lampid.setText(list.get(position).getId()+"");
		

			holder.delete.setOnClickListener(new View.OnClickListener() {					
				@SuppressLint("InlinedApi")
				@Override
				public void onClick(View v) {
									
					    String t=list.get(position).getTime();					    
					    boolean stillhave=false;
				    	SharedPreferences sp0 = context.getSharedPreferences("gs_timerlist", Context.MODE_MULTI_PROCESS);
				    	String timers0 = sp0.getString("gson", "");
				    	if (!timers0.equals("")) {				    		
				    		List<LampTimer> gslist = gson.fromJson(timers0,new TypeToken<List<LampTimer>>() {}.getType());
				            for(LampTimer t0:gslist){
				            	if(t0.getTime()==t)stillhave=true;
				            }
				    	}
					    if(stillhave)return;//如果删除这个ID的某个定时后，其他ID在同一时间还有设置，则不取消ALARM
					    
			            // 取消闹铃	
						int timeorder=Integer.valueOf(t.replace(":", ""));
						Intent intent = new Intent(context, AlarmReceiver.class);
			            PendingIntent sender = PendingIntent.getBroadcast(context,timeorder, intent, 0);		            	            
			            AlarmManager am = (AlarmManager)context.getSystemService("alarm");
			            am.cancel(sender);			            
			            
			            int pos=listfull.indexOf(list.get(position));
						list.remove(position);
						notifyDataSetChanged();

						listfull.remove(pos);
						
						String str = gson.toJson(listfull);

						SharedPreferences sp = context.getSharedPreferences("gs_timerlist", Context.MODE_MULTI_PROCESS);
						SharedPreferences.Editor editor = sp.edit(); 
						editor.clear();
						editor.putString("gson", str);  
						editor.commit(); 			            
					
				}
			});
			
			
			
        return convertView;
    }
    
    public final class ViewHolder {
		TextView lampname,day,time,op,lampid,delete;

	}


    
}

