package net.wyxj.transfer;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.concurrent.Semaphore;

import net.wyxj.transfer.def.IFileReceiveServer;
import net.wyxj.transfer.def.IFileSendServer;
import net.wyxj.transfer.def.IFileTransfer;
import net.wyxj.transfer.def.IReceiveListener;
import net.wyxj.transfer.def.ISendListener;

public abstract class FileTransfer implements IFileTransfer {

	private int m_status = Constant.STATUS_NOINIT;
	
	public Semaphore notEnding = new Semaphore(0);

	public static FileTransfer newInstance() {
		return TransferFactory.createFileTransfer();
	}

	public FileTransfer() {
		setStatus(Constant.STATUS_NOINIT);
	}

	@Override
	public void init() {
		setStatus(Constant.STATUS_INITED);
	}

	@Override
	public int start(Configuration config) {
		setStatus(Constant.STATUS_RUNNING);
		return 0;
	}

	@Override
	public final void restart(Configuration config) {
		if (getStatus() != Constant.STATUS_RUNNING) {
			throw new IllegalStateException("");
		}
		// 先发送关闭文件接收者指令
		shutdown();
		// TODO 等待接收关闭，下列方法属于暂时性方法，后期需要采取同步方式改进。
		waitForExit();
		start(config);
	}
	
	/**
	 * 抽象方法，继承类必须重写该方法以支持服务器重启。
	 */
	protected abstract void waitForExit();

	@Override
	public void shutdown() {
		setStatus(Constant.STATUS_INITED);
	}

	/**
	 * 销毁当前的实例
	 */
	@Override
	public void destroy() {
		setStatus(Constant.STATUS_DESTROYED);
	}

	public final int getStatus() {
		return m_status;
	}

	private final void setStatus(int m_status) {
		this.m_status = m_status;
	}

	/*
	 * 定义：文件发送监听器和文件接收监听器
	 */
	
	private IReceiveListener m_receiveListener = null;
	
	private ISendListener m_sendListener = null;
	
	public final IReceiveListener getReceiveListener() {
		return m_receiveListener;
	}

	public final void setReceiveListener(IReceiveListener m_receiveListener) {
		this.m_receiveListener = m_receiveListener;
		// 更新文件接收者中的监听方法
		if (getReceiveServer()!=null){
			getReceiveServer().setReceiveListener(this.m_receiveListener);
		}
	}

	public final ISendListener getSendListener() {
		return m_sendListener;
	}

	public final void setSendListener(ISendListener m_sendListener) {
		this.m_sendListener = m_sendListener;
		// 更新文件发送者中的监听方法
		if (getSendServer()!=null){
			getSendServer().setSendListener(this.m_sendListener);
		}
	}
	
	/*
	 * 下面开始定义文件接收者和文件发送者
	 */
	/** 文件接收服务器，将在 start 方法中启动 */
	private IFileReceiveServer m_receiveServer = null;
	/** 文件发送服务器，将在 init 方法中启动，start方法中会检查该方法是否调用  */
	private IFileSendServer m_sendServer = null;
	
	protected IFileSendServer getSendServer() {
		return m_sendServer;
	}

	protected void setSendServer(IFileSendServer m_sendServer) {
		this.m_sendServer = m_sendServer;
	}

	protected IFileReceiveServer getReceiveServer() {
		return m_receiveServer;
	}

	protected void setReceiveServer(IFileReceiveServer m_receiveServer) {
		this.m_receiveServer = m_receiveServer;
	}
	
	// 下列方法属于在发送者或者接收者中实现的
	/**
	 * 发送一个文件到远端，文件发送可以通过id随时中断，id将在监听器的回调方法中告知
	 * @param remote	远端地址
	 * @param file		文件
	 * @throws IllegalStateException	如果已经调用destroy方法，将抛出该异常
	 */
	public void sendFile(InetSocketAddress remote, File file)
			throws IllegalStateException {
		if (getStatus() <= Constant.STATUS_NOINIT
				&& getStatus() >= Constant.STATUS_DESTROYED) {
			throw new IllegalStateException("没有启动文件发送者");
		}
		getSendServer().sendFile(remote, file);
	}
	
	/**
	 * 发送一个文件到远端，文件发送可以通过id随时中断，id将在监听器的回调方法中告知
	 * @param remote
	 * @param file
	 * @param listener
	 * @throws IllegalStateException
	 */
	public void sendFile(InetSocketAddress remote, File file ,ISendListener listener)
			throws IllegalStateException {
		if (getStatus() <= Constant.STATUS_NOINIT
				&& getStatus() >= Constant.STATUS_DESTROYED) {
			throw new IllegalStateException("没有启动文件发送者");
		}
		getSendServer().sendFile(remote, file, listener);
	}
	
	public void abortSend(int fileID){
		if(getSendServer()!=null){
			getSendServer().abort(fileID);
		}
	}
	
	public void abortRecv(int fileID){
		if(getReceiveServer()!=null){
			getReceiveServer().abort(fileID);
		}
	}	
	
	public void abortAllSend(){
		if(getSendServer()!=null){
			getSendServer().abortAll();
		}
	}
	
	public void abortAll(){
		if(getReceiveServer()!=null){
			getReceiveServer().abortAll();
		}
	}	

}
