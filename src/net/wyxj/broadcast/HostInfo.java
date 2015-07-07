package net.wyxj.broadcast;

import com.google.gson.JsonSyntaxException;

public class HostInfo {
	
	private String name = "UNKNOWN-HOST";
	
	private String address;
	
	private int port;
	
	public HostInfo(){
		address = "127.0.0.1";
		port = 0;
	}
	
	public HostInfo(String address, int port){
		this.address = address;
		this.port = port;
	}
	
	public HostInfo(String address, int port, String name){
		this.address = address;
		this.port = port;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public HostInfo setName(String name) {
		this.name = name;
		return this;
	}

	public String getAddress() {
		return address;
	}

	public HostInfo setAddress(String address) {
		this.address = address;
		return this;
	}

	public int getPort() {
		return port;
	}

	public HostInfo setPort(int port) {
		this.port = port;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof HostInfo) {
			HostInfo info = (HostInfo) obj;
			if (this.address.equals(info.address) && this.port == info.port) {
				return true;
			}
		}
		return false;		
	}
	
	@Override
	public int hashCode(){
		return address.hashCode() + port<<16;
	}
	
	public String toString(){
		return CommonUtils.gson.toJson(this);
	}

	public static HostInfo fromString(String json) {
		HostInfo info = null;
		try {
			info = CommonUtils.gson.fromJson(json, HostInfo.class);
		} catch (JsonSyntaxException e) {
			return null;
		}
		return info;
	}
}
