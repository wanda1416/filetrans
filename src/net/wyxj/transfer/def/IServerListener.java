package net.wyxj.transfer.def;

import java.net.Socket;

/**
 * �ص�������������֪ͨ �����߻��߽����� ��ǰ��״̬��
 * ���� �������������ɹ����ã� startServer() ��
 * ʧ�ܵ��� �� startFailed() Ȼ���˳���
 * ÿ���յ�һ������ʱ�����ã� startConnect()�����ӹر�ʱ���ã� closeConnect()
 * �������������߳��е��ã����ڼ����߳��С�
 * �� �����ɹ��󣬷���ر�ǰ������ ��beforeCloseServer()
 * �ر���ɺ���ã� afterCloseServer()
 * @author ��
 */
public interface IServerListener {
	
	/**	����������ʧ�� */
	public void startFailed();
	
	/** �����������������ø÷��� */
	public void startServer();

	/** �رշ�����ǰ������ø÷��� */
	public void beforeCloseServer();
	
	/**	�ر��˷�����֮�������ø÷���*/
	public void afterCloseServer();

	/** ��һ��Socket���Ӻ������ø÷��� */
	public void startConnect(Socket socket);

	/** �ر�һ��Socket����ǰ������ø÷��� */
	public void closeConnect(Socket socket);

}