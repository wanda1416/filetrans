package net.wyxj.broadcast;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import net.wyxj.broadcast.BroadcastManager.ReceiveThread;
import net.wyxj.transfer.Constant;

/**
 * 主机管理类，负责监听主机上线和下线消息，并负责处理
 * 
 * @author 辉
 * 
 */
public class HostManager implements Runnable {

	// 不包含自身 HostInfo
	private HashSet<HostInfo> hostSet = null;
	private HashMap<HostInfo,Long> lastMsgTime = null;
	
	private HostInfo host = null;
	private int defaultPort = Constant.DEFAULT_BROADCAST_PORT;
	private IBroadcastManager manager = null;
	
	public HostInfo getSelf(){
		return host;
	}
	
	public HashSet<HostInfo> getHostSet(){
		return hostSet;
	}
	
	public HostInfo[] getAllHost(){
		HostInfo [] hosts = new HostInfo[hostSet.size()];
		Iterator<HostInfo> next = hostSet.iterator();
		int count = 0;
		while(next.hasNext()){
			hosts[count++] = next.next();
		}
		return hosts;
	}
	
	public HostManager() {
		hostSet = new HashSet<HostInfo>();
		lastMsgTime = new HashMap<HostInfo,Long>();
	}

	public static final int TIMES_ONLINE = 1;

	public static final int TIMES_RETRY = 10;
	
	public static final int SLEEP_TIME = 1000;
	
	@Override
	public void run() {
		manager = BroadcastManager.newInstance();
		manager.addListener(boradcastListener);
		manager.listenPort(defaultPort);
		host = new HostInfo();
		host.setPort(Constant.DEFAULT_LOCAL_PORT);
		host.setAddress(BroadcastManager.getMainIPAddress());
		try {
			host.setName(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			host.setName("UNKNOWN-HOST");
		}			
		// 发送上线消息
		manager.sendBroadcastMessage(defaultPort, host
				.setType(HostInfo.MSG_ADD).toString());	
		int count1 = 0;
		while (!exit) {
			if (count1 >= HostManager.TIMES_ONLINE) {
				count1 = 0;
				// 发送在线消息
				manager.sendBroadcastMessage(defaultPort,
						host.setType(HostInfo.MSG_ONLINE).toString());
			}
			// 检查所有的主机是否断线超过规定时间
			Iterator<HostInfo> next = hostSet.iterator();
			while(next.hasNext()){
				HostInfo info = next.next();
				Long time = lastMsgTime.get(info);
				if ((System.currentTimeMillis() - time) >
						(HostManager.TIMES_RETRY * HostManager.SLEEP_TIME)) {
					next.remove();
					lastMsgTime.remove(info);
					if (hostListener != null) {
						hostListener.onDelete(info);
					}
				}
			}
			count1 ++;
			try {
				Thread.sleep(HostManager.SLEEP_TIME);
			} catch (InterruptedException e) {
				break;
			}	
		}		
		// 发送下线消息
		manager.sendBroadcastMessage(defaultPort,
				host.setType(HostInfo.MSG_DELETE).toString());
		hostSet.clear();
		lastMsgTime.clear();
		manager.removeAllPort();
		manager.removeAllListener();
		manager.destroy();
	}
	
	private Thread thread = null;
	
	public void start() {
		if (thread != null){
			shutdown();
		}
		exit = false;
		thread = new Thread(this);
		thread.start();
	}

	private boolean exit = false;

	public void shutdown() {
		if (thread == null) {
			return;
		}
		if (thread.isAlive()) {
			thread.interrupt();
		}
		exit = true;
		thread = null;
	}

	private HostListener hostListener = null;

	public IBroadcastManager.BoradcastListener boradcastListener = new IBroadcastManager.BoradcastListener() {
		@Override
		public boolean receiveData(ReceiveThread receiveThread,
				DatagramPacket packet) {
			String message = new String(packet.getData(), 0, packet.getLength());
			HostInfo info = HostInfo.fromString(message);
			// 不接收自身发出的消息
			if (info != null && !host.equals(info)) {
				switch (info.getType()) {
				case HostInfo.MSG_ADD:
					boolean res0 = hostSet.add(info);
					lastMsgTime.put(info, System.currentTimeMillis());
					manager.sendBroadcastMessage(defaultPort,
							host.setType(HostInfo.MSG_NOTIFY).toString());
					if (res0 && hostListener != null) {
						hostListener.onAdd(info);
					}
					break;
				case HostInfo.MSG_NOTIFY:
					boolean res1 = hostSet.add(info);
					lastMsgTime.put(info, System.currentTimeMillis());
					if (res1 && hostListener != null) {
						hostListener.onAdd(info);
					}
					break;
				case HostInfo.MSG_ONLINE:
					boolean res2 = hostSet.add(info);
					lastMsgTime.put(info, System.currentTimeMillis());
					if (res2 && hostListener != null) {
						hostListener.onAdd(info);
					}
					break;
				case HostInfo.MSG_DELETE:
					boolean res3 = hostSet.remove(info);
					if (res3){
						lastMsgTime.remove(info);
					}
					if (res3 && hostListener != null) {
						hostListener.onDelete(info);
					}
					break;
				default:
					break;
				}
			}
			return true;
		}
	};

	public HostListener getHostListener() {
		return hostListener;
	}

	public void setHostListener(HostListener hostListener) {
		this.hostListener = hostListener;
	}

}
