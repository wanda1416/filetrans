package net.wyxj.transfer.def;

import net.wyxj.transfer.FileHeader;

/**
 * �ļ����ͼ�����ӿڣ�������һ��������Ľӿ� ��<br/>
 * �������ڵ��ļ��������ڷ����ļ�������֪ͨ�û���
 * @author ��
 */
public interface ISendListener {
	/**
	 * ���յ�һ���ļ���������ʱ�������ø÷�����
	 * @return
	 */
	public void beforeSend(FileHeader header);
	/**
	 * ��һ���ļ�������Ϻ󣬽����ø÷�����succeed��ʾ���ͳɹ����
	 * @return
	 */
	public void afterSend(FileHeader header,boolean succeed);
	/**
	 * ֪ͨ�����ߵ�ǰ���͵Ľ��ȣ����ֽ�����ʾ��<br/>
	 * @param sentBytes �Ѿ����յ��ֽ���	
	 * @param totolBytes �ļ����ܴ�С
	 */
	public void onSchedule(FileHeader header,long sentBytes,long totalBytes);

}
