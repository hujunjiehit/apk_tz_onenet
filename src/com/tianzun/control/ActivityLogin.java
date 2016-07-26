package com.tianzun.control;

import com.tele.control.R;
import com.tianzun.iot.activity.SmartconfigActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ActivityLogin extends Activity {
	
	private TextView tv_test;
	private TextView tv_paring;
	private TextView tv_onenet;
	private TextView tv_sign;
	
	
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.tv_test:
				Intent intent = new Intent(ActivityLogin.this,ActivityTest.class);
				startActivity(intent);
				break;
			
			case R.id.tv_paring:
				Intent intent1 = new Intent(ActivityLogin.this,SmartconfigActivity.class);
				startActivity(intent1);
				break;
				
			case R.id.tv_cloudlogin:
				Intent intent2 = new Intent(ActivityLogin.this,ActivityOnenet.class);
				startActivity(intent2);
				break;
			
			case R.id.tv_sign:
				Intent intent3 = new Intent(ActivityLogin.this,ActivitySign.class);
				startActivity(intent3);
				break;
				
			default:
				break;
			}
		}
	};
	
	private void initview() {
		// TODO Auto-generated method stub
		tv_test = (TextView) findViewById(R.id.tv_test);
		tv_test.setOnClickListener(onClickListener);
		tv_paring = (TextView) findViewById(R.id.tv_paring);
		tv_paring.setOnClickListener(onClickListener);
		tv_onenet = (TextView) findViewById(R.id.tv_cloudlogin);
		tv_onenet.setOnClickListener(onClickListener);
		tv_sign = (TextView) findViewById(R.id.tv_sign);
		tv_sign.setOnClickListener(onClickListener);

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initview();
	}
}
