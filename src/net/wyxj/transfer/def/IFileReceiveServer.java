package net.wyxj.transfer.def;

/**
 * �ļ������߽ӿڣ�������һ���ļ�������Ӧ��ʵ�ֵķ����� <br/>
 * һ��������7���������������������رգ���ֹ�ļ����գ����ռ����������״̬��ȡ������
 * @author ��
 *
 */
public interface IFileReceiveServer {
	
	/**
	 * ����������
	 */
	public void start();

	/**
	 * �رշ�����
	 */
	public void shutdown();
	
	/**
	 * ��ֹ���ո��ļ�
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
	public IReceiveListener getReceiveListener();
	
	/**
	 * �����ļ����ռ�����
	 * @param m_receiveListener	�µ��ļ����ռ�����
	 */
	public void setReceiveListener(IReceiveListener receiveListener);
	
	/**
	 * ��ȡ������״̬
	 */
	public int getStatus();
	
	public void setExitListener(IExitListener exitListener);

}
