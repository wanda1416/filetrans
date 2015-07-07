package net.wyxj.transfer.def;

import java.io.File;
import java.net.InetSocketAddress;

/**
 * �ļ������߽ӿڣ�������һ���ļ�������Ӧ��ʵ�ֵķ����� <br/>
 * 
 * @author ��
 *
 */
public interface IFileSendServer {
	
	/**
	 * ����������
	 */
	public void start();

	/**
	 * �رշ�����
	 */
	public void shutdown();
	
	/**
	 * ��ֹ����idΪfileID���ļ�
	 * @param fileID �ļ�id
	 */
	public void abort(int fileID);
	
	/**
	 * ��ֹ����ȫ���ļ�
	 */
	public void abortAll();

	/**
	 * ����ļ����ռ�����
	 * @return �����ļ����ռ�����
	 */
	public ISendListener getSendListener();
	
	/**
	 * �����ļ����ռ�����
	 * @param m_receiveListener	�µ��ļ����ռ�����
	 */
	public void setSendListener(ISendListener sendListener);
	
	public boolean sendFile(InetSocketAddress remote,File file);
	
	public boolean sendFile(InetSocketAddress remote,File file,ISendListener listener);
	

}
