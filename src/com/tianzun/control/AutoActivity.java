package com.tianzun.control;

import android.os.Bundle;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.view.View;
import android.view.View.OnClickListener;
import com.tele.control.R;

public class AutoActivity extends Activity implements OnClickListener {
CheckBox checkBox;
Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto);
		
		checkBox=(CheckBox)findViewById(R.id.checkBox1);
		button=(Button)findViewById(R.id.button1);
		checkBox.setOnClickListener(this);
		button.setOnClickListener(this);
		
		SharedPreferences sp = getSharedPreferences("autostart", Context.MODE_MULTI_PROCESS);
		String str=sp.getString("auto", "");
		if("0".equals(str)){
			checkBox.setChecked(false);
		}else{
			checkBox.setChecked(true);
		}
			
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.checkBox1:
			SharedPreferences sp = getSharedPreferences("autostart", Context.MODE_MULTI_PROCESS);
			SharedPreferences.Editor editor=sp.edit();
			if(checkBox.isChecked()){
				editor.putString("auto", "").commit();
				//恢复开机自动运行组件
				final ComponentName receiver = new ComponentName(AutoActivity.this,BootReceiver.class);
				final PackageManager pm = AutoActivity.this.getPackageManager();
				pm.setComponentEnabledSetting(receiver,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);

			}else {
				editor.putString("auto", "0").commit();
			}
			break;
		case R.id.button1:
			finish();
			break;

		default:
			break;
		}
		
	}


}
