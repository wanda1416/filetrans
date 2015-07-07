package net.wyxj.broadcast;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonSyntaxException;

/**
 * 所有消息类的基类
 * 
 * @author wanda1416
 * 
 */
public class Message {
	public static final int MSG_NULL = 0x00;

	// 标准消息定义，分为四种广播：
	// 上线广播/即时广播/定时广播/下线广播 用于标识主机在线状态
	public static final int MSG_ADD 	= 0x01;	
	public static final int MSG_NOTIFY 	= 0x02;
	public static final int MSG_ONLINE 	= 0x03;
	public static final int MSG_DELETE 	= 0x04;

	// 
	/**
	 * 消息类型
	 */
	private int type;
	
	/**
	 * 消息代表的主机
	 */
	private HostInfo host;

	/**
	 * 消息内容
	 */
	private Map<String, Object> map;

	public Message() {
		type = MSG_NULL;
		host = null;
		map = new HashMap<String, Object>();
	}

	public HostInfo getHost() {
		return host;
	}

	public void setHost(HostInfo host) {
		this.host = host;
	}

	public int getType() {
		return type;
	}

	public void setType(int header) {
		this.type = header;
	}

	public void put(String key, Object value) {
		map.put(key, value);
	}

	public void putString(String key, String value) {
		map.put(key, value);
	}

	public void putInteger(String key, int value) {
		map.put(key, value);
	}

	public void putBoolean(String key, boolean value) {
		map.put(key, value);
	}

	public void putDouble(String key, double value) {
		map.put(key, value);
	}

	public void putLong(String key, long value) {
		map.put(key, value);
	}

	public void putFloat(String key, float value) {
		map.put(key, value);
	}

	public void putByte(String key, byte value) {
		map.put(key, value);
	}

	public void putChar(String key, char value) {
		map.put(key, value);
	}

	public Object get(String key) {
		return map.get(key);
	}

	public String getString(String key) {
		return (String) map.get(key);
	}

	public int getInteger(String key) {
		return (int) map.get(key);
	}

	public boolean getBoolean(String key) {
		return (boolean) map.get(key);
	}

	public double getDouble(String key) {
		return (double) map.get(key);
	}

	public long getLong(String key) {
		return (long) map.get(key);
	}

	public float getFloat(String key) {
		return (float) map.get(key);
	}

	public byte getByte(String key) {
		return (byte) map.get(key);
	}

	public char getChar(String key) {
		return (char) map.get(key);
	}

	public String toString() {
		return CommonUtils.gson.toJson(this);
	}

	public static Message fromString(String json) {
		Message info = null;
		try {
			info = CommonUtils.gson.fromJson(json, Message.class);
		} catch (JsonSyntaxException e) {
			return null;
		}
		return info;
	}

	public void clear(){
		map.clear();
	}
}
