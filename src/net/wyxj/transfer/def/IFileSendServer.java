package net.wyxj.transfer.def;

import java.io.File;
import java.net.InetSocketAddress;

/**
 * 文件发送者接口，定义了一个文件发送者应当实现的方法。 <br/>
 * 
 * @author 辉
 *
 */
public interface IFileSendServer {
	
	/**
	 * 启动服务器
	 */
	public void start();

	/**
	 * 关闭服务器
	 */
	public void shutdown();
	
	/**
	 * 终止发送id为fileID的文件
	 * @param fileID 文件id
	 */
	public void abort(int fileID);
	
	/**
	 * 终止发送全部文件
	 */
	public void abortAll();

	/**
	 * 获得文件接收监听器
	 * @return 返回文件接收监听器
	 */
	public ISendListener getSendListener();
	
	/**
	 * 设置文件接收监听器
	 * @param m_receiveListener	新的文件接收监听器
	 */
	public void setSendListener(ISendListener sendListener);
	
	public boolean sendFile(InetSocketAddress remote,File file);
	
	public boolean sendFile(InetSocketAddress remote,File file,ISendListener listener);
	

}
