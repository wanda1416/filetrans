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
 * ���������࣬��������������ߺ�������Ϣ����������
 * 
 * @author ��
 * 
 */
public class HostManager implements Runnable {

	/** ���ٴ����߹㲥һ��������Ϣ */
	public static final int TIMES_ONLINE = 10;
	/** ���ٴ����߼��һ�������б� */
	public static final int TIMES_CHECK = 10;
	/** ���������ٴ���Ӧ�� */
	public static final int TIMES_RETRY = 50;
	/** ÿ�����ߵ�ʱ�䣬��λ ms */
	public static final int SLEEP_TIME = 100;

	// ���ߵ������б�
	private Set<HostInfo> hostSet = null;
	// ��������������
	private HashMap<HostInfo, Long> lastMsgTime = null;
	// ������Ϣ
	private HostInfo host = null;
	// Ĭ�ϵĹ㲥�˿�
	private int defaultPort = Constant.DEFAULT_BROADCAST_PORT;
	// �㲥��Ϣ������
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
		// ���ȳ�ʼ�� host , ��������ù㲥������
		// ��ô��host��ʼ�����ǰһ���յ��㲥��Ϣ���ᵼ�¿�ָ���쳣
		initHost();
		Message msg = new Message();
		msg.setHost(host);
		// ��ʼ���㲥��Ϣ������
		manager = BroadcastManager.newInstance();
		manager.addListener(boradcastListener);
		manager.listenPort(defaultPort);
		// ����������Ϣ
		msg.setType(Message.MSG_ADD);
		manager.sendBroadcastMessage(defaultPort, msg);
		int count1 = 0;
		int count2 = 0;
		while (!exit) {
			// ÿ��ѭ����Ҫ���еĴ���
			if (!echo()) {
				break;
			}
			// ����������Ϣ
			if (count1 >= HostManager.TIMES_ONLINE) {
				count1 = 0;
				msg.setType(Message.MSG_ONLINE);
				manager.sendBroadcastMessage(defaultPort, msg);
			}
			// ������е������Ƿ���߳����涨ʱ��
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
		// ����������Ϣ
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
	 * ÿ��ѭ����Ҫ���еĴ��� ��Ϊ����ѭ���ж����Ѿ��̶����ʽ����������µĺ�����
	 * @return ���� false ��ʾ�����쳣����ѭ����Ҫ�˳�
	 */
	private boolean echo() {
		// ������Ϣ�����д����͵���Ϣ
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
			// ����������������Ϣ
			if (msg != null && !host.equals(msg.getHost())) {
				HostInfo info = msg.getHost();
				switch (msg.getType()) {
				// ���߹㲥
				case Message.MSG_ADD:
					boolean res0 = false;
					synchronized (hostSet) {
						res0 = hostSet.add(info);
					}
					/*
					 * res0 == false ��ζ�Ÿ��������ݵ��߹��� �������ߺ�������������������������Ϣ����δ�յ���
					 * ���ǲ�����һ�µģ����������������ҽ��м�ʱ�㲥��
					 */
					lastMsgTime.put(info, System.currentTimeMillis());
					// �յ�������������Ϣ������һ�μ�ʱ�㲥
					msg.setType(Message.MSG_NOTIFY);
					manager.sendBroadcastMessage(defaultPort, msg);
					if (res0 && hostListener != null) {
						hostListener.onAdd(info, Reason.LOGIN);
					}
					break;
				// ��ʱ�㲥
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
				// ���߹㲥
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
				// ���߹㲥
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
	 * �ú������ڴ�����ĸ�������Ϣ���������Ϣ��ֻ�иú�������true���ŻὫ��Ϣ�������ݸ��û����õļ�������
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
