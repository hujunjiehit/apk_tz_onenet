package com.tianzun.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

import onenet.edp.ConnectMsg;
import onenet.edp.ConnectRespMsg;
import onenet.edp.EdpKit;
import onenet.edp.EdpMsg;
import onenet.edp.PushDataMsg;
import onenet.edp.Common.MsgType;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.tele.control.R;
import com.tianzun.control.ActivityOnenet.OnenetThread;
import com.tianzun.util.CheckUtil;
import com.tianzun.util.Command;
import com.tianzun.util.Config;
import com.tianzun.util.DataUtil;
import com.tianzun.util.Router;
import com.tianzun.util.Wifi;

public class ActivityCT extends Activity implements Runnable {

	private int index;
	private Wifi wifi = null;
	private String code = "";
	private String freq = "1000";
	private String white = "255";
	private String yellow = "255";
    private float ratio = 1;
	float cur_x = 0, cur_y = 0;
	Canvas m_canvas = null;
	Paint m_paint = new Paint();
	Path m_path = new Path();
	float rr = 220;

	private ImageView iv;
	private SeekBar sk1;
	private SeekBar sk2;
	private int r = 10, g = 10, b = 10;
	private boolean isZongguan=false;
	private TextView caise;
	private TextView name;
	private LinearLayout m1,m2,m3,m4;
	private TextView back;
	private TextView dingshi;
	
	private byte myid;		
	private int intLiangdu=0;
	private int intSewen=0;
	
	private TextView nuanbaion,nuanbaioff;
	private LinearLayout ll;
	private byte[] lastcommand;
	
	byte[] packet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ct);
		
		ll=(LinearLayout)findViewById(R.id.kaiguan);		
		iv = (ImageView) findViewById(R.id.iv);
		wifi = (Wifi) getIntent().getSerializableExtra("wifi");	
		Bundle bundle=getIntent().getExtras();
		index=bundle.getInt("index");
		
		myid=(byte) (index - 100);
		name=(TextView)findViewById(R.id.name);
		name.setText(wifi.getName());
		
		nuanbaion=(TextView)findViewById(R.id.tv_nuanbaion);
		nuanbaioff=(TextView)findViewById(R.id.tv_nuanbaioff);
		
		back=(TextView)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			finish();
				
			}
		});
		dingshi=(TextView)findViewById(R.id.dingshi);
		dingshi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ActivityCT.this,TimeActivity.class);
				intent.putExtra("lampname", wifi.getName());
				intent.putExtra("lampid", index);
				intent.putExtra("ip", wifi.getIp());
				intent.putExtra("wifi", wifi);
				startActivity(intent);		
				
			}
		});
		m1=(LinearLayout)findViewById(R.id.m1);
		m2=(LinearLayout)findViewById(R.id.m2);
		m3=(LinearLayout)findViewById(R.id.m3);
		m4=(LinearLayout)findViewById(R.id.m4);
		m1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
	
				int p1 = 23;//亮度值
				int p2= 9;//色温值
				byte[] command={0x11,0x01,0x02,0x03,myid,0x02,(byte) 0xe1,(byte) p1,(byte) p2};
				cmd(command, wifi);
				
				sk1.setProgress(23);
				sk2.setProgress(9);
				int color=Color.rgb(100,100,23);
				iv.setBackgroundColor(color);			
				
			}
		});
		m2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
						
				int p1 = 255;//亮度值
				int p2= 100;//色温值
				byte[] command={0x11,0x01,0x02,0x03,myid,0x02,(byte) 0xe1,(byte) p1,(byte) p2};
				cmd(command, wifi);
				
				sk1.setProgress(260);//修改100到255考虑冗余260
				sk2.setProgress(255);
				int color=Color.rgb(255,255,255);
				iv.setBackgroundColor(color);			
								
			}
		});
		m3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				sk1.setProgress(127);
				sk2.setProgress(50);
				int color=Color.rgb(255,255,200);
				iv.setBackgroundColor(color);	
				
				int p1 = 127;
				int p2 = 50;
				byte[] command={0x11,0x01,0x02,0x03,myid,0x02,(byte) 0xe1,(byte) p1,(byte) p2};
				cmd(command, wifi);
								
			}
		});
		m4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		    	
				sk1.setProgress(80);
				sk2.setProgress(15);
				int color=Color.rgb(255,255,100);
				iv.setBackgroundColor(color);		
				
				int p1 = 80;
				int p2 = 15;
				byte[] command={0x11,0x01,0x02,0x03,myid,0x02,(byte) 0xe1,(byte) p1,(byte) p2};
				cmd(command, wifi);
				
			}
		});
			
		
		caise=(TextView)findViewById(R.id.caise);
		caise.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ActivityCT.this,ActivityColorpicker.class);
				intent.putExtra("wifi", wifi);
				intent.putExtra("index", index);
				startActivity(intent);		
				finish();
			}
		});



		nuanbaion.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				ll.setBackgroundResource(R.drawable.open);
				sk1.setEnabled(true);
				sk2.setEnabled(true);
				control("ctON", 50, r, g, b);				
			}
		});
		nuanbaioff.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				ll.setBackgroundResource(R.drawable.shut);
				sk1.setEnabled(false);
				sk2.setEnabled(false);
				control("ctOFF", 50, 0, 0, 0);				
			}
		});


        sk2=(SeekBar)findViewById(R.id.seekBar2);////////////////////////////色温///////////////////////////
		sk2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {	
				
				if(intSewen==0 || intSewen==100 ){
				cmd(lastcommand, wifi);
				}else{
					cmd(lastcommand, wifi);
				}
			}			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {				
			}			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {if(!fromUser)return;
				
				if(progress>85)progress=100;
				if(progress<15)progress=0;
				
				int nProgress =(int) (progress * 255 / 100);

				intSewen=progress;
				
				byte[] command={0x11,0x01,0x02,0x03,myid,0x02,(byte) 0xe1,(byte) intLiangdu,(byte) progress};	
				
				lastcommand=command;
				if(progress%10!=0)return;
				
				yellow = nProgress+"";
				white = (255 - nProgress)+"";
				
				int color=Color.rgb(255,255,nProgress);
				iv.setBackgroundColor(color);						
				
			}
		});

        sk1=(SeekBar)findViewById(R.id.seekBar1);////////////////////////////亮度///////////////////////////
		sk1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				cmd(lastcommand, wifi);
				
				if(intLiangdu==10 || intLiangdu==255 ){
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				}
			}			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {	
			}			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {if(!fromUser)return;
				
				if(progress>225)progress=255;
				if(progress<30)progress=10;
								
				intLiangdu=progress;
				byte[] command={0x11,0x01,0x02,0x03,myid,0x02,(byte) 0xe1,(byte) progress,(byte) intSewen};

				lastcommand=command;			
				
				yellow = progress+"";
				white = (255 - progress)+"";
				ratio = (float)progress / 255;		    	
				int rr = (int)(255*ratio);
				int gg = (int)(255*ratio);
				int bb = (int)(progress*ratio);
				int color=Color.rgb(rr,gg,bb);
				iv.setBackgroundColor(color);
				
				if(progress%20!=0)return;
				
				cmd(command, wifi);	
			}
		});
	}
	

	public void control(String which, int progress, int rr, int gg, int bb) {
		String r0 = null, g0 = null, b0 = null;
		String w0=white,y0=yellow;
		if(isZongguan){w0="0";y0="0";}

		if ("ctON".equals(which)) {
			r0 = (int) (rr * ratio) + "";
			g0 = (int) (gg * ratio) + "";
			b0 = (int) (bb * ratio) + "";
			int color=Color.rgb(255,255,255-progress);
			iv.setBackgroundColor(color);
			byte[] command={0x11,0x01,0x02,0x03,myid,0x02,0x11};
			cmd(command, wifi);
			
			
//			int desDevId = 1090905;
//			PushDataMsg pushDataMsg = new PushDataMsg();
//			try {
//				packet = pushDataMsg.packMsg(desDevId,command);
//				outStream.write(packet);
//				Toast.makeText(ActivityCT.this, "发送远程开灯指令",Toast.LENGTH_SHORT).show();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			return;

		} else if ("ctOFF".equals(which)) {
			r0 = "0";
			g0 = "0";
			b0 = "0";
			y0 = "0";
			w0 = "0";
			int color=Color.rgb(0,0,0);
			iv.setBackgroundColor(color);
			byte[] command={0x11,0x01,0x02,0x03,myid,0x02,0x21};
			cmd(command, wifi);
			
//			desDevId = 1090905;
//			PushDataMsg pushDataMsg1 = new PushDataMsg();
//			try {
//				packet = pushDataMsg1.packMsg(desDevId, command);
//				outStream.write(packet);
//				Toast.makeText(ActivityCT.this, "发送远程关灯指令",Toast.LENGTH_SHORT).show();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			return;

		} 
		else if ("lum".equals(which)) {// 如果是亮度则读取rgb
			r0 = String.valueOf(rr);
			g0 = String.valueOf(gg);
			b0 = String.valueOf(bb);
		} 
		else {
			if ("red".equals(which)) {
				rr = progress;
			} else if ("green".equals(which)) {
				gg = progress;
			} else if ("blue".equals(which)) {
				bb = progress;
			}
			r0 = (int) (rr * ratio) + "";
			g0 = (int) (gg * ratio) + "";
			b0 = (int) (bb * ratio) + "";
		}

		code = "setlight={\"freq\":" + freq + ",\"rgb\":{\"red\":" + r0
				+ ",\"green\":" + g0 + ",\"blue\":" + b0 + ",\"yellow\":"
				+ y0 + ",\"white\":" + w0 + "}}";

		Thread t = new Thread(ActivityCT.this);
		t.start();

	}

	public void openrelay() {//总开关开
		String r0,g0,b0,w0,y0;
		r0 = (int) (r * ratio) + "";
		g0 = (int) (g * ratio) + "";
		b0 = (int) (b * ratio) + "";
		w0=(int)(Integer.valueOf(white)* ratio) + "";
		y0=(int)(Integer.valueOf(yellow)* ratio) + "";
		
		code = "setlight={\"freq\":" + freq + ",\"rgb\":{\"red\":" + r0	+ ",\"green\":" + g0 + ",\"blue\":" + b0 + ",\"yellow\":" + y0 + ",\"white\":" + w0 + "}}";//,\"lgt_relay\":{\"relay\":1}
		Thread t = new Thread(ActivityCT.this);
		t.start();

	}

	public void closerelay() {//总开关关
		String r0="0",g0="0",b0="0",w0="0",y0="0";
		code = "setlight={\"freq\":" + freq + ",\"rgb\":{\"red\":" + r0	+ ",\"green\":" + g0 + ",\"blue\":" + b0 + ",\"yellow\":" + y0 + ",\"white\":" + w0 + "}}";//,\"lgt_relay\":{\"relay\":1}
		Thread t = new Thread(ActivityCT.this);
		t.start();
	}


	@Override
	public void run() {
		if(CheckUtil.isLocal(this, wifi))
		{
			CommandLocalRunnable clr = new CommandLocalRunnable();
			Thread t = new Thread(clr);
			t.start();
		}else
		{
			CommandRemoteRunnable crr = new CommandRemoteRunnable();
			
			Thread t = new Thread(crr);
			t.start();
		}


	}

	private class CommandRemoteRunnable implements Runnable
	{
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			 Router centerRouter = findRouterById(wifi.getRouterid());
			 if(centerRouter != null && centerRouter.getCentermac() != null){
				 Command.commandRemote(centerRouter.getCentermac(), code, true,handler,Config.WHAT_DEFAULT);
			 }
			
			 //ProgressDialogUtil.showAlertDialog(ActivityManualAirConditioner.this);
		}
		
	}
	



	private class CommandLocalRunnable implements Runnable {
		@Override
		public void run() {

			Command.commandRouter(code, wifi.getIp(), handler,Config.WHAT_DEFAULT);

		}

	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Config.WHAT_DEFAULT:
				break;
			case Config.WHAT_CHECK_WIFI_ONLINE:
				handleResult(msg);
				break;
			case 0x12345:
				Toast.makeText(ActivityCT.this, "正在获取设备id...", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	private void handleResult(Message msg) {
    	//Bundle bundle = msg.getData();   	
    	//String result = bundle.getString("result");
    	//bundle.getSerializable("wifi");
    	//new AlertDialog.Builder(ActivityCT.this).setMessage(result).show();
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}
	
	private Router findRouterById(int id){
		return CheckUtil.findRouterById(this,wifi.getRouterid());
		
	}
	
	public void cmd(final byte[] code,final Wifi wifi){
		new Thread(new Runnable(){

			@Override
			public void run() {

				if(MyApplication.desDevId.equals("")){
					Log.d("onenet", "desDevId is not ready");
					MyApplication.mApplication.getDesDevId();
					handler.sendEmptyMessage(0x12345);
					return;
				}
				int desDevId = Integer.parseInt(MyApplication.desDevId);
				PushDataMsg pushDataMsg1 = new PushDataMsg();
				try {
					packet = pushDataMsg1.packMsg(desDevId,code);
					if(SocketManager.getInstance().isConnected()){
						SocketManager.getInstance().getOutStream().write(packet);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
//				if(CheckUtil.isLocal(getApplicationContext(), wifi))
//				{//局域网直接广播 
//
//					DatagramSocket socket = null;
//					DatagramPacket packet = null;
//					try {
//						byte[] data =code;
//						socket = new DatagramSocket();
//						socket.setBroadcast(true); 
//						packet = new DatagramPacket(data, data.length,InetAddress.getByName(wifi.getIp()),Config.BROADCAST_PORT);
//						socket.send(packet);
//						socket.close();
//					}catch (Exception ex)
//					{
//						
//					} finally {
//						if (socket != null) {
//							socket.close();
//						}
//					}			
//				}else
//				{//远程
//					 String sixteendata = DataUtil.bytesToHexString(code);
//					 Router centerRouter = findRouterById(wifi.getRouterid());
//					 if(centerRouter != null && centerRouter.getCentermac() != null){
//						 Command.commandRemote(centerRouter.getCentermac(), sixteendata, true,handler,Config.WHAT_DEFAULT,2);
//					 }
//				}
								
			}
			
		}).start();
	}

}