package com.tianzun.util;

import java.net.Socket;

import com.tianzun.control.ModuleThread;


public class Config {
	
	public static final int WIFI_RETRY_TIMES = 10;
	public static final int WIFI_OPEN_SLEEP_MILLISECONDS = 1000;
	public static final int WIFI_CLOSE_SLEEP_MILLISECONDS = 1000;
	public static final int WIFI_SCAN_SLEEP_MILLISECONDS = 500;
	public static final int AP_MAX_MISS_TIME = 1000;
	public enum WifiCipherType  
	{  
	    WIFICIPHER_WEP,WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID  
	}  
	public static final int SOCKET_TIMEOUT_MILLISECONDS = 4*1000;
	public static final int SET_WIFI_CONFIG_WAIT_TIME = 8*1000;	//未来将要将参数设定在服务器上的参数
	public static final String MY_CONTROLLER = "my_controller";
	public static final int STUDY_WAIT_INTERVAL = 1000;
	public static final int STUDY_WAIT_COUNT = 120;
	public static final String SUCCESS_ERROR_CODE = "000";

	public static final int BTN_PRESS_VIBE = 200;
	
	public static final int WHAT_ERROR = -1;
	public static final int WHAT_DEFAULT = 0;
	public static final int WHAT_UPDATE_OK = 1;
	public static final int WHAT_CHECK_WIFI_ONLINE = 4;
	public static final int WHAT_GETLIGHT = 101;
	public static final int WHAT_SUCESS = 1001;
	public static final int WHAT_FAIL = 1002;

	public static final int CONFIG_WIFI_TRY_COUNT = 800;

	public static enum IDENTIFIER{冰箱,电脑,电视,风扇,空调,热水器,吸尘器,洗衣机,音响};
	
	public static final String 	BROADCAST_IP		= 	"192.168.4.1";			//连接遥控设备是的广播地址
	public static final int		BROADCAST_PORT		= 	8268;					//连接遥控设备是的广播端口
	
	public static final String 	BROADCAST_IP_DEFAULT	= 	"192.168.1.255";	//发送同网段默认的广播地址
	public static final int		BROADCAST_PORT_DEFAULT	= 	3600;				//发送同网段默认的广播端口
	
	public static final int		MODULE_TCP_PORT			=	8100;				//连接模块的TCP端口号
	
	public static final String 	ROUTER_BROADCAST_IP		= 	"192.168.0.255";	//连接路由器的广播地址
	public static final int		ROUTER_BROADCAST_PORT	= 	8268;				//连接路由器的广播端口

	public static final String	ROUTER_WIFI		= "router_wifi";
	public static final String	CONFIG_DATA		= "config_data";	 		
	public static final String	WIFI_SSID	=	"wifi_ssid";
	public static final String	WIFI_BSSID	=	"wifi_bssid";
	public static final String	FIRST_CONFIG = "is_first_config";
	public static final String	ROUTER_BROADCAST	= "router_broadcast";
	public static enum	MODE{NORMAL,STUDY};
	public static enum 	RETURN_DATA{NO,YES};
	public static final String 	CMD_SYSINFO = 	"sysinfo";
	public static final String 	CMD_GETWIFICONF	=	"getwificonf";
	public static final String 	CMD_SETWIFICONF	=	"setwificonf";
	public static final String 	CMD_SETNETCONN	=	"setnetconn";
	public static final String 	CMD_APDOWN = "apdown";
	public static final String 	INVALID_IP_ADDRESS = "0.0.0.0";
	public static final String 	REMOTE_SERVER_IP	= 	"112.74.106.108";
	
	public static final String VERSION_URL = "http://112.74.106.108:8088/update/version.html";
	public static final String APK_URL = "http://112.74.106.108:8088/update/controller.apk";
	public static final String 	SERVER_DOMAIN 		= "http://112.74.106.108:8088/";
	public static final String 	REMOTE_SERVER_CMD_URL	= 	"http://112.74.106.108:8088/command/cmd.do?mac=%s&cmd=%s&returnData=%s"; //远程遥控时操作API
	public static final String 	REMOTE_SERVER_CHECK_URL	= 	"http://112.74.106.108:8088/command/check.do?mac=%s"; //远程遥控时操作API
	
	public static final String 	REMOTE_SERVER_DOMAIN	= 	"www";
	public static final int 	REMOTE_SERVER_PORT	= 	8007;
	
	public static ModuleThread ModuleThread = null;
	public static Socket modulesocket = null;
	
}
