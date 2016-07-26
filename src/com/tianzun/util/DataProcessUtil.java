/**
 * 
 */
package com.tianzun.util;

/**
 * @author lenovo
 *
 */
public class DataProcessUtil {
	
	/**
	 * 硬件通信协议有改动。除去16进制的12 byte header,1 byte crc8
	 * @return
	 */
	public static String processResponseDataFromDevice(String oriData){
		if(oriData==null || oriData.equals("")){
			return oriData;
		}
		if(!(oriData.trim().startsWith("{"))){//原始数据
			int start = oriData.indexOf("{");
			int end = oriData.lastIndexOf("}");
			if(start!=-1 && end!=-1 && start <=end+1 && oriData.length()>=end+1){
				oriData = oriData.substring(start,end+1);
			}
		}
		//oriData = oriData.length()>24?oriData.substring(24) : oriData;
		return oriData;
		
		
	}
	public static void main(String[] args) {
		String oriData ="fdk123{\"Response\":{\"Station\":{\"Connect_Station\":{\"ssid\":\"GD711S\",\"password\":\"qazxswedcvfrt\"}}}}";
		oriData = DataProcessUtil.processResponseDataFromDevice(oriData);
		System.out.println(oriData);
	}
	

}
