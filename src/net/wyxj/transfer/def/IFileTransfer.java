package net.wyxj.transfer.def;

import net.wyxj.transfer.Configuration;

/**
 * �ļ������߽ӿڣ�������һ���ļ�������Ӧ���߱��Ļ�������<br/>
 * �ýӿڶ��������෽����<br/>
 * һ������״̬���ƣ������������շ��������رս��շ�����������ʵ���ȷ�����<br/>
 * ��һ�����ڷ����ļ��������ļ����ͷ�����
 * @author ��
 * */
public interface IFileTransfer {

	/*
	 * ���̿�����: ���������� init,start,restart,shutdown,destroy�� �ļ����ձ����ڵ��� start
	 * ������,shutdown ����ǰ����ִ�С� �ļ����Ͳ���Ҫһ������start�����Ǳ������init��������destroy֮ǰ��
	 */
	
	public void init();

	public int start(Configuration config);

	public void restart(Configuration config);

	public void shutdown();

	public void destroy();

	public int getStatus();

}
