package com.tianzun.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Controller  implements Serializable{

	
	private int id;
	private int wifiId;
	private int controllerCategoryId;

	private String name;
	private int count=0;
	private String modelName;
	private String url;
	private String templateCode="";
	private String c3Rule;
	private String module;
	
	
	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	public String getC3Rule() {
		return c3Rule;
	}

	public void setC3Rule(String c3Rule) {
		this.c3Rule = c3Rule;
	}

	public  Controller()
	{
		
	}

	public Controller(int wifiId, int controllerCategoryId,
			String name) {
		super();

		this.wifiId = wifiId;
		this.controllerCategoryId = controllerCategoryId;

		this.name = name;
	}
	
	public Controller(int wifiId, int controllerCategoryId,
			String name,int count) {
		super();

		this.wifiId = wifiId;
		this.controllerCategoryId = controllerCategoryId;
		this.count = count;
		this.name = name;
	}
	
	public Controller(int wifiId, int controllerCategoryId,
			String name,int count,String modelName) {
		super();

		this.wifiId = wifiId;
		this.controllerCategoryId = controllerCategoryId;
		this.count = count;
		this.name = name;
		this.modelName = modelName;
	}
	
	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

	public int getWifiId() {
		return wifiId;
	}
	public void setWifiId(int wifiId) {
		this.wifiId = wifiId;
	}
	public int getControllerCategoryId() {
		return controllerCategoryId;
	}
	public void setControllerCategoryId(int controllerCategoryId) {
		this.controllerCategoryId = controllerCategoryId;
	}


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Controller(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
//	public static List<Controller> list =null;
//	
//	static
//	{
//		list = new ArrayList<Controller>();
//		list.add(new Controller(1,2,"测试1"));
//		list.add(new Controller(1,2,"测试2"));
//		list.add(new Controller(1,2,"测试3"));
//	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name;
	}

	
	
}
