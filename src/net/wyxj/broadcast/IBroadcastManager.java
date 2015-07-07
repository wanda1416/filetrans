package net.wyxj.broadcast;

import java.net.DatagramPacket;

import net.wyxj.broadcast.BroadcastManager.ReceiveThread;

/**
 * ������һ���㲥��Ϣ����Ҫʵ�ֵĹ���.
 * ���ܰ�����
 * 1. ���͹㲥��Ϣ���ض��˿�
 * 2. �����ض���UDP�˿ڣ�������Ϣ
 * 3. ����һ��������������������Ϣ 
 * 
 * @author ��
 *
 */
public interface IBroadcastManager {

	/**
	 * �����㲥��Ϣ�ļ�����
	 * @author ��
	 *
	 */
	
	public interface BoradcastListener {

		public boolean receiveData(ReceiveThread receiveThread, final DatagramPacket packet);
		
	}
	
	public void addListener(BoradcastListener listener);
	
	public void removeListener(BoradcastListener listener);
	
	public void removeAllListener();
	
	public void destroy();
	
	// ������Ϣ

	public boolean sendBroadcastMessage(int port, byte[] data);
	
	public boolean sendBroadcastMessage(int port, String data);
	
	// ������Ϣ
	
	public boolean listenPort(int port);
	
	public boolean removePort(int port);
	
	public void removeAllPort();
	
	
}
