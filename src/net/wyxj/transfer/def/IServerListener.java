package net.wyxj.transfer.def;

import java.net.Socket;

/**
 * 回调监听器，用于通知 发送者或者接收者 当前的状态。
 * 首先 启动服务器，成功调用： startServer() 。
 * 失败调用 ： startFailed() 然后退出。
 * 每当收到一个连接时将调用： startConnect()，连接关闭时调用： closeConnect()
 * 上述调用在新线程中调用，不在监听线程中。
 * 当 启动成功后，服务关闭前将调用 ：beforeCloseServer()
 * 关闭完成后调用： afterCloseServer()
 * @author 辉
 */
public interface IServerListener {
	
	/**	服务器启动失败 */
	public void startFailed();
	
	/** 启动服务器后必须调用该方法 */
	public void startServer();

	/** 关闭服务器前必须调用该方法 */
	public void beforeCloseServer();
	
	/**	关闭了服务器之后必须调用该方法*/
	public void afterCloseServer();

	/** 打开一个Socket连接后必须调用该方法 */
	public void startConnect(Socket socket);

	/** 关闭一个Socket连接前必须调用该方法 */
	public void closeConnect(Socket socket);

}