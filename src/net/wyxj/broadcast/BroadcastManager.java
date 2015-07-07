
package net.wyxj.broadcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 广播消息类，用于发送广播消息，同时监听收到的广播消息。
 * 
 * @author 辉
 * 
 */
public class BroadcastManager implements IBroadcastManager {

	private static final int BUFFER_SIZE = 8192;
	
	private	DatagramSocket defaultSendSocket = null;
	
	private InetAddress broadcastAddress = null;
	
	public static final Charset DEFAULT_ENCODE = Charset.forName("UTF-8");
	
	private BroadcastManager() {
		broadcastAddress = getBroadcastAddress();
		if( broadcastAddress == null){
			System.err.println("无法获得广播地址！");
			try {
				broadcastAddress = InetAddress.getByName("127.0.0.1");
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}else{
			System.err.println("广播地址:"+broadcastAddress.toString());
		}
		try {
			defaultSendSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		listenerList = new LinkedList<BoradcastListener>();
		map = new HashMap<Integer,ReceiveThread>();
	}
	
	@Override
	public synchronized boolean sendBroadcastMessage(int destPort, byte[] data) {
		if (data.length >= BUFFER_SIZE){
			return false;
		}
		DatagramPacket packet = new DatagramPacket(data, data.length,
				broadcastAddress, destPort);
		try {
			defaultSendSocket.send(packet);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	@Override
	public synchronized boolean sendBroadcastMessage(int destPort, String data) {
		return sendBroadcastMessage(destPort,
				data.getBytes(DEFAULT_ENCODE));
	}
	
	List<BoradcastListener> listenerList;
	
	@Override
	public void addListener(BoradcastListener listener) {
		listenerList.add(listener);
	}
	
	@Override
	public void removeListener(BoradcastListener listener) {
		listenerList.remove(listener);
	}

	@Override
	public void removeAllListener() {
		listenerList.clear();
	}

	public static IBroadcastManager newInstance() {
		return new BroadcastManager();
	}

	@Override
	public void destroy() {
		defaultSendSocket.close();
	}

	private HashMap<Integer,ReceiveThread> map = null;
	
	@Override
	public boolean listenPort(int port) {
		if(map.containsKey(port)){
			return false;
		}		
		ReceiveThread thread = new ReceiveThread(port);
		thread.start();
		map.put(port, thread);
		return true;
	}

	@Override
	public boolean removePort(int port) {
		if(!map.containsKey(port)){
			return false;
		}	
		map.get(port).exit();
		return true;
	}

	@Override
	public void removeAllPort() {
		for(ReceiveThread thread:map.values()){
			thread.exit();
		}
	}

	public class ReceiveThread extends Thread {
		
		private int port;
		
		public int getPort(){
			return port;
		}
	
		public ReceiveThread(int port){
			this.port = port;			
		}
		
		private boolean exit = false;
		
		@Override
		public void run(){			
			DatagramSocket socket = null;
			try {
				socket = new DatagramSocket(port);
				socket.setSoTimeout(100);
			} catch (SocketException e) {
				e.printStackTrace();
				map.remove(port);
				return;
			}
			byte[] buf = new byte[BUFFER_SIZE];
			DatagramPacket recvData = new DatagramPacket(buf, buf.length);
			while (!isExit()) {
				try {
					socket.receive(recvData);
				} catch (IOException e) {
					continue;
				}
				// 通知所有注册的监听器，收到了一个消息。
				for(BoradcastListener manager:listenerList){
					if(!manager.receiveData(this,recvData)){
						// 
					}
				}
			}
			socket.close();	
			map.remove(port);
		}

		public boolean isExit() {
			return exit;
		}

		public void exit() {
			exit(true);
		}
		
		public void exit(boolean interrupt) {
			if (interrupt && this.isAlive()){
				this.interrupt();
			}
			this.exit = true;
		}
		
	}

	public static ArrayList<String> getLocalIPList(){
		ArrayList<String> ipList = new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces
						.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ip = (InetAddress) addresses.nextElement();
					if (ip != null && ip instanceof Inet4Address) {
						ipList.add(ip.getHostAddress());
					}
				}
			}
		} catch (Exception e) {
			System.err.println("获取ip地址列表出错");
		}
		return ipList;
	}
	
	public static String getMainIPAddress(){
		String ip = null;
		ArrayList<String> ipList = getLocalIPList();
		for(int i=0;i<ipList.size();i++){
			if( ipList.get(i).equals("127.0.0.1") == false &&
					ipList.get(i).startsWith("192.168.") ){
				ip = ipList.get(i);
				break;
			}
		}
		if(ip == null){
			ip = "127.0.0.1";
		}
		return ip;
	}
	
	public static InetAddress getBroadcastAddress() {
		InetAddress address;
		try {
			address = InetAddress.getByName(getMainIPAddress());
		} catch (UnknownHostException e) {
			return null;
		}
		if (address != null) {
			String addr = address.getHostAddress();
			if(addr.equals("127.0.0.1")){
				return null;
			}
			String ip = addr.substring(0, addr.lastIndexOf("."));
			ip = ip + ".255";
			try {
				return InetAddress.getByName(ip);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}		
		return null;
	}
	
}
