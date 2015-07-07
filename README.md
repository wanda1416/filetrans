
#  文件传输框架

--------------------------
### 说明

	目标：本框架用于完成点对点的文件传输，采用Java语言开发。

说明：
	
	目前采用的是 ServerSocket 和 Socket 完成通信。
	采用并发处理多用于连接。
	默认情况下，如果10s内无法读取数据，将自动关闭。
	如果连接中断，将在1s内关闭连接。
	
缺点：

	1. 线程没有复用
	2. 未采用NIO读写文件
	3. 传输未压缩
	

-------------------------
### 发送与接收示例（不采用监听器）

**发送文件：**
		
		// remote 指定了远端的Socket地址
		InetSocketAddress remote = new InetSocketAddress(
				SocketTools.getLANAddress(),Constant.DEFAULT_LOCAL_PORT);

		FileTransfer transfer = FileTransfer.newInstance();
		File file = new File("e:\\quick\\test.html");
		transfer.sendFile(remote, file);	// 非阻塞

		......	// 等待发送结束

		transfer.destroy();

发送文件调用 sendFile 方法，有两个重载。

	public void sendFile(InetSocketAddress remote, File file);
	
	public void sendFile(InetSocketAddress remote, File file ,ISendListener listener);

第二个方法将为该文件发送时指定一个单独的文件发送监听器。


**接收文件：**
		
		Configuration config = new Configuration();
		config.setReceivePath("./recv");
		
		FileTransfer transfer = FileTransfer.newInstance();
		// 对于接收文件，必须先启动。
		transfer.start(config);
		
		.....	// 等待接收完毕

		transfer.shutdown();
		transfer.destroy();



-------------------------
### 监听器说明

允许设置发送和接收监听器，监听器有三个方法。

ISendListener： beforeSend  、afterSend  和 onSchedule 。

IReceiveListener ： beforeReceive  、afterReceive  和 onSchedule 。

其中 beforeSend和 afterSend 一定是成对调用，用于指示文件发送/接收 的开始与结束。

但是 只有当 beforeReceive 返回 true，即允许接收文件时，后续才会调用 afterReceive。

onSchedule 用于接收文件的传输进度，每完成 Constant.INTERVAL_SIZE 字节调用一次。默认是 40KB。

**IReceiveListener**
	
	/**
	 * 在收到一个文件发送请求时，将调用该方法。如果返回false，将拒绝接收。
	 * @return
	 */
	public boolean beforeReceive(FileHeader header);
	/**
	 * 在一个文件接收完毕后，将调用该方法。
	 * @return
	 */
	public void afterReceive(FileHeader header,boolean succeed);
	/**
	 * 通知调用者当前接收的进度，以字节数表示。<br/>
	 * @param receivedBytes 已经接收的字节数	
	 * @param totolBytes 文件的总大小
	 */
	public void onSchedule(FileHeader header,long receivedBytes,long totalBytes);

**ISendListener**
	
	/**
	 * 在收到一个文件发送请求时，将调用该方法。
	 * @return
	 */
	public void beforeSend(FileHeader header);
	/**
	 * 在一个文件发送完毕后，将调用该方法。succeed表示发送成功与否。
	 * @return
	 */
	public void afterSend(FileHeader header,boolean succeed);
	/**
	 * 通知调用者当前发送的进度，以字节数表示。<br/>
	 * @param sentBytes 已经接收的字节数	
	 * @param totolBytes 文件的总大小
	 */
	public void onSchedule(FileHeader header,long sentBytes,long totalBytes);


------------------------
### 文件传输的中止

允许在文件传输的过程中随时中止文件发送和接收，中止需要通过 fileID 进行。

因此如果需要实现随时中止，那么必须创建监听器，以获得最新的 fileID信息。

文件id将分布在 0 - （2^31-1） 之间，采用随机生成。

**方法：**
	
	public void abortSend(int fileID);
	
	public void abortRecv(int fileID);


#### TransferManager

示例：
	
		transfer = FileTransfer.newInstance();
		TransferManager manager = new TransferManager(transfer);
		manager.setSendListener(sender);
		manager.setReceiveListener(listener);
		transfer.start(config);

也可以通过 TransferManager 来管理文件传输的状态信息。
TransferManager 提供方法。

得到正在发送或接收的文件信息列表:

	public ArrayList<FileHeader> getSendList()；

	public ArrayList<FileHeader> getRecvList()；

得到正在发送或接收的文件名列表:
	
	public ArrayList<String> getSendNames();

	public ArrayList<String> getRecvNames();

通过文件名得到 fileID：
	
	public int getSendIdFromName(String filename);

	public int getRecvIdFromName(String filename);

通过 ID 得到文件信息：
	
	public FileHeader getHeaderFromSID(int fileID);

	public FileHeader getHeaderFromRID(int fileID);

通过 fileID 得到传输的进度（完成的字节数）：
	
	public long getSendSchedule(int fileID);
	
	public long getRecvSchedule(int fileID);

通过 fileID 得到传输的进度（完成的比率）：
	
	public double getSendRate(int fileID);
	
	public double getRecvRate(int fileID);	
