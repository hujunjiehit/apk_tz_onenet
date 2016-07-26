package com.tianzun.util;

public class LampTimer implements Comparable<LampTimer> {

public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}

public String getTime() {
	return time;
}
public void setTime(String time) {
	this.time = time;
}
public int getOp() {
	return op;
}
public void setOp(int op) {
	this.op = op;
}

public String getDay() {
	return day;
}
public void setDay(String day) {
	this.day = day;
}


public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}

public String getIp() {
	return ip;
}
public void setIp(String ip) {
	this.ip = ip;
}



private int id;
private String name;
private String day;
private String time;
private int op;
private String ip;
private Wifi wifi;

public Wifi getWifi() {
	return wifi;
}
public void setWifi(Wifi wifi) {
	this.wifi = wifi;
}
@Override
public String toString() {
	return time;
}
@Override
public int compareTo(LampTimer another) {
	
    int compareName = this.time.compareTo(another.getTime()); //
    if (compareName == 0) {  
               return (this.name == another.getName() ? 0 : this.name.compareTo(another.getName()));  
          }  
           return compareName;

}
}
