package net.wyxj.broadcast;

import java.net.DatagramPacket;

import net.wyxj.broadcast.BroadcastManager.ReceiveThread;

/**
 * 定义了一个广播消息类需要实现的功能.
 * 功能包括：
 * 1. 发送广播消息到特定端口
 * 2. 监听特定的UDP端口，接收消息
 * 3. 设置一个或多个监听器，处理消息 
 * 
 * @author 辉
 *
 */
public interface IBroadcastManager {

	/**
	 * 监听广播消息的监听器
	 * @author 辉
	 *
	 */
	
	public interface BoradcastListener {

		public boolean receiveData(ReceiveThread receiveThread, final DatagramPacket packet);
		
	}
	
	public void addListener(BoradcastListener listener);
	
	public void removeListener(BoradcastListener listener);
	
	public void removeAllListener();
	
	public void destroy();
	
	// 发送消息

	public boolean sendBroadcastMessage(int port, byte[] data);
	
	public boolean sendBroadcastMessage(int port, String data);
	
	// 接收消息
	
	public boolean listenPort(int port);
	
	public boolean removePort(int port);
	
	public void removeAllPort();
	
	
}
