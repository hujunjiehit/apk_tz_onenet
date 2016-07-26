package com.tianzun.util;

public class Router {
private int id;
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}

private int remember;
public int getRemember() {
	return remember;
}
public void setRemember(int remember) {
	this.remember = remember;
}

private String SSID;
public String getSSID() {
	return SSID;
}
public void setSSID(String sSID) {
	SSID = sSID;
}
private String password;
public String getPassword() {
	return password;
}
public void setPassword(String password) {
	this.password = password;
}
public String mac;
public String getMac() {
	return mac;
}
public void setMac(String mac) {
	this.mac = mac;
}
public String centermac;

public String getCentermac() {
	return centermac;
}
public void setCentermac(String centermac) {
	this.centermac = centermac;
}
public String broadcastIP;
public String getBroadcastIP() {
	return broadcastIP;
}
public void setBroadcastIP(String broadcastIP) {
	this.broadcastIP = broadcastIP;
}
public Router(String mac, String ssid,String password,String broadcastIp) {
	super();

	this.mac = mac;
	this.SSID = ssid;
	this.password = password;
	this.broadcastIP = broadcastIp;
}
public Router(){
	super();
}
}
