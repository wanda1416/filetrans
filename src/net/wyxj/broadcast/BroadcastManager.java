package net.wyxj.broadcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 广播消息类，用于发送广播消息，同时监听收到的广播消息。
 * 
 * @author 辉
 * 
 */
public class BroadcastManager extends AbstractBroadcastManager {

	private static final int BUFFER_SIZE = 8192;

	private DatagramSocket defaultSendSocket = null;

	private InetAddress broadcastAddress = null;

	private BroadcastManager() {
		broadcastAddress = CommonUtils.getBroadcastAddress();
		if (broadcastAddress == null) {
			System.err.println("无法获得广播地址！");
			try {
				broadcastAddress = InetAddress.getByName("127.0.0.1");
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("广播地址:" + broadcastAddress.toString());
		}
		try {
			defaultSendSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		listenerList = new LinkedList<BoradcastListener>();
		map = new HashMap<Integer, BMReceiveThread>();
	}

	@Override
	public synchronized boolean sendBroadcastMessage(int destPort, byte[] data) {
		if (data.length >= BUFFER_SIZE) {
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

	private HashMap<Integer, BMReceiveThread> map = null;

	@Override
	public boolean listenPort(int port) {
		if (map.containsKey(port)) {
			return false;
		}
		BMReceiveThread thread = new BMReceiveThread(port);
		thread.start();
		map.put(port, thread);
		return true;
	}

	@Override
	public boolean removePort(int port) {
		if (!map.containsKey(port)) {
			return false;
		}
		map.get(port).exit();
		return true;
	}

	@Override
	public void removeAllPort() {
		for (BMReceiveThread thread : map.values()) {
			thread.exit();
		}
	}

	public class BMReceiveThread extends Thread {

		private int port;

		public int getPort() {
			return port;
		}

		public BMReceiveThread(int port) {
			this.port = port;
		}

		private boolean exit = false;

		@Override
		public void run() {
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
				for (BoradcastListener manager : listenerList) {
					if (!manager.receiveData(this, recvData)) {
						// receiveData 需要返回true才能使得消息继续传递下去
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
			if (interrupt && this.isAlive()) {
				this.interrupt();
			}
			this.exit = true;
		}

	}

}
