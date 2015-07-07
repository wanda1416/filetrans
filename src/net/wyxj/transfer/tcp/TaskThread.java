package net.wyxj.transfer.tcp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;

import net.wyxj.transfer.FileHeader;
import net.wyxj.transfer.MyLogger;
import net.wyxj.transfer.def.ISendListener;
import net.wyxj.transfer.def.IServerListener;
import net.wyxj.transfer.def.ITransferListener;

public class TaskThread extends Thread {

	public TaskThread() {
		taskQueue = new LinkedBlockingQueue<SendTask>();
		setThreadSet(new HashSet<SendThread>());
		setConnectMap(new HashMap<Integer, SendThread>());
	}

	@Override
	public void run() {
		if (getServerListener() != null) {
			getServerListener().startServer();
		}
		while (isExit() == false) {
			try {
				SendTask task = this.getTask();
				if (task != null) {
					SendThread sendThread = new SendThread(task);
					sendThread.setListener(getTransferListener());
					sendThread.start();
				}
			} catch (InterruptedException e) {
				e.printStackTrace(MyLogger.logger);
			}
		}
		if (getServerListener() != null) {
			getServerListener().beforeCloseServer();
			getServerListener().afterCloseServer();
		}
	}

	private boolean exit = false;

	public boolean isExit() {
		return this.exit;
	}

	public void exit() {
		if (this.isAlive()) {
			this.interrupt();
		}
		for (SendThread thread : getThreadSet()) {
			thread.abort();
		}
		this.exit = true;
	}

	private LinkedBlockingQueue<SendTask> taskQueue;

	public SendTask getTask() throws InterruptedException {
		return taskQueue.take();
	}

	public boolean addTask(SendTask task) {
		return taskQueue.offer(task);
	}

	private HashMap<Integer, SendThread> connectMap;

	public HashMap<Integer, SendThread> getConnectMap() {
		return connectMap;
	}

	public void setConnectMap(HashMap<Integer, SendThread> connectMap) {
		this.connectMap = connectMap;
	}

	private ISendListener listener;

	public ISendListener getSendListener() {
		return listener;
	}

	public void setSendListener(ISendListener listener) {
		this.listener = listener;
	}

	private HashSet<SendThread> threadSet;

	public HashSet<SendThread> getThreadSet() {
		return threadSet;
	}

	public void setThreadSet(HashSet<SendThread> threadSet) {
		this.threadSet = threadSet;
	}

	private IServerListener recall;

	public IServerListener getServerListener() {
		return recall;
	}

	public void setServerListener(IServerListener recall) {
		this.recall = recall;
	}

	public ITransferListener<SendThread> getTransferListener() {
		return m_transferListener;
	}

	public void setTransferListener(
			ITransferListener<SendThread> m_transferListener) {
		this.m_transferListener = m_transferListener;
	}

	public void abort(int fileID) {
		SendThread thread = getConnectMap().get(fileID);
		if (thread != null) {
			MyLogger.println("文件(" + fileID + "):已经中断发送");
			thread.abort();
		} else {
			MyLogger.println("文件(" + fileID + "):任务未开始,或者已经结束");
		}
	}

	public void abortAll() {
		for (Entry<Integer, SendThread> entry : getConnectMap().entrySet()) {
			SendThread thread = entry.getValue();
			thread.abort();
		}
		MyLogger.println("已经中断了所有文件的发送");
	}

	private ITransferListener<SendThread> m_transferListener = new ITransferListener<SendThread>() {

		@Override
		public boolean beforeTransfer(SendThread thread, FileHeader header) {
			getSendListener().beforeSend(header);
			getConnectMap().put(header.getFileID(), thread);
			if (getServerListener() != null) {
				getServerListener().startConnect(thread.getSocket());
			}
			return true;
		}

		@Override
		public void afterTransfer(SendThread thread, FileHeader header,
				boolean succeed) {
			getConnectMap().remove(header.getFileID());
			getSendListener().afterSend(header, succeed);
			if (getServerListener() != null) {
				getServerListener().closeConnect(thread.getSocket());
			}
		}

		@Override
		public void onSchedule(SendThread thread, FileHeader header,
				long receivedBytes, long totalBytes) {
			getSendListener().onSchedule(header, receivedBytes, totalBytes);
		}

		@Override
		public void start(SendThread thread) {
			MyLogger.println("发送线程：" + thread.toString() + "启动");
			getThreadSet().add(thread);
		}

		@Override
		public void end(SendThread thread) {
			MyLogger.println("发送线程：" + thread.toString() + "关闭");
			getThreadSet().remove(thread);
		}
	};

}
