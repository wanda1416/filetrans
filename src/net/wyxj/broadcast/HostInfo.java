package net.wyxj.broadcast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class HostInfo {
	
	public static final int MSG_ADD = 0x01;
	public static final int MSG_NOTIFY = 0x02;
	public static final int MSG_ONLINE = 0x03;
	public static final int MSG_DELETE = 0x04;

	private int type;
	
	private String name;
	
	private String address;
	
	private int port;

	public int getType() {
		return type;
	}

	public HostInfo setType(int type) {
		this.type = type;
		return this;
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
	public boolean equals(Object obj){
		if(!(obj instanceof HostInfo)){
			return false;
		}
		HostInfo info = (HostInfo)obj;
		if(this.address.equals(info.address) && this.port == info.port){
			return true;
		}
		return false;		
	}
	
	@Override
	public int hashCode(){
		return address.hashCode() + port<<16;
	}
	
	public static Gson gson = new Gson();
	
	public String toString(){
		return gson.toJson(this);
	}

	public static HostInfo fromString(String json) {
		HostInfo info = null;
		try {
			info = gson.fromJson(json, HostInfo.class);
		} catch (JsonSyntaxException e) {
			return null;
		}
		return info;
	}
}
