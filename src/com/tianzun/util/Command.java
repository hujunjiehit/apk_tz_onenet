package com.tianzun.util;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.commons.lang.StringUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Command {

	private static final String TAG = "Command";

	private static byte[] inBuff = new byte[8192];

	private static DatagramPacket inPacket = new DatagramPacket(inBuff,
			inBuff.length); // 以指定的字节数组创建准备接收数据的DatagramPacket对象

	

	
	
	/**
	 * 不更新界面,执行命令，指定是否等待返回结果,仅限在手机连接遥控WIFI时使用
	 * @param cmd
	 * @param waitReturn
	 * @return
	 */
	public static String commandWifi(String cmd,boolean waitReturn) {

		String recvData = "";
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		try {
			Log.i(TAG, "发送命令-->" + cmd+";broadcastIp-->"+Config.BROADCAST_IP);
			byte[] data = cmd.getBytes();
			socket = new DatagramSocket();
			socket.setBroadcast(true); // send端指定接受端的端口，自己的端口是随机的
			packet = new DatagramPacket(data, data.length,InetAddress.getByName(Config.BROADCAST_IP),Config.BROADCAST_PORT);
			socket.send(packet);
			if(waitReturn)
			{
				socket.setSoTimeout(4000);
				socket.receive(inPacket);
				recvData = new String(inBuff, 0, inPacket.getLength());
				if(!StringUtils.isBlank(recvData))
				{
					recvData = DataProcessUtil.processResponseDataFromDevice(recvData);
					recvData = recvData.replace("response", "Response");
				}
				Log.i(TAG, "接收数据-->" + recvData);
			}
			
			socket.close();
		}catch (Exception ex)
		{
			ex.printStackTrace();
			Log.e(TAG, "commandWifi ex-->"+ex.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
		return recvData;
	}
	
	/**
	 * 更新界面,执行命令，等待返回结果,仅限在手机连接遥控WIFI时使用
	 * @param cmd
	 * @param waitReturn
	 * @param handler
	 * @param what
	 * @return
	 */
	public static String commandWifi(String cmd,Handler handler,int what) {

		String recvData = "";
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		try {
			Log.i(TAG, "发送命令-->" + cmd+";broadcastIp-->"+Config.BROADCAST_IP);
			byte[] data = cmd.getBytes();
			socket = new DatagramSocket();
			socket.setBroadcast(true); // send端指定接受端的端口，自己的端口是随机的
			packet = new DatagramPacket(data, data.length,InetAddress.getByName(Config.BROADCAST_IP),Config.BROADCAST_PORT);
			socket.send(packet);
			
			socket.receive(inPacket);
			recvData = new String(inBuff, 0, inPacket.getLength());
			if(!StringUtils.isBlank(recvData))
			{
				recvData = DataProcessUtil.processResponseDataFromDevice(recvData);
				recvData = recvData.replace("response", "Response");
			}
			Log.i(TAG, "接收数据-->" + recvData);
			Message msg = new Message();
			Bundle b = new Bundle();
			msg.what = what;
			b.putString("result", recvData);
			msg.setData(b);
			handler.sendMessage(msg);
			socket.close();
		}catch (Exception ex)
		{
			Log.i(TAG, "ex-->"+ex.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
		return recvData;
	}
	

	
	/**
	 * 不更新界面,执行命令，在指定时间内等待返回结果,仅限在手机连接遥控WIFI时使用
	 * @param cmd
	 * @return
	 */
	public static String commandWifiWithTimeout(String cmd) {

		String recvData = "";
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		try {
			Log.i(TAG, "发送命令-->" + cmd+";broadcastIp-->"+Config.BROADCAST_IP);
			byte[] data = cmd.getBytes();
			socket = new DatagramSocket();
			socket.setBroadcast(true); // send端指定接受端的端口，自己的端口是随机的
			packet = new DatagramPacket(data, data.length,InetAddress.getByName(Config.BROADCAST_IP),Config.BROADCAST_PORT);
			socket.send(packet);
			try
			{
				socket.setSoTimeout(Config.SOCKET_TIMEOUT_MILLISECONDS);
				socket.receive(inPacket);
				recvData = new String(inBuff, 0, inPacket.getLength());
				if(!StringUtils.isBlank(recvData))
				{
					recvData = DataProcessUtil.processResponseDataFromDevice(recvData);
					recvData = recvData.replace("response", "Response");
				}
			}catch(Exception ex)
			{
				Log.i(TAG, cmd+"命令超时!");
				recvData = "";
			}
			Log.i(TAG, "接收数据-->" + recvData);
			socket.close();
		}catch (Exception ex)
		{
			Log.i(TAG, "ex-->"+ex.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
		return recvData;
	}
	
	/**
	 * 更新界面,执行命令，在指定时间内等待返回结果,仅限在手机连接遥控WIFI时使用
	 * @param cmd
	 * @param waitReturn
	 * @param handler
	 * @param what
	 * @return
	 */
	public static String commandWifiWithTimeout(String cmd,Handler handler,int what) {

		String recvData = "";
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		try {
			Log.i(TAG, "发送命令-->" + cmd+";broadcastIp-->"+Config.BROADCAST_IP);
			byte[] data = cmd.getBytes();
			socket = new DatagramSocket();
			socket.setBroadcast(true); // send端指定接受端的端口，自己的端口是随机的
			packet = new DatagramPacket(data, data.length,InetAddress.getByName(Config.BROADCAST_IP),Config.BROADCAST_PORT);
			socket.send(packet);
			try
			{
				socket.setSoTimeout(Config.SOCKET_TIMEOUT_MILLISECONDS);
				socket.receive(inPacket);
				recvData = new String(inBuff, 0, inPacket.getLength());
				if(!StringUtils.isBlank(recvData))
				{
					recvData = DataProcessUtil.processResponseDataFromDevice(recvData);
					recvData = recvData.replace("response", "Response");
				}
			}catch(Exception ex)
			{
				Log.i(TAG, cmd+"命令超时!");
				recvData = "";
			}

			Log.i(TAG, "接收数据-->" + recvData);
			Message msg = new Message();
			Bundle b = new Bundle();
			msg.what = what;
			b.putString("result", recvData);
			msg.setData(b);
			handler.sendMessage(msg);
			Log.i(TAG, "接收数据-->" + recvData);
			socket.close();
		}catch (Exception ex)
		{
			Log.i(TAG, "ex-->"+ex.getMessage());
		}  finally {
			if (socket != null) {
				socket.close();
			}
		}
		return recvData;
	}
	

	/**
	 * 不更新界面,只执行命令，指定是否等待返回结果,仅限在手机连接家里路由器时使用
	 * @param cmd
	 * @param broadcastIp 
	 * @param waitReturn
	 * @return
	 */
	public static String commandRouter(String cmd,Handler handler,String broadcastIp,boolean waitReturn) {

		String recvData = "";
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		try {
			byte[] data = cmd.getBytes();
			socket = new DatagramSocket();
			socket.setBroadcast(true); 
			packet = new DatagramPacket(data, data.length,InetAddress.getByName(broadcastIp),Config.BROADCAST_PORT);
			socket.send(packet);
			if(waitReturn)
			{
				socket.receive(inPacket);
				recvData = new String(inBuff, 0, inPacket.getLength());
				if(!"".equals(recvData))
				{
					recvData = recvData.replace("response", "Response");
				}
				
				Message msg = new Message();
				Bundle b = new Bundle();
				msg.what = Config.WHAT_DEFAULT;
				b.putString("result", recvData);
				msg.setData(b);
				handler.sendMessage(msg);
				
			}
			

			
			
			socket.close();
		}catch (Exception ex)
		{
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
		

		return recvData;
	}
	
	/**
	 * 不更新界面,只执行命令，指定是否等待返回结果,仅限在手机连接家里路由器时使用
	 * @param cmd
	 * @param broadcastIp 
	 * @param waitReturn
	 * @return
	 */
	public static String commandRouter(String cmd,String broadcastIp,boolean waitReturn) {

		String recvData = "";
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		try {
			Log.i(TAG, "发送命令-->" + cmd+";broadcastIp-->"+broadcastIp);
			byte[] data = cmd.getBytes();
			socket = new DatagramSocket();
			socket.setBroadcast(true); 
			packet = new DatagramPacket(data, data.length,InetAddress.getByName(broadcastIp),Config.BROADCAST_PORT);
			socket.send(packet);
			if(waitReturn)
			{
				socket.receive(inPacket);
				recvData = new String(inBuff, 0, inPacket.getLength());
				if(!StringUtils.isBlank(recvData))
				{
					recvData = DataProcessUtil.processResponseDataFromDevice(recvData);
					recvData = recvData.replace("response", "Response");
				}
				Log.i(TAG, "接收数据-->" + recvData);
			}
			
			socket.close();
		}catch (Exception ex)
		{
			Log.i(TAG, "ex-->"+ex.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
		return recvData;
	}
	
	
	
	
	/**
	 * 更新界面,只执行命令，等待返回结果,仅限在手机连接家里路由器时使用
	 * @param cmd
	 * @param broadcastIp 
	 * @param handler
	 * @param what
	 * @return
	 */
	public static String commandRouter(String cmd,String broadcastIp,Handler handler,int what) {

		String recvData = "";
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		try {
			Log.i(TAG, "发送命令-->" + cmd+";broadcastIp-->"+broadcastIp);
			byte[] data = cmd.getBytes();
			socket = new DatagramSocket();
			socket.setBroadcast(true); 
			packet = new DatagramPacket(data, data.length,InetAddress.getByName(broadcastIp),Config.BROADCAST_PORT);
			socket.send(packet);
			socket.setSoTimeout(4000);
			socket.receive(inPacket);
			recvData = new String(inBuff, 0, inPacket.getLength());
			if(!StringUtils.isBlank(recvData))
			{
				recvData = DataProcessUtil.processResponseDataFromDevice(recvData);
				recvData = recvData.replace("response", "Response");
			}
			Log.i(TAG, "接收数据-->" + recvData);
			
			Message msg = new Message();
			Bundle b = new Bundle();
			msg.what = what;
			b.putString("result", recvData);
			msg.setData(b);
			handler.sendMessage(msg);
			
			socket.close();
		}catch (Exception ex)
		{
			Log.i(TAG, "commandRouter ex-->"+ex.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
		return recvData;
	}
	
	
	/**
	 * 更新界面,只执行命令，等待返回结果,仅限在手机连接家里路由器时使用
	 * @param cmd
	 * @param broadcastIp 
	 * @param handler
	 * @param what
	 * @return
	 */
	public static String commandRouter(String cmd,String broadcastIp) {

		String recvData = "";
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		try {
			Log.i(TAG, "发送命令-->" + cmd+";broadcastIp-->"+broadcastIp);
			byte[] data = cmd.getBytes();
			socket = new DatagramSocket();
			socket.setBroadcast(true); 
			packet = new DatagramPacket(data, data.length,InetAddress.getByName(broadcastIp),Config.BROADCAST_PORT);
			socket.send(packet);
			socket.receive(inPacket);
			recvData = new String(inBuff, 0, inPacket.getLength());
			if(!StringUtils.isBlank(recvData))
			{
				recvData = DataProcessUtil.processResponseDataFromDevice(recvData);
				recvData = recvData.replace("response", "Response");
			}
			Log.i(TAG, "接收数据-->" + recvData);
			
			
			socket.close();
		}catch (Exception ex)
		{
			Log.i(TAG, "ex-->"+ex.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
		return recvData;
	}
	
	
	/**
	 * 更新界面,只执行命令，等待返回结果,仅限在手机连接家里路由器时使用
	 * @param cmd
	 * @param Wifi 
	 * @param handler
	 * @param what
	 * @return
	 */
	public static String commandRouter(String cmd,Wifi wifi,Handler handler,int what) {

		String recvData = "";
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		try {
			Log.i(TAG, "发送命令-->" + cmd+";broadcastIp-->"+wifi.getIp());
			byte[] data = cmd.getBytes();
			socket = new DatagramSocket();
			socket.setBroadcast(true); 
			packet = new DatagramPacket(data, data.length,InetAddress.getByName(wifi.getIp()),Config.BROADCAST_PORT);
			socket.send(packet);
			socket.receive(inPacket);
			recvData = new String(inBuff, 0, inPacket.getLength());
			if(!StringUtils.isBlank(recvData))
			{
				recvData = DataProcessUtil.processResponseDataFromDevice(recvData);
				recvData = recvData.replace("response", "Response");
			}
			Log.i(TAG, "接收数据-->" + recvData);
			
			Message msg = new Message();
			Bundle b = new Bundle();
			msg.what = what;
			b.putString("result", recvData);
			b.putSerializable("wifi", wifi);
			msg.setData(b);
			handler.sendMessage(msg);
			
			socket.close();
		}catch (Exception ex)
		{
			Log.i(TAG, "ex-->"+ex.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
		return recvData;
	}
	
	
	/**
	 * 更新界面,只执行命令，等待返回结果,仅限在手机连接家里路由器时使用
	 * @param cmd
	 * @param Wifi 
	 * @param handler
	 * @param what
	 * @return
	 */
	public static String commandRouterWithTimeout(String cmd,Wifi wifi,Handler handler,int what) {

		String recvData = "";
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		try {
			Log.i(TAG, "发送命令-->" + cmd+";broadcastIp-->"+wifi.getIp());
			byte[] data = cmd.getBytes();
			socket = new DatagramSocket();
			socket.setBroadcast(true); 
			packet = new DatagramPacket(data, data.length,InetAddress.getByName(wifi.getIp()),Config.BROADCAST_PORT);
			socket.send(packet);
			try
			{
				socket.setSoTimeout(Config.SOCKET_TIMEOUT_MILLISECONDS);
				socket.receive(inPacket);
				recvData = new String(inBuff, 0, inPacket.getLength());
				if(!StringUtils.isBlank(recvData))
				{
					recvData = DataProcessUtil.processResponseDataFromDevice(recvData);
					recvData = recvData.replace("response", "Response");
				}
				Log.i(TAG, "接收数据-->" + recvData);
			}catch(Exception ex)
			{
				Log.i(TAG, cmd+" 命令超时!");
				recvData = "";
			}
			Message msg = new Message();
			Bundle b = new Bundle();
			msg.what = what;
			b.putString("result", recvData);
			b.putSerializable("wifi", wifi);
			msg.setData(b);
			handler.sendMessage(msg);
			
			socket.close();
		}catch (Exception ex)
		{
			Log.i(TAG, "ex-->"+ex.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
		return recvData;
	}
	
	
	/**
	 * 更新界面,只执行命令，等待返回结果,仅限在手机连接家里路由器时使用
	 * @param cmd
	 * @param Wifi 
	 * @param handler
	 * @param what
	 * @return
	 */
	public static String commandRouter(String cmd,Wifi wifi,Controller controller,Handler handler,int what) {

		String recvData = "";
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		try {
			Log.i(TAG, "发送命令-->" + cmd+";broadcastIp-->"+wifi.getIp());
			byte[] data = cmd.getBytes();
			socket = new DatagramSocket();
			socket.setBroadcast(true); 
			packet = new DatagramPacket(data, data.length,InetAddress.getByName(wifi.getIp()),Config.BROADCAST_PORT);
			socket.send(packet);
			socket.receive(inPacket);
			recvData = new String(inBuff, 0, inPacket.getLength());
			if(!StringUtils.isBlank(recvData))
			{
				recvData = DataProcessUtil.processResponseDataFromDevice(recvData);
				recvData = recvData.replace("response", "Response");
			}
			Log.i(TAG, "接收数据-->" + recvData);
			
			Message msg = new Message();
			Bundle b = new Bundle();
			msg.what = what;
			b.putString("result", recvData);
			b.putSerializable("wifi", wifi);
			b.putSerializable("controller", controller);
			msg.setData(b);
			handler.sendMessage(msg);
			
			socket.close();
		}catch (Exception ex)
		{
			Log.i(TAG, "ex-->"+ex.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
		return recvData;
	}

	
	
	
	/**
	 * 不更新界面,只执行命令，在指定时间内等待返回结果,仅限在手机连接家里路由器时使用
	 * @param cmd
	 * @return
	 */
	public static String commandRouterWithTimeout(String cmd,String broadcastIp) {

		String recvData = "";
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		try {
			Log.i(TAG, "发送命令-->" + cmd+";broadcastIp-->"+broadcastIp);
			byte[] data = cmd.getBytes();
			socket = new DatagramSocket();
			socket.setBroadcast(true); // send端指定接受端的端口，自己的端口是随机的
			packet = new DatagramPacket(data, data.length,InetAddress.getByName(broadcastIp),Config.BROADCAST_PORT);
			socket.send(packet);
			try
			{
				socket.setSoTimeout(Config.SOCKET_TIMEOUT_MILLISECONDS);
				socket.receive(inPacket);
				recvData = new String(inBuff, 0, inPacket.getLength());
				if(!StringUtils.isBlank(recvData))
				{
					recvData = DataProcessUtil.processResponseDataFromDevice(recvData);
					recvData = recvData.replace("response", "Response");
				}
			}catch(Exception ex)
			{
				Log.i(TAG, cmd+" 命令超时!");
				recvData = "";
			}
			Log.i(TAG, "接收数据-->" + recvData);
			socket.close();
		} catch (Exception ex)
		{
			Log.i(TAG, "ex-->"+ex.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
		return recvData;
	}
	
	/**
	 * 更新界面,执行命令，在指定时间内等待返回结果,仅限在手机连接家里路由器时使用
	 * @param cmd
	 * @param broadcastIp
	 * @param handler
	 * @param what
	 * @return
	 */
	public static String commandRouterWithTimeout(String cmd,String broadcastIp,Handler handler,int what) {

		String recvData = "";
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		try {
			Log.i(TAG, "发送命令-->" + cmd+";broadcastIp-->"+broadcastIp);
			byte[] data = cmd.getBytes();
			socket = new DatagramSocket();
			socket.setBroadcast(true); // send端指定接受端的端口，自己的端口是随机的
			packet = new DatagramPacket(data, data.length,InetAddress.getByName(broadcastIp),Config.BROADCAST_PORT);
			socket.send(packet);
			try
			{
				socket.setSoTimeout(Config.SOCKET_TIMEOUT_MILLISECONDS);
				socket.receive(inPacket);
				recvData = new String(inBuff, 0, inPacket.getLength());
				if(!StringUtils.isBlank(recvData))
				{
					recvData = DataProcessUtil.processResponseDataFromDevice(recvData);
					recvData = recvData.replace("response", "Response");
				}
			}catch(Exception ex)
			{
				Log.i(TAG, cmd+" 命令超时!");
				recvData = "";
			}
			Log.i(TAG, "接收数据-->" + recvData);
			
			Message msg = new Message();
			Bundle b = new Bundle();
			msg.what = what;
			b.putString("result", recvData);
			msg.setData(b);
			handler.sendMessage(msg);
			
			socket.close();
		}catch (Exception ex)
		{
			Log.i(TAG, "ex-->"+ex.getMessage());
		}  finally {
			if (socket != null) {
				socket.close();
			}
		}
		return recvData;
	}
	
	/**
	 * 不更新界面,远程执行执行命令并获得返回结果
	 * @param cmd
	 * @param handler
	 * @return
	 */
	public static String commandRemote(String mac, String cmd,boolean waitReturn) {

		String recvData = "";
		String url = "";
		if(waitReturn)
		{
			url = String.format(Config.REMOTE_SERVER_CMD_URL, mac, cmd,Config.RETURN_DATA.YES.toString());
		}else
		{
			url = String.format(Config.REMOTE_SERVER_CMD_URL, mac, cmd,Config.RETURN_DATA.NO.toString());
		}
		Log.i(TAG, "url-->"+url);
		try {
			recvData = HtmlUtil.getContent(url, "utf-8");
			if(!StringUtils.isBlank(recvData))
			{
				recvData = DataProcessUtil.processResponseDataFromDevice(recvData);
				recvData = recvData.replace("response", "Response");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("远程遥控结果-->" + recvData);

		return recvData;
	}

	/**
	 * 更新界面,远程执行执行命令并获得返回结果
	 * @param cmd
	 * @param handler
	 * @param what
	 * @return
	 */
	public static String commandRemote(String mac, String cmd,boolean waitReturn, Handler handler,int what) {

		String recvData = "";
		String url = "";
		if(waitReturn)
		{	
			url = String.format(Config.REMOTE_SERVER_CMD_URL, mac, cmd,Config.RETURN_DATA.YES.toString());
		}else
		{
			url = String.format(Config.REMOTE_SERVER_CMD_URL, mac, cmd,Config.RETURN_DATA.NO.toString());
		}
		Log.i(TAG, "url-->"+url);
		try {
			recvData = HtmlUtil.getContent(url, "utf-8");
			if(!StringUtils.isBlank(recvData))
			{
				recvData = DataProcessUtil.processResponseDataFromDevice(recvData);
				recvData = recvData.replace("response", "Response");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Message msg = new Message();
		msg.what = what;
		Bundle b = new Bundle();
		b.putString("result", recvData);
		msg.setData(b);
		handler.sendMessage(msg);
		System.out.println("远程遥控结果-->" + recvData);

		return recvData;
	}
	
	/**
	 * 更新界面,远程执行执行命令并获得返回结果
	 * @param cmd
	 * @param handler
	 * @param what
	 * @return
	 */
	public static String commandRemote(String mac, String cmd,boolean waitReturn, Handler handler,int what,int dataFormat) {

		String recvData = "";
		String url = "";
		if(waitReturn)
		{	
			url = String.format(Config.REMOTE_SERVER_CMD_URL+"&dataFormat=%s", mac, cmd,Config.RETURN_DATA.YES.toString(),dataFormat);
		}else
		{
			url = String.format(Config.REMOTE_SERVER_CMD_URL+"&dataFormat=%s", mac, cmd,Config.RETURN_DATA.NO.toString(),dataFormat);
		}
		Log.i(TAG, "url-->"+url);
		try {
			recvData = HtmlUtil.getContent(url, "utf-8");
			if(!StringUtils.isBlank(recvData))
			{
				recvData = DataProcessUtil.processResponseDataFromDevice(recvData);
				recvData = recvData.replace("response", "Response");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Message msg = new Message();
		msg.what = what;
		Bundle b = new Bundle();
		b.putString("result", recvData);
		msg.setData(b);
		if(handler != null){
			handler.sendMessage(msg);
		}
		System.out.println("远程遥控结果-->" + recvData);

		return recvData;
	}
	
	
	/**
	 * 更新界面,远程执行执行命令并获得返回结果
	 * @param cmd
	 * @param handler
	 * @param what
	 * @return
	 */
	public static String commandRemote(Wifi wifi, String cmd,boolean waitReturn, Handler handler,int what) {

		String recvData = "";
		String url = "";
		if(waitReturn)
		{
			url = String.format(Config.REMOTE_SERVER_CMD_URL, wifi.getMac(), cmd,Config.RETURN_DATA.YES.toString());
		}else
		{
			url = String.format(Config.REMOTE_SERVER_CMD_URL, wifi.getMac(), cmd,Config.RETURN_DATA.NO.toString());
		}
		Log.i(TAG, "url-->"+url);
		try {
			recvData = HtmlUtil.getContent(url, "utf-8");
			if(!StringUtils.isBlank(recvData))
			{
				recvData = DataProcessUtil.processResponseDataFromDevice(recvData);
				recvData = recvData.replace("response", "Response");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Message msg = new Message();
		msg.what = what;
		Bundle b = new Bundle();
		b.putString("result", recvData);
		b.putSerializable("wifi", wifi);
		msg.setData(b);
		handler.sendMessage(msg);
		System.out.println("远程遥控结果-->" + recvData);

		return recvData;
	}

	
	
	


	/**
	 * 检查本地网络wifi是否在线
	 * @param broadcastIp
	 * @param handler
	 * @return
	 */
	public static String checkWifiOnlineLocal(String broadcastIp, Handler handler,int what) {
		String recvData = "";
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		String cmd = Config.CMD_SYSINFO;
		try {
			Log.i(TAG, "发送命令-->" + cmd+";broadcastIp-->"+broadcastIp);
			byte[] data = cmd.getBytes();

			socket = new DatagramSocket();
			socket.setBroadcast(true); // 有没有没啥不同
			// send端指定接受端的端口，自己的端口是随机的
			packet = new DatagramPacket(data, data.length,
					InetAddress.getByName(broadcastIp),
					Config.BROADCAST_PORT);

			socket.send(packet);
			
			Message msg = new Message();
			Bundle b = new Bundle();

			socket.receive(inPacket);
	
			recvData = new String(inBuff, 0, inPacket.getLength());
			if(!StringUtils.isBlank(recvData))
			{
				recvData = DataProcessUtil.processResponseDataFromDevice(recvData);
				recvData = recvData.replace("response", "Response");
			}
			msg.what = what;
			b.putString("result", recvData);
			msg.setData(b);
			handler.sendMessage(msg);
			Log.i(TAG, "接收数据-->" + recvData);
			socket.close();
		} catch (Exception ex)
		{
			Log.i(TAG, "ex-->"+ex.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}

		return recvData;
	}
	
	/**
	 * 检查远程网络wifi是否在线
	 * @param mac
	 * @param handler
	 * @return
	 */
	public static String checkWifiOnlineRemote(String mac, Handler handler,int what) {
		String recvData = "";
		String url = String.format(Config.REMOTE_SERVER_CMD_URL, mac, Config.CMD_SYSINFO,Config.RETURN_DATA.YES.toString());
		Log.i(TAG, "url-->"+url);
		try {
			recvData = HtmlUtil.getContent(url, "utf-8");
			if(!StringUtils.isBlank(recvData))
			{
				recvData = DataProcessUtil.processResponseDataFromDevice(recvData);
				recvData = recvData.replace("response", "Response");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putString("result", recvData);
		msg.what = what;
		msg.setData(b);
		handler.sendMessage(msg);
		System.out.println("远程遥控结果-->" + recvData);

		return recvData;
	}
	
	
	/**
	 * 更新界面,远程执行执行命令并获得返回结果
	 * @param cmd
	 * @param handler
	 * @param what
	 * @return
	 */
	public static String commandRemoteCheck(Wifi wifi, Handler handler,int what) {

		String recvData = "";
		String url = "";
		
		url = String.format(Config.REMOTE_SERVER_CHECK_URL, wifi.getMac());
		
		Log.i(TAG, "url-->"+url);
		try {
			recvData = HtmlUtil.getContent(url, "utf-8");
			if(!StringUtils.isBlank(recvData))
			{
				recvData = DataProcessUtil.processResponseDataFromDevice(recvData);
				recvData = recvData.replace("response", "Response");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Message msg = new Message();
		msg.what = what;
		Bundle b = new Bundle();
		b.putString("result", recvData);
		b.putSerializable("wifi", wifi);
		msg.setData(b);
		handler.sendMessage(msg);
		System.out.println("远程遥控结果-->" + recvData);

		return recvData;
	}

	
	
	/**
	 * 检查本地网络wifi是否在线
	 * @param broadcastIp
	 * @param handler
	 * @return
	 */
	public static String checkWifiOnlineLocalWithTimeout(String broadcastIp, Handler handler,int what) {
		String recvData = "";
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		String cmd = Config.CMD_SYSINFO;
		try {
			Log.i(TAG, "发送命令-->" + cmd+";broadcastIp-->"+broadcastIp);
			byte[] data = cmd.getBytes();

			socket = new DatagramSocket();
			socket.setBroadcast(true); // 有没有没啥不同
			// send端指定接受端的端口，自己的端口是随机的
			packet = new DatagramPacket(data, data.length,
					InetAddress.getByName(broadcastIp),
					Config.BROADCAST_PORT);

			socket.send(packet);
			
			Message msg = new Message();
			Bundle b = new Bundle();
			try
			{
				socket.setSoTimeout(Config.SOCKET_TIMEOUT_MILLISECONDS);
				socket.receive(inPacket);
				recvData = new String(inBuff, 0, inPacket.getLength());
				if(!StringUtils.isBlank(recvData))
				{
					recvData = DataProcessUtil.processResponseDataFromDevice(recvData);
					recvData = recvData.replace("response", "Response");
				}
			}catch(Exception ex)
			{
				recvData ="";
			}
			msg.what = what;
			b.putString("result", recvData);
			msg.setData(b);
			handler.sendMessage(msg);
			Log.i(TAG, "接收数据-->" + recvData);
			socket.close();
		}catch (Exception ex)
		{
			//Log.i(TAG, "ex-->"+ex.getMessage());
			ex.printStackTrace();
		} finally {
			if (socket != null) {
				socket.close();
			}
		}

		return recvData;
	}
	
	/**
	 * 检查远程网络wifi是否在线
	 * @param mac
	 * @param handler
	 * @return
	 */
	public static String checkWifiOnlineRemoteWithTimeout(String mac, Handler handler,int what) {
		String recvData = "";
		String url = String.format(Config.REMOTE_SERVER_CMD_URL, mac, Config.CMD_SYSINFO,Config.RETURN_DATA.YES.toString());
		Log.i(TAG, "url-->"+url);
		try {
			recvData = HtmlUtil.getContent(url, "utf-8");
			if(!StringUtils.isBlank(recvData))
			{
				recvData = DataProcessUtil.processResponseDataFromDevice(recvData);
				recvData = recvData.replace("response", "Response");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			recvData = "";
		}
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putString("result", recvData);
		msg.what = what;
		msg.setData(b);
		handler.sendMessage(msg);
		System.out.println("远程遥控结果-->" + recvData);

		return recvData;
	}
	/**
	 * 指定时间内检测设备是否在线，包括远程和内网
	 * @param mac
	 * @param broadcastIp
	 * @param handler
	 * @param what
	 * @return
	 */
	public static boolean checkWifiOnlineWithTimeout(String mac,String broadcastIp,Handler handler,int what)
	{
		String recvData = "";
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		String cmd = Config.CMD_SYSINFO;
		boolean flag = false;
		try {
			Log.i(TAG, "发送命令-->" + cmd+";broadcastIp-->"+broadcastIp);
			byte[] data = cmd.getBytes();

			socket = new DatagramSocket();
			socket.setBroadcast(true); // 有没有没啥不同
			// send端指定接受端的端口，自己的端口是随机的
			packet = new DatagramPacket(data, data.length,
					InetAddress.getByName(broadcastIp),
					Config.BROADCAST_PORT);

			socket.send(packet);
			
			Message msg = new Message();
			Bundle b = new Bundle();
			try
			{
				socket.setSoTimeout(Config.SOCKET_TIMEOUT_MILLISECONDS);
				socket.receive(inPacket);
				recvData = new String(inBuff, 0, inPacket.getLength());
				if(!StringUtils.isBlank(recvData))
				{
					recvData = DataProcessUtil.processResponseDataFromDevice(recvData);
					recvData = recvData.replace("response", "Response");
				}
			}catch(Exception ex)
			{
				recvData = "";
			}
			
			if(CheckUtil.isBlank(recvData))
			{
				String url = String.format(Config.REMOTE_SERVER_CMD_URL, mac, Config.CMD_SYSINFO,Config.RETURN_DATA.YES.toString());
				Log.i(TAG, "url-->"+url);
				try {
					recvData = HtmlUtil.getContent(url, "utf-8");
					recvData = DataProcessUtil.processResponseDataFromDevice(recvData);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					recvData = "";
				}
			}else
			{
				Log.i(TAG, "设备不在当前网络中!");
			}
			
			msg.what = what;
			b.putString("result", recvData);
			msg.setData(b);
			handler.sendMessage(msg);
			Log.i(TAG, "接收数据-->" + recvData);
			socket.close();
		}catch (Exception ex)
		{
			Log.i(TAG, "ex-->"+ex.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
			
			if(CheckUtil.isBlank(recvData))
			{
				flag = false;
			}else
			{
				flag = true;
			}
		}
		
		return flag;
	
	}
	
	
	
	public static String setNetConn(String content) {

		String cmd = Config.CMD_SETNETCONN + "=" + "{\"Domain\":\""
				+ Config.REMOTE_SERVER_DOMAIN + "\",\"IpAddr\":\""
				+ Config.REMOTE_SERVER_IP + "\",\"Proto\":0,\"Port\":"
				+ Config.REMOTE_SERVER_PORT + "}";

		String result = commandWifi(cmd, false);

		return result;

	}
	
	public static String setNetConnWithTimeout(String content) {

		String cmd = Config.CMD_SETNETCONN + "=" + "{\"Domain\":\""
				+ Config.REMOTE_SERVER_DOMAIN + "\",\"IpAddr\":\""
				+ Config.REMOTE_SERVER_IP + "\",\"Proto\":0,\"Port\":"
				+ Config.REMOTE_SERVER_PORT + "}";

		String result = commandWifiWithTimeout(cmd);

		return result;

	}

}
