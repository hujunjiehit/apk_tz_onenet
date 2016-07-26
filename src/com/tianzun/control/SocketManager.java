package com.tianzun.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.security.PublicKey;
import java.util.List;

import com.tele.control.R;

import onenet.edp.ConnectMsg;
import onenet.edp.ConnectRespMsg;
import onenet.edp.EdpKit;
import onenet.edp.EdpMsg;
import onenet.edp.Common.MsgType;
import onenet.edp.PingMsg;
import onenet.edp.PingRespMsg;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class SocketManager {

	private Socket socket;
	private InputStream inStream;
	private OutputStream outStream;
	byte[] packet;
	EdpKit kit = new EdpKit(); // 初始化一个EdpKit实例，用于服务器响应包的数据解析
	byte[] readBuffer = new byte[1024]; // 接收数据的缓存池

	String serverIp = "jjfaedp.hedevice.com";
	int serverPort = 876;
	String devKey = "rlDTdgLKiGuLOXfJmF0sIP6gHn0=";
	int desDevId; // ***用户请使用自己的目标设备ID***
	
	private static SocketManager mInstance;
	private OnenetThread workThread;
	private boolean isConnected;
	private boolean pingStatus;   //false:not ok,  true:ok
	
	private SocketManager(){
		workThread = new OnenetThread();
		isConnected = false;
	}
	
	public static SocketManager getInstance(){
		if(mInstance == null){
			synchronized (SocketManager.class) {
				mInstance = new SocketManager();
			}
		}
		return mInstance;
	}
	
	public void runConnect(){
		workThread.start();
	}
	
	public boolean isConnected(){
		if(socket == null){
			return false;
		}
		return this.isConnected;
	}
	
	public OutputStream getOutStream(){
		return this.outStream;
	}
	
	public boolean getPingStatus(){
		return this.pingStatus;
	}
	public Socket getSocket(){
		return this.socket;
	}
	public void sendPingMsg() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				pingStatus = false;
				PingMsg pingMsg = new PingMsg();
				packet = pingMsg.packMsg();
				try {
					if(outStream == null){
						//Toast.makeText(mContext,mContext.getResources().getString(R.string.onenetsign_already_registed), Toast.LENGTH_SHORT).show();
						Log.i("onenet", "check your network!!!");
						workThread.run();
						return;
					}
					outStream.write(packet);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.i("onenet", "sendPingMsg exception,e:"+e.getMessage());
					isConnected = false;
					workThread.run();
					return;
				}
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//读取服务器的ping响应
				try {
					int readSize = inStream.read(readBuffer);
					if (readSize > 0) {
						byte[] recvPacket = new byte[readSize];
						System.arraycopy(readBuffer, 0, recvPacket, 0, readSize);
						List<EdpMsg> msgs = kit.unpack(recvPacket);
						if (msgs == null || msgs.size() > 1) {
							Log.i("onenet","[ping responce]receive packet exception.---- ping failed");
						} else {
							EdpMsg msg = msgs.get(0);
							if (msg.getMsgType() == MsgType.PINGRESP) {
								PingRespMsg pingRespMsg = (PingRespMsg) msg;
								Log.i("onenet", "[ping responce] ---- ping sucess");
								isConnected = true;
								pingStatus = true;
							} else {
								Log.i("onenet","[ping responce]responce packet is not ping responce.type:" + msg.getMsgType());
							}
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		

	}
	
	public class OnenetThread extends Thread {
		@Override
		public void run() {
			try {
				// 开启连接请求
				if(socket != null){
					socket.close();
				}
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
					Log.i("onenet","[connect responce] msgs.size() = " + msgs.size());
					if (msgs == null || msgs.size() > 1) {
						Log.i("onenet","[connect responce]receive packet exception.");
					} else {
						EdpMsg msg = msgs.get(0);
						if (msg.getMsgType() == MsgType.CONNRESP) {
							ConnectRespMsg connectRespMsg = (ConnectRespMsg) msg;
							Log.i("onenet", "[connect responce] res_code:" + connectRespMsg.getResCode());
							if(connectRespMsg.getResCode() == 0){
								Log.i("onenet", "[connect responce] -- connect sucess");
								isConnected = true;
							}
						} else {
							Log.i("onenet","[connect responce]responce packet is not connect responce.type:" + msg.getMsgType());
						}
					}
				}

			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("onenet","SocketException,e:"+e.getMessage());
			} // 设置超时时长为一分钟
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("onenet","IOException,e:"+e.getMessage());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("onenet","InterruptedException,e:"+e.getMessage());
			}
		}
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


}
