package net.wyxj.transfer.def;

import net.wyxj.transfer.Configuration;

/**
 * 文件接收服务器 <br/>
 * 该类将启动一个内部线程用于监听文件发送请求
 * 
 * @author 辉
 * 
 */
public abstract class AbstractFileReceiveServer implements IFileReceiveServer{

	private Configuration config;

	/**
	 * 该监听器监听文件事件，需要传入到具体的工作者线程中。在 TcpFileReceiveServer 实现中
	 * 该监听器传给 ListenThread 线程，该线程中使用 TransferThread.RecallListener 获得
	 * 创建的Socket的状态，并调用该监听器用于传给 ReceiveListener。 在TcpFileReceiveServer
	 * 中，如果找不到  ReceiveListener，那么将使用一个 默认的 ReceiveAdapter 代替。
	 * 因此在 ListenThread 中一定有 ReceiveListener，也一定有 TransferThread.RecallListener。
	 */
	private IReceiveListener receiveListener = null;

	public AbstractFileReceiveServer(Configuration config) {
		setConfig(config.clone());
	}

	@Override
	public final IReceiveListener getReceiveListener() {
		return receiveListener;
	}

	@Override
	public void setReceiveListener(IReceiveListener receiveListener) {
		this.receiveListener = receiveListener;
	}

	public final Configuration getConfig() {
		return config;
	}

	public final void setConfig(Configuration config) {
		this.config = config;
	}

}
