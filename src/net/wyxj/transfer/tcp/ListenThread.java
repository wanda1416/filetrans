package net.wyxj.transfer.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import net.wyxj.transfer.Configuration;
import net.wyxj.transfer.Constant;
import net.wyxj.transfer.FileHeader;
import net.wyxj.transfer.MyLogger;
import net.wyxj.transfer.def.IReceiveListener;
import net.wyxj.transfer.def.IServerListener;
import net.wyxj.transfer.def.ITransferListener;

public class ListenThread extends Thread {

	private Configuration config;

	private IReceiveListener listener;

	private HashSet<ReceiveThread> threadSet;

	private HashMap<Integer, ReceiveThread> connectMap;

	private boolean exit = false;

	private ServerSocket fileServerSocket = null;

	public ListenThread(Configuration config) {
		setConfig(config);
		setThreadSet(new HashSet<ReceiveThread>());
		setConnectMap(new HashMap<Integer, ReceiveThread>());
	}

	private IServerListener recall;

	@Override
	public void run() {
		// 启动服务器
		try {
			fileServerSocket = new ServerSocket(getConfig().getLocalPort(),
					getConfig().getMaxBacklog(),
					InetAddress.getByName(getConfig().getLocalAddress()));
			System.out.println(String.format("服务器(%s:%d)创建成功", getConfig()
					.getLocalAddress(), getConfig().getLocalPort()));
			fileServerSocket.setSoTimeout(Constant.DEFAULT_SOCKET_TIMEOUT);
		} catch (IOException e) {
			MyLogger.println("启动服务器失败，请检查端口是否被占用");
			e.printStackTrace(MyLogger.logger);
			if (getServerListener() != null) {
				getServerListener().startFailed();
			}
			return;
		}
		// 服务器启动成功
		if (getServerListener() != null) {
			getServerListener().startServer();
		}
		while (isExit() == false) {
			try {
				// 此处创建一个 Socket，但是Socket不在该线程下关闭。
				Socket socket = fileServerSocket.accept();
				String m_clientIP = socket.getInetAddress().getHostAddress();
				int m_clientPort = socket.getPort();
				MyLogger.println(String.format("服务器收到来自客户端(%s:%d)的连接请求",
						m_clientIP, m_clientPort));
				// 一个线程处理一个客户端的连接
				ReceiveThread transfer = new ReceiveThread(socket,
						getConfig());
				transfer.setListener(getTransferListener());
				// 启动接收线程
				transfer.start();
			} catch (SocketTimeoutException e) {
				continue;
			} catch (IOException e) {
				e.printStackTrace(MyLogger.logger);
				break;
			}
		}
		if (getServerListener() != null) {
			getServerListener().beforeCloseServer();
		}
		// 关闭流
		try {
			if (fileServerSocket != null)
				fileServerSocket.close();
			System.out.println("文件接收服务器关闭,即将退出");
		} catch (IOException e) {
			e.printStackTrace(MyLogger.logger);
		}
		if (getServerListener() != null) {
			getServerListener().afterCloseServer();
		}
		return;
	}

	public boolean isExit() {
		return exit;
	}

	public void exit() {
		if( this.isAlive()){
			this.interrupt();
		}
		for (ReceiveThread thread : getThreadSet()) {
			thread.exit();
		}
		this.exit = true;
	}

	public Configuration getConfig() {
		return config;
	}

	public void setConfig(Configuration config) {
		this.config = config;
	}

	public HashSet<ReceiveThread> getThreadSet() {
		return threadSet;
	}

	public void setThreadSet(HashSet<ReceiveThread> threadSet) {
		this.threadSet = threadSet;
	}

	public IReceiveListener getReceiveListener() {
		return listener;
	}

	public void setReceiveListener(IReceiveListener listener) {
		this.listener = listener;
	}

	public ITransferListener<ReceiveThread> getTransferListener() {
		return m_transferListener;
	}

	private ITransferListener<ReceiveThread> m_transferListener = new ITransferListener<ReceiveThread>() {
		
		/**
		 * 文件接收前的回调方法，当该方法返回 false 时，Socket将拒绝接收该文件。 <br/>
		 * 仅当返回 true 时， getConnectMap() 才会关联 fileID 与 transferThread。
		 */
		@Override
		public boolean beforeTransfer(ReceiveThread thread, FileHeader header) {
			boolean result = getReceiveListener().beforeReceive(header);
			if (result) {
				MyLogger.println("即将从 [" + thread.getSocket().toString()
						+ "] 接收文件：" + header.getFileName());
				getConnectMap().put(header.getFileID(), thread);
			} else {
				MyLogger.println("已经拒绝[" + thread.getSocket().toString()
						+ "] 接收文件：" + header.getFileName());
			}
			return result;
		}

		@Override
		public void afterTransfer(ReceiveThread thread, FileHeader header,
				boolean succeed) {
			MyLogger.println("从 [" + thread.getSocket().toString() + "] 接收文件："
					+ header.getFileName() + (succeed ? "成功" : "失败"));
			getConnectMap().remove(header.getFileID());
			getReceiveListener().afterReceive(header, succeed);
		}

		@Override
		public void onSchedule(ReceiveThread thread, FileHeader header,
				long receivedBytes, long totalBytes) {
			getReceiveListener().onSchedule(header, receivedBytes, totalBytes);
		}

		@Override
		public void start(ReceiveThread thread) {
			MyLogger.println("接收线程：" + thread.toString() + "启动");
			getThreadSet().add(thread);
			if (getServerListener() != null) {
				getServerListener().startConnect(thread.getSocket());
			}
		}

		@Override
		public void end(ReceiveThread thread) {
			MyLogger.println("接收线程：" + thread.toString() + "关闭");
			getThreadSet().remove(thread);
			if (getServerListener() != null) {
				getServerListener().closeConnect(thread.getSocket());
			}
			try {
				thread.getSocket().close();
			} catch (IOException e) {
				MyLogger.println("关闭Socket时发生异常:"
						+ thread.getSocket().toString());
				e.printStackTrace(MyLogger.logger);
			}
		}
	};

	public void abort(int fileID) {
		ReceiveThread thread = getConnectMap().get(fileID);
		if (thread != null) {
			MyLogger.println("文件(" + fileID + "):已经中断接收");
			thread.abort();
		} else {
			MyLogger.println("文件(" + fileID + "):任务未开始,或者已经结束");
		}
	}

	public void abortAll() {
		for (Entry<Integer, ReceiveThread> entry : getConnectMap().entrySet()) {
			ReceiveThread thread = entry.getValue();
			thread.abort();
		}
		MyLogger.println("已经中断了所有文件的接收");
	}

	public HashMap<Integer, ReceiveThread> getConnectMap() {
		return connectMap;
	}

	public void setConnectMap(HashMap<Integer, ReceiveThread> connectMap) {
		this.connectMap = connectMap;
	}

	public IServerListener getServerListener() {
		return recall;
	}

	public void setServerListener(IServerListener recall) {
		this.recall = recall;
	}

}
