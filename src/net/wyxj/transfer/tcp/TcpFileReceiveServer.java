package net.wyxj.transfer.tcp;

import java.net.Socket;

import net.wyxj.transfer.Configuration;
import net.wyxj.transfer.Constant;
import net.wyxj.transfer.def.IExitListener;
import net.wyxj.transfer.def.AbstractFileReceiveServer;
import net.wyxj.transfer.def.IReceiveListener;
import net.wyxj.transfer.def.IServerListener;
import net.wyxj.transfer.impl.ReceiveAdapter;

public class TcpFileReceiveServer extends AbstractFileReceiveServer {

	private ListenThread m_mainThread = null;
	
	private int status;

	public void setStatus(int status){
		synchronized(this){
			this.status = status;
		}
	}
	
	@Override
	public int getStatus(){
		synchronized(this){
			return status;
		}
	}
	
	/**
	 * TCPServer 将启动一个线程来创建 ServerSocket 监听。 <br/>
	 * 因此该监听器用于接收 ServerSocket的相关事件。 <br/>
	 * 包括 ServerSocket 的 启动失败、启动成功、收到连接、关闭连接、即将关闭、成功关闭 等事件
	 */
	private final IServerListener m_recall = new IServerListener() {

		@Override
		public void startServer() {
			setStatus(Constant.STATUS_RUNNING);
		}

		@Override
		public void beforeCloseServer() {		
			
		}

		@Override
		public void startConnect(Socket socket) {
			
		}

		@Override
		public void closeConnect(Socket socket) {
		}

		@Override
		public void startFailed() {	
			// 接收服务器启动失败
			setStatus(Constant.STATUS_DESTROYED);
		}

		@Override
		public void afterCloseServer() {
			setStatus(Constant.STATUS_DESTROYED);
			// 向 FileTransfer 的 信号量提供一个资源
			if (getExitListener() != null) {
				getExitListener().exit();
			}
		}

	};

	public TcpFileReceiveServer(Configuration config) {
		super(config);
		setStatus(Constant.STATUS_INITED);
	}

	@Override
	public void start() {
		if (m_mainThread != null) {
			return;
		}
		if (getReceiveListener() == null) {
			setReceiveListener(new ReceiveAdapter());
		}
		m_mainThread = new ListenThread(getConfig());
		m_mainThread.setReceiveListener(getReceiveListener());
		m_mainThread.setServerListener(getServerListener());
		m_mainThread.start();
	}

	@Override
	public void shutdown() {
		if (m_mainThread == null) {
			return;
		}
		if (m_mainThread.isAlive()) {
			m_mainThread.interrupt();
		}
		m_mainThread.exit();
		m_mainThread = null;
	}

	@Override
	public void abort(int fileID) {
		if (m_mainThread==null)
			return;
		m_mainThread.abort(fileID);
	}

	@Override
	public void abortAll() {
		if (m_mainThread==null)
			return;
		m_mainThread.abortAll();
	}

	@Override
	public void setReceiveListener(IReceiveListener receiveListener) {
		super.setReceiveListener(receiveListener);
		if (m_mainThread != null) {
			m_mainThread.setReceiveListener(getReceiveListener());
		}
	}

	public IServerListener getServerListener() {
		return m_recall;
	}

	private IExitListener exitListener = null;
	
	@Override
	public void setExitListener(IExitListener exitListener) {
		this.exitListener = exitListener;		
	}

	public IExitListener getExitListener() {
		return this.exitListener;		
	}
}
