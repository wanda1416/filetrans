package net.wyxj.transfer.def;

import net.wyxj.transfer.FileHeader;

/**
 * �ļ����ռ�����ӿڣ�������һ��������Ľӿ� ��<br/>
 * �������ڵ��ļ��������ڽ����ļ�������֪ͨ�û���
 * @author ��
 */
public interface IReceiveListener {
	/**
	 * ���յ�һ���ļ���������ʱ�������ø÷������������false�����ܾ����ա�
	 * @return
	 */
	public boolean beforeReceive(FileHeader header);
	/**
	 * ��һ���ļ�������Ϻ󣬽����ø÷�����
	 * @return
	 */
	public void afterReceive(FileHeader header,boolean succeed);
	/**
	 * ֪ͨ�����ߵ�ǰ���յĽ��ȣ����ֽ�����ʾ��<br/>
	 * @param receivedBytes �Ѿ����յ��ֽ���	
	 * @param totolBytes �ļ����ܴ�С
	 */
	public void onSchedule(FileHeader header,long receivedBytes,long totalBytes);
	
}
