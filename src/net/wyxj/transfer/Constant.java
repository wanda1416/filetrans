package net.wyxj.transfer;

import java.nio.charset.Charset;

public class Constant {
	
	/**
	 * 文件接收服务器启动默认的端口，除非在机器上需要启动两个服务器，否则不建议修改
	 */
	public static final int DEFAULT_BROADCAST_PORT = 31416;
	
	/**
	 * 文件接收服务器启动默认的端口，除非在机器上需要启动两个服务器，否则不建议修改
	 */
	public static final int DEFAULT_LOCAL_PORT = 31415;
	
	/**
	 * 默认的文件接收目录
	 */
	public static final String DEFAULT_RECEIVE_PATH = "./recv";
	
	/**
	 * 默认的最大连接数
	 */
	public static final int DEFAULT_BACKLOG = 10;
	
	/**
	 * 默认的配置文件名
	 */
	public static final String DEFAULT_CONFIGURATION_FILE = "set.ini";
	
	/**
	 * 默认的主机名
	 */
	public static final String DEFAULT_HOST_NAME = "localhost";
	
	/**
	 * 默认的Socket超时时间，单位 ms
	 */
	public static final int DEFAULT_SOCKET_TIMEOUT = 100;
	
	/**
	 * 默认重试的次数，包括 SocketTimeout 和 无法读取数据
	 */
	public static final int DEFAULT_TRY_TIMES = 100;
	
	/**
	 * 休眠时间间隔，比如无法读取和写入数据时休眠时间
	 */
	public static final int DEFAULT_SLEEP_TIME = 10;
	
	/**
	 * 默认的字符编码
	 */
	public static final Charset DEFAULT_ENCODE = Charset.forName("UTF-8");
	
	/**
	 * 默认的缓冲区大小
	 */
	public static final int DEFAULT_BUFFER_SIZE = 32*1024;
	
	/**
	 * 默认的文件进度通知间隔
	 */
	public static final int DEFAULT_INTERVAL_SIZE = 32*1024;

	/**
	 * 默认的文件校验方式
	 */
	public static final int DEFAULT_FILE_CHECK = FileHeader.CHECK_CRC32;
	
	/*
	 * 文件传输工作者的运行状态
	 */
	
	public static final int STATUS_NOINIT = 0;
	
	public static final int STATUS_INITED = 1;
	
	public static final int STATUS_RUNNING = 2;
	
	public static final int STATUS_DESTROYED = 3;
}
