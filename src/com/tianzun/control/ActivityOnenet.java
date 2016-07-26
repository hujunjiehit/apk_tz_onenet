package com.tianzun.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

import onenet.edp.ConnectMsg;
import onenet.edp.ConnectRespMsg;
import onenet.edp.EdpKit;
import onenet.edp.EdpMsg;
import onenet.edp.PushDataMsg;
import onenet.edp.Common.MsgType;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tele.control.R;

public class ActivityOnenet extends Activity {

	private Button btn_onenetconnect;
	private Button btn_onenetlighton;
	private Button btn_onenetlightoff;
	private Button btn_onenetdisconnect;
	private Button btn_onenetrestart;
	private Button btn_onenetquery;
	private EditText et_devID;
	Socket socket;
	InputStream inStream;
	OutputStream outStream;
	byte[] packet;
	EdpKit kit = new EdpKit(); // 初始化一个EdpKit实例，用于服务器响应包的数据解析
	byte[] readBuffer = new byte[1024]; // 接收数据的缓存池

	String serverIp = "jjfaedp.hedevice.com";
	int serverPort = 876;
	String devKey = "rlDTdgLKiGuLOXfJmF0sIP6gHn0=";
	int desDevId; // ***用户请使用自己的目标设备ID***

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {

			case R.id.onenet_connect:
				new OnenetThread().start();
				Toast.makeText(ActivityOnenet.this, "发送连接onenet请求",
						Toast.LENGTH_SHORT).show();
				break;

			case R.id.onenet_light_on:
				// 远程发送开灯指令
				byte[] pushData = {0x11, 0x01, 0x02, 0x03, (byte) 0x01, 0x02,
						(byte) 0x11 };
				if (TextUtils.isEmpty(et_devID.getText())) {
					//1090905
					Toast.makeText(ActivityOnenet.this, "请输入要发送的目标ID号",Toast.LENGTH_SHORT).show();
					break;
				}
				desDevId = Integer.parseInt(et_devID.getText().toString());
				PushDataMsg pushDataMsg = new PushDataMsg();
				try {
					packet = pushDataMsg.packMsg(desDevId, pushData);
					outStream.write(packet);
					Toast.makeText(ActivityOnenet.this, "发送远程开灯指令",Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			case R.id.onenet_light_off:
				// 远程发送关灯指令
				byte[] pushData1 = { 0x11, 0x01, 0x02, 0x03, (byte) 0x01, 0x02,
						(byte) 0x21 };
				if (TextUtils.isEmpty(et_devID.getText())) { 
					//1090905
					Toast.makeText(ActivityOnenet.this, "请输入要发送的目标ID号",
							Toast.LENGTH_SHORT).show();
					break;
				}
				desDevId = Integer.parseInt(et_devID.getText().toString());
				PushDataMsg pushDataMsg1 = new PushDataMsg();
				try {
					packet = pushDataMsg1.packMsg(desDevId, pushData1);
					outStream.write(packet);
					Toast.makeText(ActivityOnenet.this, "发送远程关灯指令",
							Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;

			case R.id.onenet_disconnect:
				// 关闭socket连接
				try {
					socket.close();
					inStream.close();
					outStream.close();
					Toast.makeText(ActivityOnenet.this, "断开与OneNet连接",
							Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			case R.id.onenet_restart:
				// 远程重启模块
				byte[] pushData2 = { 0x33, 0x01, 0x02, 0x03, (byte) 0x01, 0x02,
						(byte) 0x21 };
				if (TextUtils.isEmpty(et_devID.getText())) {
					Toast.makeText(ActivityOnenet.this, "请输入要发送的目标ID号",
							Toast.LENGTH_SHORT).show();
					break;
				}
				desDevId = Integer.parseInt(et_devID.getText().toString());
				PushDataMsg pushDataMsg2 = new PushDataMsg();
				try {
					packet = pushDataMsg2.packMsg(desDevId, pushData2);
					outStream.write(packet);
					Toast.makeText(ActivityOnenet.this, "重启模块",
							Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			case R.id.onenet_query:
				// 查询灯的指令
				byte[] pushData3 = { 0x22, 0x01, 0x02, 0x03, (byte) 0x01, 0x02,
						(byte) 0x21 };
				if (TextUtils.isEmpty(et_devID.getText())) {
					Toast.makeText(ActivityOnenet.this, "请输入要发送的目标ID号",
							Toast.LENGTH_SHORT).show();
					break;
				}
				desDevId = Integer.parseInt(et_devID.getText().toString());
				PushDataMsg pushDataMsg3 = new PushDataMsg();
				try {
					packet = pushDataMsg3.packMsg(desDevId, pushData3);
					outStream.write(packet);
					Toast.makeText(ActivityOnenet.this, "查询灯的状态",
							Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			default:
				break;
			}
		}
	};

	private void initview() {
		// TODO Auto-generated method stub
		btn_onenetconnect = (Button) findViewById(R.id.onenet_connect);
		btn_onenetconnect.setOnClickListener(onClickListener);
		btn_onenetlighton = (Button) findViewById(R.id.onenet_light_on);
		btn_onenetlighton.setOnClickListener(onClickListener);
		btn_onenetlightoff = (Button) findViewById(R.id.onenet_light_off);
		btn_onenetlightoff.setOnClickListener(onClickListener);
		btn_onenetdisconnect = (Button) findViewById(R.id.onenet_disconnect);
		btn_onenetdisconnect.setOnClickListener(onClickListener);
		btn_onenetquery = (Button) findViewById(R.id.onenet_query);
		btn_onenetquery.setOnClickListener(onClickListener);
		btn_onenetrestart = (Button) findViewById(R.id.onenet_restart);
		btn_onenetrestart.setOnClickListener(onClickListener);
		et_devID = (EditText) findViewById(R.id.ed_connect_devID);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_onenet);
		initview();
	}

	public static String byteArrayToString(byte[] array) {
		if (array == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			String hex = Integer.toHexString(array[i] & 0xff);
			if (hex.length() == 1) {
				sb.append("0" + hex);
			} else {
				sb.append(hex);
			}
		}

		return sb.toString();
	}

	public class OnenetThread extends Thread {

		@Override
		public void run() {
			try {
				// 开启连接请求
				socket = new Socket(serverIp, serverPort);
				socket.setSoTimeout(60 * 1000);
				inStream = socket.getInputStream();
				outStream = socket.getOutputStream();
				ConnectMsg connectMsg = new ConnectMsg();
				int devID = Integer.parseInt(MyApplication.device_id);
				packet = connectMsg.packMsg(devID, devKey);
				outStream.write(packet);
				Log.i("onenet", "[connect]packet size:" + packet.length);
				Log.i("ontnet", "[connect]packet:" + byteArrayToString(packet));
				Thread.sleep(500);
				// 接收服务器的连接响应
				int readSize = inStream.read(readBuffer);
				if (readSize > 0) {
					byte[] recvPacket = new byte[readSize];
					System.arraycopy(readBuffer, 0, recvPacket, 0, readSize);
					List<EdpMsg> msgs = kit.unpack(recvPacket);
					if (msgs == null || msgs.size() > 1) {
						Log.i("onenet",
								"[connect responce]receive packet exception.");
					} else {
						EdpMsg msg = msgs.get(0);
						if (msg.getMsgType() == MsgType.CONNRESP) {
							ConnectRespMsg connectRespMsg = (ConnectRespMsg) msg;
							Log.i("onenet", "[connect responce] res_code:"
									+ connectRespMsg.getResCode());
						} else {
							Log.i("onenet","[connect responce]responce packet is not connect responce.type:"
											+ msg.getMsgType());
						}
					}
				}

			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // 设置超时时长为一分钟
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
