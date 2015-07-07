package net.wyxj.transfer.tcp;

import java.io.File;
import java.net.InetSocketAddress;

import net.wyxj.transfer.def.ISendListener;

public class SendTask {

	private InetSocketAddress remote;

	private File file;

	private ISendListener listener;

	/**
	 * ����һ���ļ������������м���������Ϊnull�����Ǳ��뱣֤Զ�˺��ļ�����Ч�ԡ�
	 * 
	 * @param remote
	 *            Զ�˵�ַ
	 * @param file
	 *            �����͵��ļ�
	 * @param listener
	 *            ��ǰ�ļ��ķ��ͼ�����
	 */
	public SendTask(InetSocketAddress remote, File file, ISendListener listener) {
		setRemote(remote);
		setFile(file);
		setListener(listener);
	}

	public InetSocketAddress getRemote() {
		return remote;
	}

	public void setRemote(InetSocketAddress remote) {
		this.remote = remote;
	}

	public ISendListener getListener() {
		return listener;
	}

	public void setListener(ISendListener listener) {
		this.listener = listener;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

}
