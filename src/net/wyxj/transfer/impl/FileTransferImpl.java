package net.wyxj.transfer.impl;

import net.wyxj.transfer.Configuration;
import net.wyxj.transfer.Constant;
import net.wyxj.transfer.FileTransfer;
import net.wyxj.transfer.MyLogger;
import net.wyxj.transfer.TransferFactory;
import net.wyxj.transfer.def.IExitListener;

public class FileTransferImpl extends FileTransfer {
	
	public FileTransferImpl() {
		super();
		init();
	}
	
	@Override
	public void init(){
		// ״̬���
		if (getStatus() != Constant.STATUS_NOINIT) {
			throw new IllegalStateException("");
		}
		// ����һ���ļ����ͷ���������������ʵ�ֿ����Զ��塣����ʵ�ֽӿ� IFileSendServer��
		setSendServer(TransferFactory.createFileSendServer());
		getSendServer().setSendListener(getSendListener());
		getSendServer().start();
		super.init();
	}
	
	@Override
	public int start(Configuration config){
		if (getStatus() == Constant.STATUS_NOINIT){
			init();
		}
		// ״̬���
		if (getStatus() != Constant.STATUS_INITED) {
			throw new IllegalStateException("");
		}
		// ����һ���ļ����շ���������������ʵ�ֿ����Զ��塣����ʵ�ֽӿ� IFileReceiveServer��
		setReceiveServer(TransferFactory.createFileReceiveServer(config));
		getReceiveServer().setReceiveListener(getReceiveListener());
		getReceiveServer().setExitListener(new IExitListener(){
			@Override
			public void exit() {
				// �ͷ�һ����Դ���Ա�ʹ�� waitForReceiveServerExit ����������
				notifyForExit();
			}			
		});
		getReceiveServer().start();		
		super.start(config);
		return 0;
	}
	
	@Override
	public void shutdown(){
		// ״̬���
		if (getStatus() != Constant.STATUS_RUNNING) {
			throw new IllegalStateException("");
		}
		if (getReceiveServer() != null){
			getReceiveServer().shutdown();
		}
		super.shutdown();
	}
	
	@Override
	public void destroy(){
		// ״̬���
		if (getStatus() == Constant.STATUS_DESTROYED) {
			throw new IllegalStateException("");
		}
		if (getStatus() == Constant.STATUS_RUNNING){
			// ����ļ����շ�������û�йرգ���ô�رշ�����
			shutdown();
		}
		if (getSendServer()!=null){
			getSendServer().shutdown();
		}
		super.destroy();
	}
	
	@Override
	public void waitForExit(){
		try {
			super.notEnding.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace(MyLogger.logger);
		}
	}
	
	public void notifyForExit(){
		super.notEnding.release();
	}
}
