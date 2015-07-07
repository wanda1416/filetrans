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
		// 状态检查
		if (getStatus() != Constant.STATUS_NOINIT) {
			throw new IllegalStateException("");
		}
		// 创建一个文件发送服务器，服务器的实现可以自定义。仅需实现接口 IFileSendServer。
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
		// 状态检查
		if (getStatus() != Constant.STATUS_INITED) {
			throw new IllegalStateException("");
		}
		// 创建一个文件接收服务器，服务器的实现可以自定义。仅需实现接口 IFileReceiveServer。
		setReceiveServer(TransferFactory.createFileReceiveServer(config));
		getReceiveServer().setReceiveListener(getReceiveListener());
		getReceiveServer().setExitListener(new IExitListener(){
			@Override
			public void exit() {
				// 释放一个资源，以便使得 waitForReceiveServerExit 不再阻塞。
				notifyForExit();
			}			
		});
		getReceiveServer().start();		
		super.start(config);
		return 0;
	}
	
	@Override
	public void shutdown(){
		// 状态检查
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
		// 状态检查
		if (getStatus() == Constant.STATUS_DESTROYED) {
			throw new IllegalStateException("");
		}
		if (getStatus() == Constant.STATUS_RUNNING){
			// 如果文件接收服务器还没有关闭，那么关闭服务器
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
