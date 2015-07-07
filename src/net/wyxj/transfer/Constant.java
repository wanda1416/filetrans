package net.wyxj.transfer;

import java.nio.charset.Charset;

public class Constant {
	
	/**
	 * �ļ����շ���������Ĭ�ϵĶ˿ڣ������ڻ�������Ҫ�������������������򲻽����޸�
	 */
	public static final int DEFAULT_BROADCAST_PORT = 31416;
	
	/**
	 * �ļ����շ���������Ĭ�ϵĶ˿ڣ������ڻ�������Ҫ�������������������򲻽����޸�
	 */
	public static final int DEFAULT_LOCAL_PORT = 31415;
	
	/**
	 * Ĭ�ϵ��ļ�����Ŀ¼
	 */
	public static final String DEFAULT_RECEIVE_PATH = "./recv";
	
	/**
	 * Ĭ�ϵ����������
	 */
	public static final int DEFAULT_BACKLOG = 10;
	
	/**
	 * Ĭ�ϵ������ļ���
	 */
	public static final String DEFAULT_CONFIGURATION_FILE = "set.ini";
	
	/**
	 * Ĭ�ϵ�������
	 */
	public static final String DEFAULT_HOST_NAME = "localhost";
	
	/**
	 * Ĭ�ϵ�Socket��ʱʱ�䣬��λ ms
	 */
	public static final int DEFAULT_SOCKET_TIMEOUT = 100;
	
	/**
	 * Ĭ�����ԵĴ��������� SocketTimeout �� �޷���ȡ����
	 */
	public static final int DEFAULT_TRY_TIMES = 100;
	
	/**
	 * ����ʱ�����������޷���ȡ��д������ʱ����ʱ��
	 */
	public static final int DEFAULT_SLEEP_TIME = 10;
	
	/**
	 * Ĭ�ϵ��ַ�����
	 */
	public static final Charset DEFAULT_ENCODE = Charset.forName("UTF-8");
	
	/**
	 * Ĭ�ϵĻ�������С
	 */
	public static final int DEFAULT_BUFFER_SIZE = 32*1024;
	
	/**
	 * Ĭ�ϵ��ļ�����֪ͨ���
	 */
	public static final int DEFAULT_INTERVAL_SIZE = 32*1024;

	/**
	 * Ĭ�ϵ��ļ�У�鷽ʽ
	 */
	public static final int DEFAULT_FILE_CHECK = FileHeader.CHECK_CRC32;
	
	/*
	 * �ļ����乤���ߵ�����״̬
	 */
	
	public static final int STATUS_NOINIT = 0;
	
	public static final int STATUS_INITED = 1;
	
	public static final int STATUS_RUNNING = 2;
	
	public static final int STATUS_DESTROYED = 3;
}
