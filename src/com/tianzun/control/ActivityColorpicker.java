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

import org.json.JSONException;
import org.json.JSONObject;

import com.tele.control.R;
import com.tianzun.clientview.CpView;
import com.tianzun.clientview.CpView.OnColorChangedListener;
import com.tianzun.util.CheckUtil;
import com.tianzun.util.Command;
import com.tianzun.util.Config;
import com.tianzun.util.DataUtil;
import com.tianzun.util.Router;
import com.tianzun.util.Wifi;

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
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ActivityColorpicker extends Activity implements Runnable {

	private CpView myView;

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
	float rr = 220;// 圆盘半径

	private ImageView iv;
	private SeekBar sk1,spsk1;
	private int r = 255, g = 255, b = 255;
	private boolean isZongguan=false;
	private TextView nuanbai;
	private TextView name;
	
	private Handler handler;
	private TextView back;
	
	private byte myid;
	
	private TextView nuanbaion,nuanbaioff;
	private LinearLayout ll;
	
	private TextView dingshi;
	
	private LinearLayout m1,m2,m3,m4;
	
	private byte[] lastcommand=null;
	
	private int lastIndex = 0;
	
	byte[] packet;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cp);
		
		ll=(LinearLayout)findViewById(R.id.kaiguan);
		nuanbaion=(TextView)findViewById(R.id.tv_nuanbaion);
		nuanbaioff=(TextView)findViewById(R.id.tv_nuanbaioff);
		
		iv = (ImageView) findViewById(R.id.iv);
		wifi = (Wifi) getIntent().getSerializableExtra("wifi");	
		
		Bundle bundle=getIntent().getExtras();
		index=bundle.getInt("index");

		myid=(byte) (index - 100);

		name=(TextView)findViewById(R.id.name);

		name.setText(wifi.getName());
		
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
				Intent intent = new Intent(ActivityColorpicker.this,TimeActivity.class);
				intent.putExtra("lampname", wifi.getName());
				intent.putExtra("lampid", index);
				intent.putExtra("ip", wifi.getIp());
				startActivity(intent);		
				
			}
		});
		
		nuanbai=(TextView)findViewById(R.id.nuanbai);
		nuanbai.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ActivityColorpicker.this,ActivityCT.class);
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
				myView.setOn();
				sk1.setEnabled(true);
				spsk1.setEnabled(true);
				control("rgbON", 50, r, g, b);			
			}
		});
		nuanbaioff.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				ll.setBackgroundResource(R.drawable.shut);
				myView.setOff();
				sk1.setEnabled(false);
				spsk1.setEnabled(false);
				control("rgbOFF", 50, 0, 0, 0);			
			}
		});



		myView = (CpView) findViewById(R.id.color_picker_view);

		myView.setOnColorChangedListener(new OnColorChangedListener() {

			@Override
			public void onColorChange(int color) {
				r = Color.red(color);
				g = Color.green(color);
				b = Color.blue(color);

				r=(int)((float)r*ratio);
				g=(int)((float)g*ratio);
				b=(int)((float)b*ratio);


				iv.setBackgroundColor(color);
				control("lum", 50, r, g, b);
			}

			@Override
			public void onStop() {
				// TODO Auto-generated method stub

				//control("lum", 50, r, g, b);
			}
			
			
			

		});



        sk1=(SeekBar)findViewById(R.id.seekBar1);
		sk1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {	
				cmd(lastcommand, wifi);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				yellow = progress+"";
				white = (255 - progress)+"";

				ratio = (float)progress / 255;

				
				int r0 = (int)(r*ratio);
				int g0 = (int)(g*ratio);
				int b0 = (int)(b*ratio);
				int color=Color.rgb(r0,g0,b0);
				iv.setBackgroundColor(color);

				byte[] command={0x11,0x01,0x02,0x03,myid,0x04,(byte) 0xd1,(byte) r0,(byte) g0,(byte) b0};
				lastcommand = command;
				
    	
			}
		});
		
		spsk1=(SeekBar)findViewById(R.id.spseekBar1);
		spsk1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {	
				cmd(lastcommand, wifi);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				//yellow = progress+"";
				//white = (255 - progress)+"";
				progress = 10 -progress;
				byte[] command={0x11,0x01,0x02,0x03,myid,0x04,(byte) 0x81,(byte)progress};
				lastcommand = command;
				
    	
			}
		});
		
		
		m1=(LinearLayout)findViewById(R.id.m1);
		m2=(LinearLayout)findViewById(R.id.m2);
		m3=(LinearLayout)findViewById(R.id.m3);
		m4=(LinearLayout)findViewById(R.id.m4);
		m1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
	
				byte[] command={0x11,0x01,0x02,0x03,myid,0x04,(byte) 0x91};
				cmd(command, wifi);
				
				
			}
		});
		m2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
						
				byte[] command={0x11,0x01,0x02,0x03,myid,0x04,(byte) 0xA1};
				cmd(command, wifi);
								
			}
		});
		m3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				byte[] command={0x11,0x01,0x02,0x03,myid,0x04,(byte) 0xB1};
				cmd(command, wifi);
								
			}
		});
		m4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		    	
				byte[] command={0x11,0x01,0x02,0x03,myid,0x04,(byte) 0xC1};
				cmd(command, wifi);
				
			}
		});
			
		
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case Config.WHAT_DEFAULT:
					break;
				case Config.WHAT_GETLIGHT:
					handleResult(msg);
					break;

				}
			}
		};
	}

	public void control(String which, int progress, int rr, int gg, int bb) {
		String r0 = null, g0 = null, b0 = null;
		String w0=white,y0=yellow;
		if(isZongguan){w0="0";y0="0";}

		if ("rgbON".equals(which)) {
			r0 = (int) (rr * ratio) + "";
			g0 = (int) (gg * ratio) + "";
			b0 = (int) (bb * ratio) + "";
			byte[] command={0x11,0x01,0x02,0x03,myid,0x04,0x11};
			cmd(command, wifi);
			return;

		} else if ("rgbOFF".equals(which)) {
			r0 = "0";
			g0 = "0";
			b0 = "0";
			byte[] command={0x11,0x01,0x02,0x03,myid,0x04,0x21};
			cmd(command, wifi);
			return;

		} 
		else if ("lum".equals(which)) {
			r0 = String.valueOf(rr);
			g0 = String.valueOf(gg);
			b0 = String.valueOf(bb);
			
			byte[] command={0x11,0x01,0x02,0x03,myid,0x04,(byte) 0xd1,(byte) rr,(byte) gg,(byte) bb};
			cmd(command, wifi);
			return;
			
			
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

		Thread t = new Thread(ActivityColorpicker.this);
		t.start();

	}

	public void openrelay() {//总开关开
		String r0,g0,b0,w0,y0;
		r0 = (int) (r * ratio) + "";
		g0 = (int) (g * ratio) + "";
		b0 = (int) (b * ratio) + "";
		w0=(int)(Integer.valueOf(white)* ratio) + "";
		y0=(int)(Integer.valueOf(yellow)* ratio) + "";
		
		code = "setlight={\"freq\":" + freq + ",\"rgb\":{\"red\":" + r0
				+ ",\"green\":" + g0 + ",\"blue\":" + b0 + ",\"yellow\":"
				+ y0 + ",\"white\":" + w0 + "}}";//,\"lgt_relay\":{\"relay\":1}
		Thread t = new Thread(ActivityColorpicker.this);
		t.start();
	}

	public void closerelay() {//总开关关
		String r0="0",g0="0",b0="0",w0="0",y0="0";
		code = "setlight={\"freq\":" + freq + ",\"rgb\":{\"red\":" + r0
				+ ",\"green\":" + g0 + ",\"blue\":" + b0 + ",\"yellow\":"
				+ y0 + ",\"white\":" + w0 + "}}";
		Thread t = new Thread(ActivityColorpicker.this);
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
			
			 Command.commandRemote(wifi.getBSSID(), code, true,handler,Config.WHAT_DEFAULT);
			 //ProgressDialogUtil.showAlertDialog(ActivityManualAirConditioner.this);
		}
		
	}


	private class CommandLocalRunnable implements Runnable {
		@Override
		public void run() {

			Command.commandRouter(code, wifi.getIp(), handler,Config.WHAT_DEFAULT);

		}

	}



	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}
	

	private void handleResult(Message msg) {
    	Bundle bundle = msg.getData();   	
    	String result = bundle.getString("result");    	
		if ("".equals(result)) {			
            return;
		} else {
			JSONObject obj;
			try {
				obj = new JSONObject(result).getJSONObject("rgb");
				
				r=obj.getInt("red");
				g=obj.getInt("green");
				b=obj.getInt("blue");
				
				int color=Color.rgb(r,g,b);
				iv.setBackgroundColor(color);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onResume(){		
		super.onResume();
	}
	public void cmd(final byte[] code,final Wifi wifi){
		new Thread(new Runnable() {

			@Override
			public void run() {

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
				
//				if (CheckUtil.isLocal(getApplicationContext(), wifi)) {// 局域
//					DatagramSocket socket = null;
//					DatagramPacket packet = null;
//					try {
//						byte[] data = code;
//						socket = new DatagramSocket();
//						socket.setBroadcast(true);
//						packet = new DatagramPacket(data, data.length,
//								InetAddress.getByName(wifi.getIp()),
//								Config.BROADCAST_PORT);
//						socket.send(packet);
//						socket.close();
//					} catch (Exception ex) {
//					} finally {
//						if (socket != null) {
//							socket.close();
//						}
//					}
//
//				} else {// 远程
//					String sixteendata = DataUtil.bytesToHexString(code);
//					 Router centerRouter = findRouterById(wifi.getRouterid());
//					 if(centerRouter != null && centerRouter.getCentermac() != null){
//						 Command.commandRemote(centerRouter.getCentermac(), sixteendata, true,handler,Config.WHAT_DEFAULT,2);
//					 }
//					//Command.commandRemote(wifi.getCentermac(), sixteendata, true,handler, Config.WHAT_DEFAULT, 2);
//				}
			}

		}).start();
	}
	private Router findRouterById(int id){
		return CheckUtil.findRouterById(this,wifi.getRouterid());
		
	}
	
}