package net.wyxj.broadcast;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import net.wyxj.broadcast.BroadcastManager.BMReceiveThread;
import net.wyxj.transfer.Constant;

/**
 * 主机管理类，负责监听主机上线和下线消息，并负责处理
 * 
 * @author 辉
 * 
 */
public class HostManager implements Runnable {

	/** 多少次休眠广播一次在线消息 */
	public static final int TIMES_ONLINE = 10;
	/** 多少次休眠检查一次在线列表 */
	public static final int TIMES_CHECK = 10;
	/** 允许最大多少次无应答 */
	public static final int TIMES_RETRY = 50;
	/** 每次休眠的时间，单位 ms */
	public static final int SLEEP_TIME = 100;

	// 在线的主机列表
	private Set<HostInfo> hostSet = null;
	// 在线主机心跳表
	private HashMap<HostInfo, Long> lastMsgTime = null;
	// 本机信息
	private HostInfo host = null;
	// 默认的广播端口
	private int defaultPort = Constant.DEFAULT_BROADCAST_PORT;
	// 广播消息管理器
	private IBroadcastManager manager = null;

	public HostInfo getSelf() {
		return host;
	}

	public Set<HostInfo> getHostSet() {
		return hostSet;
	}

	public HostInfo[] getAllHost() {
		HostInfo[] hosts = new HostInfo[hostSet.size()];
		synchronized (hostSet) {
			Iterator<HostInfo> next = hostSet.iterator();
			int count = 0;
			while (next.hasNext()) {
				hosts[count++] = next.next();
			}
		}
		return hosts;
	}

	public HostManager() {
		hostSet = Collections.synchronizedSet(new HashSet<HostInfo>());
		lastMsgTime = new HashMap<HostInfo, Long>();
	}

	private void initHost() {
		String address = CommonUtils.getMainIPAddress();
		host = new HostInfo(address, Constant.DEFAULT_LOCAL_PORT);
		try {
			host.setName(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			host.setName("UNKNOWN-HOST");
		}
	}

	@Override
	public void run() {
		// 首先初始化 host , 如果先设置广播监听器
		// 那么在host初始化完成前一旦收到广播消息将会导致空指针异常
		initHost();
		Message msg = new Message();
		msg.setHost(host);
		// 初始化广播消息管理器
		manager = BroadcastManager.newInstance();
		manager.addListener(boradcastListener);
		manager.listenPort(defaultPort);
		// 发送上线消息
		msg.setType(Message.MSG_ADD);
		manager.sendBroadcastMessage(defaultPort, msg);
		int count1 = 0;
		int count2 = 0;
		while (!exit) {
			// 每次循环都要进行的处理
			if (!echo()) {
				break;
			}
			// 发送在线消息
			if (count1 >= HostManager.TIMES_ONLINE) {
				count1 = 0;
				msg.setType(Message.MSG_ONLINE);
				manager.sendBroadcastMessage(defaultPort, msg);
			}
			// 检查所有的主机是否断线超过规定时间
			if (count2 >= HostManager.TIMES_CHECK) {
				count2 = 0;
				synchronized (hostSet) {
					Iterator<HostInfo> next = hostSet.iterator();
					while (next.hasNext()) {
						HostInfo info = next.next();
						Long time = lastMsgTime.get(info);
						if ((System.currentTimeMillis() - time) > (HostManager.TIMES_RETRY * HostManager.SLEEP_TIME)) {
							next.remove();
							lastMsgTime.remove(info);
							if (hostListener != null) {
								hostListener.onDelete(info, Reason.TIMEOUT);
							}
						}
					}
				}
			}
			count1++;
			count2++;
			try {
				Thread.sleep(HostManager.SLEEP_TIME);
			} catch (InterruptedException e) {
				break;
			}
		}
		// 发送下线消息
		msg.setType(Message.MSG_DELETE);
		manager.sendBroadcastMessage(defaultPort, msg);
		synchronized (hostSet) {
			hostSet.clear();
		}
		lastMsgTime.clear();
		manager.removeAllPort();
		manager.removeAllListener();
		manager.destroy();
	}

	/**
	 * 每次循环都要进行的处理 因为在主循环中动作已经固定，故将更改置于新的函数中
	 * @return 返回 false 表示发生异常，主循环需要退出
	 */
	private boolean echo() {
		// 发送消息队列中待发送的消息
		while (msgQueue.size() > 0) {
			try {
				Message msg0 = msgQueue.take();
				msg0.setHost(getSelf());
				manager.sendBroadcastMessage(defaultPort, msg0);
			} catch (InterruptedException e) {
				return false;
			}
		}
		return true;
	}

	private LinkedBlockingQueue<Message> msgQueue = new LinkedBlockingQueue<Message>();

	public boolean sendMessage(Message msg) {
		if (thread != null && thread.isAlive()) {
			try {
				msgQueue.put(msg);
			} catch (InterruptedException e) {
				return false;
			}
			return true;
		}
		return false;
	}

	private Thread thread = null;

	public void start() {
		if (thread != null) {
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
		public boolean receiveData(BMReceiveThread receiveThread,
				DatagramPacket packet) {
			String str = new String(packet.getData(), 0, packet.getLength());
			Message msg = Message.fromString(str);
			// 不接收自身发出的消息
			if (msg != null && !host.equals(msg.getHost())) {
				HostInfo info = msg.getHost();
				switch (msg.getType()) {
				// 上线广播
				case Message.MSG_ADD:
					boolean res0 = false;
					synchronized (hostSet) {
						res0 = hostSet.add(info);
					}
					/*
					 * res0 == false 意味着该主机短暂掉线过。 或者下线后立刻重新启动，但是下线消息本机未收到。
					 * 但是策略是一致的，都更新心跳表，并且进行即时广播。
					 */
					lastMsgTime.put(info, System.currentTimeMillis());
					// 收到新主机上线消息，进行一次即时广播
					msg.setType(Message.MSG_NOTIFY);
					manager.sendBroadcastMessage(defaultPort, msg);
					if (res0 && hostListener != null) {
						hostListener.onAdd(info, Reason.LOGIN);
					}
					break;
				// 即时广播
				case Message.MSG_NOTIFY:
					boolean res1 = false;
					synchronized (hostSet) {
						res1 = hostSet.add(info);
					}
					lastMsgTime.put(info, System.currentTimeMillis());
					if (res1 && hostListener != null) {
						hostListener.onAdd(info, Reason.ALREADY);
					}
					break;
				// 在线广播
				case Message.MSG_ONLINE:
					boolean res2 = false;
					synchronized (hostSet) {
						res2 = hostSet.add(info);
					}
					lastMsgTime.put(info, System.currentTimeMillis());
					if (res2 && hostListener != null) {
						hostListener.onAdd(info, Reason.ONLINE);
					}
					break;
				// 下线广播
				case Message.MSG_DELETE:
					boolean res3 = false;
					synchronized (hostSet) {
						res3 = hostSet.remove(info);
					}
					if (res3) {
						lastMsgTime.remove(info);
					}
					if (res3 && hostListener != null) {
						hostListener.onDelete(info, Reason.EXIT);
					}
					break;
				default:
					if (processMessage(msg) && hostListener != null) {
						hostListener.onDefault(info, msg);
					}
					break;
				}
			}
			return true;
		}
	};

	/**
	 * 该函数用于处理非四个基本消息外的其它消息，只有该函数返回true，才会将消息继续传递给用户设置的监听器。
	 * 
	 * @param msg
	 * @return
	 */
	private boolean processMessage(Message msg) {

		return true;
	}

	public HostListener getHostListener() {
		return hostListener;
	}

	public void setHostListener(HostListener hostListener) {
		this.hostListener = hostListener;
	}

}
