package net.wyxj.transfer.def;

/**
 * 文件接收者接口，定义了一个文件接收者应当实现的方法。 <br/>
 * 一共定义了7个方法。包括：启动、关闭，中止文件接收，接收监听器管理和状态获取方法。
 * @author 辉
 *
 */
public interface IFileReceiveServer {
	
	/**
	 * 启动服务器
	 */
	public void start();

	/**
	 * 关闭服务器
	 */
	public void shutdown();
	
	/**
	 * 终止接收该文件
	 * @param fileID 文件id
	 */
	public void abort(int fileID);
	
	/**
	 * 终止接收全部文件
	 */
	public void abortAll();

	/**
	 * 获得文件接收监听器
	 * @return 返回文件接收监听器
	 */
	public IReceiveListener getReceiveListener();
	
	/**
	 * 设置文件接收监听器
	 * @param m_receiveListener	新的文件接收监听器
	 */
	public void setReceiveListener(IReceiveListener receiveListener);
	
	/**
	 * 获取服务器状态
	 */
	public int getStatus();
	
	public void setExitListener(IExitListener exitListener);

}
