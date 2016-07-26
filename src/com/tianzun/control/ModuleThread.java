package com.tianzun.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.tianzun.util.Config;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ModuleThread extends Thread {

	private ServerSocket socket;
	private ReceiveThread mReceiveThread = null;
	private static final String TAG = "ModuleThread";

	private Handler mHandler = null;
	private boolean stop = false;
	private String serverMsg;

	private OutputStream outStream = null;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();

		try {
			socket = new ServerSocket(Config.MODULE_TCP_PORT);
			Config.modulesocket = socket.accept();
			stop = true;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (Config.modulesocket != null) {
			Log.d(TAG, "connection OK");
			return;
		}

		mReceiveThread = new ReceiveThread(Config.modulesocket);
		stop = false;
		mReceiveThread.start();
	}

	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}

	public boolean isRun() {
		return !stop;
	}

	public String getMsg() {
		return serverMsg;
	}

	public void sendMsg(final byte[] cmd) {
		byte[] msgBuffer = null;
		Log.d(TAG, "get cmd" + cmd);
		
		msgBuffer = cmd;

		try {
			if (Config.modulesocket == null) {
				Log.d(TAG, "getmessage error");
				return;
			} else {
				outStream = Config.modulesocket.getOutputStream();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			outStream.write(msgBuffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void doMsg(int what, Bundle bundle) {
		Message msg = new Message();
		msg.what = what;
		if (bundle != null) {
			msg.setData(bundle);
		}
		if (mHandler != null)
			mHandler.sendMessage(msg);
	}

	protected void doMsg(int what) {
		doMsg(what, null);
	}

	public class ReceiveThread extends Thread {
		private InputStream inStream = null;

		private byte[] buf;
		private String str = null;

		ReceiveThread(Socket s) {
			try {
				this.inStream = s.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			while (!stop) {
				this.buf = new byte[4096];

				try {
					this.inStream.read(this.buf);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					this.str = new String(this.buf, "UTF-8").trim();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (str.length() == 0) {
					// stopSocketServer();
				}

				if (str.length() != 0) {
					Log.i(TAG, "get msg" + str + "!!!");
					Bundle bundle = new Bundle();
					bundle.putString("Message", str);
				}

			}
		}

	}

	private void stopSocketServer() {
		if (Config.modulesocket != null) {
			try {
				Config.modulesocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Config.ModuleThread = null;
		stop = true;
	}

	@Override
	@Deprecated
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
		stopSocketServer();
	}

}
