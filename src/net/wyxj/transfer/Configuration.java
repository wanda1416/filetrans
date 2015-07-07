package net.wyxj.transfer;

import java.util.HashMap;
import java.util.Map;

import net.wyxj.tools.filetrans.ConfigTools;
import net.wyxj.tools.filetrans.SocketTools;

public class Configuration {
	// ���صĵ�ַ�ڴ������Զ����ã�����Ҫд�뵽�����ļ���
	/** ����IP��ַ */
	private String localAddress;
	// ���в���������Ҫ���ڱ��ֵĲ�������Ҫд�뵽�����ļ��У������ڳ�ʼ��ʱ��ȡ
	/** ���ط������˿� */
	private int localPort;
	/** �����ļ���Ŀ¼ */
	private String receivePath;
	/** �����ܵĿͻ��� */
	private int maxBacklog;
	/** ������ */
	private String hostName;
	
	public static final String PORT = "DefaultLocalPort";
	public static final String PATH = "DefaultReceivePath";
	public static final String BACKLOG = "DefaultMaxBacklog";
	public static final String HOSTNAME = "DefaultHostName";
	
	public Configuration() {
		initialize();
		resetAddress();
	}

	public void initialize() {
		// ��ȡ�����ļ�
		Map<String, String> set = ConfigTools.readStoreInfo();
		if(set == null){
			set = new HashMap<String, String>();
		}
		setReceivePath(set.get(PATH));
		setHostName(set.get(HOSTNAME));
		if (receivePath == null) {
			setReceivePath(Constant.DEFAULT_RECEIVE_PATH);
		}
		try {
			setLocalPort(Integer.valueOf(set.get(PORT)));
		} catch (NumberFormatException e) {
			setLocalPort(Constant.DEFAULT_LOCAL_PORT);
		}
		try {
			setMaxBacklog(Integer.valueOf(set.get(BACKLOG)));
		} catch (NumberFormatException e) {
			setMaxBacklog(Constant.DEFAULT_BACKLOG);
		}
	}

	public void saveConfig() {
		Map<String, String> info = new HashMap<String, String>();
		info.put(PATH, getReceivePath());
		info.put(PORT, String.valueOf(getLocalPort()));
		info.put(BACKLOG, String.valueOf(getMaxBacklog()));
		info.put(HOSTNAME, getHostName());
		ConfigTools.writeStoreInfo(info);
	}

	public void resetAddress() {
		setLocalAddress(SocketTools.getLANAddress());
	}

	// bean function area

	public String getLocalAddress() {
		return localAddress;
	}

	public void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	public String getReceivePath() {
		return receivePath;
	}

	public void setReceivePath(String receivePath) {
		this.receivePath = receivePath;
	}

	public int getMaxBacklog() {
		return maxBacklog;
	}

	public void setMaxBacklog(int maxBacklog) {
		this.maxBacklog = maxBacklog;
	}
	
	@Override
	public Configuration clone(){
		Configuration config = new Configuration();
		config.setLocalAddress(this.getLocalAddress());
		config.setLocalPort(this.getLocalPort());
		config.setMaxBacklog(this.getMaxBacklog());
		config.setReceivePath(this.getReceivePath());
		config.setHostName(this.getHostName());
		return config;		
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

}
