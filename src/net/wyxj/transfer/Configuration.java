package net.wyxj.transfer;

import java.util.HashMap;
import java.util.Map;

import net.wyxj.tools.filetrans.ConfigTools;
import net.wyxj.tools.filetrans.SocketTools;

public class Configuration {
	// 本地的地址在创建后自动配置，不需要写入到配置文件中
	/** 本地IP地址 */
	private String localAddress;
	// 下列参数代表需要长期保持的参数，需要写入到配置文件中，并且在初始化时读取
	/** 本地服务器端口 */
	private int localPort;
	/** 接收文件的目录 */
	private String receivePath;
	/** 最大接受的客户端 */
	private int maxBacklog;
	/** 主机名 */
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
		// 读取配置文件
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
