package net.wyxj.transfer.def;

import net.wyxj.transfer.Configuration;

/**
 * 文件传输者接口，定义了一个文件传输类应当具备的基本方法<br/>
 * 该接口定义了两类方法。<br/>
 * 一类用于状态控制，包括启动接收服务器，关闭接收服务器和销毁实例等方法。<br/>
 * 另一类用于发送文件，包括文件发送方法。
 * @author 辉
 * */
public interface IFileTransfer {

	/*
	 * 流程控制类: 方法包括： init,start,restart,shutdown,destroy。 文件接收必须在调用 start
	 * 方法后,shutdown 方法前才能执行。 文件发送不需要一定调用start，但是必须调用init，并且在destroy之前。
	 */
	
	public void init();

	public int start(Configuration config);

	public void restart(Configuration config);

	public void shutdown();

	public void destroy();

	public int getStatus();

}
