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
		// �ȷ��͹ر��ļ�������ָ��
		shutdown();
		// TODO �ȴ����չرգ����з���������ʱ�Է�����������Ҫ��ȡͬ����ʽ�Ľ���
		waitForExit();
		start(config);
	}
	
	/**
	 * ���󷽷����̳��������д�÷�����֧�ַ�����������
	 */
	protected abstract void waitForExit();

	@Override
	public void shutdown() {
		setStatus(Constant.STATUS_INITED);
	}

	/**
	 * ���ٵ�ǰ��ʵ��
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
	 * ���壺�ļ����ͼ��������ļ����ռ�����
	 */
	
	private IReceiveListener m_receiveListener = null;
	
	private ISendListener m_sendListener = null;
	
	public final IReceiveListener getReceiveListener() {
		return m_receiveListener;
	}

	public final void setReceiveListener(IReceiveListener m_receiveListener) {
		this.m_receiveListener = m_receiveListener;
		// �����ļ��������еļ�������
		if (getReceiveServer()!=null){
			getReceiveServer().setReceiveListener(this.m_receiveListener);
		}
	}

	public final ISendListener getSendListener() {
		return m_sendListener;
	}

	public final void setSendListener(ISendListener m_sendListener) {
		this.m_sendListener = m_sendListener;
		// �����ļ��������еļ�������
		if (getSendServer()!=null){
			getSendServer().setSendListener(this.m_sendListener);
		}
	}
	
	/*
	 * ���濪ʼ�����ļ������ߺ��ļ�������
	 */
	/** �ļ����շ����������� start ���������� */
	private IFileReceiveServer m_receiveServer = null;
	/** �ļ����ͷ����������� init ������������start�����л���÷����Ƿ����  */
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
	
	// ���з��������ڷ����߻��߽�������ʵ�ֵ�
	/**
	 * ����һ���ļ���Զ�ˣ��ļ����Ϳ���ͨ��id��ʱ�жϣ�id���ڼ������Ļص������и�֪
	 * @param remote	Զ�˵�ַ
	 * @param file		�ļ�
	 * @throws IllegalStateException	����Ѿ�����destroy���������׳����쳣
	 */
	public void sendFile(InetSocketAddress remote, File file)
			throws IllegalStateException {
		if (getStatus() <= Constant.STATUS_NOINIT
				&& getStatus() >= Constant.STATUS_DESTROYED) {
			throw new IllegalStateException("û�������ļ�������");
		}
		getSendServer().sendFile(remote, file);
	}
	
	/**
	 * ����һ���ļ���Զ�ˣ��ļ����Ϳ���ͨ��id��ʱ�жϣ�id���ڼ������Ļص������и�֪
	 * @param remote
	 * @param file
	 * @param listener
	 * @throws IllegalStateException
	 */
	public void sendFile(InetSocketAddress remote, File file ,ISendListener listener)
			throws IllegalStateException {
		if (getStatus() <= Constant.STATUS_NOINIT
				&& getStatus() >= Constant.STATUS_DESTROYED) {
			throw new IllegalStateException("û�������ļ�������");
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
