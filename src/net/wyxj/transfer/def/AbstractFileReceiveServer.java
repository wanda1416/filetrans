package net.wyxj.transfer.def;

import net.wyxj.transfer.Configuration;

/**
 * �ļ����շ����� <br/>
 * ���ཫ����һ���ڲ��߳����ڼ����ļ���������
 * 
 * @author ��
 * 
 */
public abstract class AbstractFileReceiveServer implements IFileReceiveServer{

	private Configuration config;

	/**
	 * �ü����������ļ��¼�����Ҫ���뵽����Ĺ������߳��С��� TcpFileReceiveServer ʵ����
	 * �ü��������� ListenThread �̣߳����߳���ʹ�� TransferThread.RecallListener ���
	 * ������Socket��״̬�������øü��������ڴ��� ReceiveListener�� ��TcpFileReceiveServer
	 * �У�����Ҳ���  ReceiveListener����ô��ʹ��һ�� Ĭ�ϵ� ReceiveAdapter ���档
	 * ����� ListenThread ��һ���� ReceiveListener��Ҳһ���� TransferThread.RecallListener��
	 */
	private IReceiveListener receiveListener = null;

	public AbstractFileReceiveServer(Configuration config) {
		setConfig(config.clone());
	}

	@Override
	public final IReceiveListener getReceiveListener() {
		return receiveListener;
	}

	@Override
	public void setReceiveListener(IReceiveListener receiveListener) {
		this.receiveListener = receiveListener;
	}

	public final Configuration getConfig() {
		return config;
	}

	public final void setConfig(Configuration config) {
		this.config = config;
	}

}
