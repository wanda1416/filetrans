package net.wyxj.transfer.tcp;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Socket;

import net.wyxj.transfer.def.AbstractFileSendServer;
import net.wyxj.transfer.def.ISendListener;
import net.wyxj.transfer.def.IServerListener;
import net.wyxj.transfer.impl.SendAdapter;

public class TcpFileSendServer extends AbstractFileSendServer {

	private TaskThread managerThread = null;

	public TcpFileSendServer() {
		super();
	}

	@Override
	public void start() {
		if (getManagerThread() != null) {
			return;
		}
		setManagerThread(new TaskThread());
		if (getSendListener() == null) {
			getManagerThread().setSendListener(new SendAdapter());
		} else {
			getManagerThread().setSendListener(getSendListener());
		}
		getManagerThread().setServerListener(getServerListener());
		getManagerThread().start();
	}

	@Override
	public void shutdown() {
		if (getManagerThread() == null) {
			return;
		}
		if (getManagerThread().isAlive()) {
			getManagerThread().interrupt();
		}
		getManagerThread().exit();
		setManagerThread(null);
	}

	@Override
	public void abort(int fileID) {
		if (getManagerThread() == null)
			return;
		getManagerThread().abort(fileID);
	}

	@Override
	public void abortAll() {
		if (getManagerThread() == null)
			return;
		getManagerThread().abortAll();
	}

	@Override
	public boolean sendFile(InetSocketAddress remote, File file) {
		return getManagerThread().addTask(new SendTask(remote, file, null));
	}

	@Override
	public boolean sendFile(InetSocketAddress remote, File file,
			ISendListener listener) {
		return getManagerThread().addTask(new SendTask(remote, file, listener));
	}

	public TaskThread getManagerThread() {
		return managerThread;
	}

	public void setManagerThread(TaskThread managerThread) {
		this.managerThread = managerThread;
	}

	@Override
	public void setSendListener(ISendListener sendListener) {
		super.setSendListener(sendListener);
		if (getManagerThread() != null) {
			getManagerThread().setSendListener(sendListener);
		}
	}

	private final IServerListener m_recall = new IServerListener() {

		@Override
		public void startServer() {
		}

		@Override
		public void startConnect(Socket socket) {
		}

		@Override
		public void closeConnect(Socket socket) {
		}

		@Override
		public void startFailed() {
		}

		@Override
		public void beforeCloseServer() {
		}

		@Override
		public void afterCloseServer() {
		}
	};

	public IServerListener getServerListener() {
		return m_recall;
	}

}
