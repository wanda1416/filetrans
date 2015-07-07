package net.wyxj.transfer.tcp;

import java.io.File;
import java.net.InetSocketAddress;

import net.wyxj.transfer.def.ISendListener;

public class SendTask {

	private InetSocketAddress remote;

	private File file;

	private ISendListener listener;

	/**
	 * 创建一个文件发送任务，其中监听器可以为null，但是必须保证远端和文件的有效性。
	 * 
	 * @param remote
	 *            远端地址
	 * @param file
	 *            待发送的文件
	 * @param listener
	 *            当前文件的发送监听器
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
