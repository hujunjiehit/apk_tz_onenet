package com.tianzun.util;

import java.io.Serializable;

public class Wifi implements Serializable{

private static final long serialVersionUID = 1L;
private int routerid;
public int getRouterid() {
	return routerid;
}
public void setRouterid(int routerid) {
	this.routerid = routerid;
}

private String SSID;
public String getSSID() {
	return SSID;
}
public void setSSID(String sSID) {
	SSID = sSID;
}

private boolean Online=false;
public boolean isOnline() {
	return Online;
}
public void setOnline(boolean online) {
	Online = online;
}
private boolean Open=false;

public boolean isOpen() {
	return Open;
}
public void setOpen(boolean open) {
	Open = open;
}

private String name;
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}

private String ip;
public String getIp() {
	return ip;
}
public void setIp(String ip) {
	this.ip = ip;
}

private String mac;
public String getMac() {
	return mac;
}
public void setMac(String mac) {
	this.mac = mac;
}


private String BSSID;
public String getBSSID() {
	return BSSID;
}
public void setBSSID(String bSSID) {
	BSSID = bSSID;
}
private String catid;
public String getCatid() {
	return catid;
}
public void setCatid(String catid) {
	this.catid = catid;
}

public Wifi(String mac, String ssid) {
	super();
	this.mac = mac;
	this.SSID = ssid;
}
public Wifi(String mac, String ssid, String ip,int routerId,String bssid) {
	super();
	this.name = ssid;
	this.mac = mac;
	this.SSID = ssid;
	this.ip = ip;
	this.routerid = routerId;
	this.BSSID = bssid;
}
public Wifi(String name, String catid, String ip) {
	super();
	this.name = name;
	this.catid= catid;
	this.ip=ip;
}
public Wifi() {
	super();
}

@Override
public String toString() {
	return SSID;
}
}
