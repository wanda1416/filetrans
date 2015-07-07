# 设计文档 
--------------------------
包名：net.wyxj.transfer

用途：实现远程文件发送与接收

版本：v2.0@2015-04-05 02:45:20

--------------------------
### 未来扩展

**功能**

1. 增加断点续传功能。

2. 增加多线程传输功能。

3. 增加压缩传输功能。

4. 增加加密传输功能。

**效率**

1. 采用NIO完成读写。

2. 使用连接池管理线程。

-------------------------
### 类概述

#### net.wyxj.transfer
	Configuration:
		配置类，保存了初始化Transfer需要的所有参数。
	FileHeader:
		文件信息描述类。
	FileTransfer:
		文件传输抽象类，无法直接构造，需要通过 newInstance()方法得到一个实例后才可以进行文件传输。
	MyLogger:
		日志记录类
	TransferFactory:
		工厂类，用于创建实现了的 IFileTransfer、FileReceiveServer、IFileReceiveServer 实例
	TransferManager：
		文件管理类，用于管理文件传输状态。 创建时需要传入一个文件工作者，TransferManager将会将内部的监听器注册到文件工作者中。
 		也可以将自定义的监听器传给TransferManager，同样可以实现监听。
		该类的目的在于向用户提供一个可以快捷管理文件发送与接收的功能。
 		包括如下功能： 1. 获取当前正在发送或者接收的文件列表以及传输进度； 
		2. 通过文件ID、文件名终止特定文件的发送和接收；
 

#### net.wyxj.transfer.def
	AbstractFileReceiveServer:

	AbstractFileSendServer:

	Constant:
		静态常量类
	IFileReceiveServer:

	IFileSendServer:

	IFileTransfer:
		定义了一个文件传输类必须实现的方法。	
	IReceiveListener:
		文件接收监听器
	ISendListener:
		文件发送监听器
	IServerListener:
		
	ITransferListener:
		

#### net.wyxj.transfer.impl
	FileTransferImpl:
		文件传输类的具体功能实现类。
	ReceiveAdapter:

	SendAdapter:


#### net.wyxj.transfer.tcp
	ListenThread:

	ManagerThread:

	ReceiveThread:

	SendTask:

	SendThread:

	TcpFileReceiveServer:

	TcpFileSendServer:
	
		
-------------------------
### 应用参数配置

#### 参数描述


#### 参数初始化流程
	
	initialize()  ->  读取保存的参数文件，没有则设置为默认值。
	resetAddress()	->	读取当前的ip地址，并设置。优先选取局域网地址。
	
#### 参数保存方案：
保存参数依赖于两个静态方法：
	
	public static Map<String, String> readStoreInfo();
		
	public static void writeStoreInfo(Map<String, String> info);	

**未来：** 在 Configuration 类的具体实例中，将调用上述两个方法完成参数的保存和配置。
上述方法应当提出 Configuration 类。

-------------------------
### 文件传输工作者

定义文件传输工作者的接口为：**IFileTransfer**

#### IFileTransfer
IFileTransfer 实现的方法包括： init,start,restart,shutdown,destroy。<br/>
文件接收必须在调用 start 方法后,shutdown 方法前才能执行。 <br/>
文件发送不需要一定调用start，但是必须调用init（默认启动），并且在destroy之前。

接口如下：	 

	public void init();

	public int start(Configuration config);

	public void restart(Configuration config);

	public void shutdown();

	public void destroy();

	public int getStatus();
	
#### FileTransfer

FileTransfer 实现了 IFileTransfer 接口全部方法，但是除 restart 和 getStatus，其它方法需要子类继续完善。

此外， FileTransfer 定义了六个属性已经相应的 setter/getter 方法。
	
	private Configuration m_config ;
	private int m_status ;
	private IReceiveListener m_receiveListener ;
	private ISendListener m_sendListener ;
	private IFileReceiveServer m_receiveServer ;
	private IFileSendServer m_sendServer ;

其中， m_receiveServer 和 m_sendServer 代表文件发送者和文件接收者。是完成文件传输的实际工作类。创建采用 TransferFactory 工厂类，可选的有 ：
TcpFileReceiveServer 和 TcpFileSendServer 两个采用 TCP 协议完成的类。

	
#### FileTransferImpl
	
FileTransferImpl 完成了 IFileTransfer 中 init、start、shutdown、destory 和 FileTransfer 中 waitForReceiveServerExit	的重写。完整了FileTransfer 的流程。
由于需要依赖 FileTransfer 中定义的接口，实际上 FileTransferImpl 无法完成太多功能。

**未来：** waitForReceiveServerExit 采用轮训 IReceiveServer 状态完成，占用资源高，应该改成同步方式通知。


-------------------------
### 文件接收者

定义一个文件接收服务器接口为： **IFileReceiveServer**

#### IFileReceiveServer
接口如下：
	
	public void start();  // 启动服务器

	public void shutdown();  // 关闭服务器

	public void abort(int fileID);	// 终止接收该文件

	public void abortAll();		// 终止接收全部的文件

	public IReceiveListener getReceiveListener(); 
		
	public void setReceiveListener(IReceiveListener m_receiveListener);

#### FileReceiveServer
该类实现了 IFileReceiveServer ， FileTransferImpl 创建该类用于接收文件。

在服务器运行期间，一旦接收到文件发送请求。则调用：
	
	getReceiveListener().beforeReceive(header);

仅当上述方法返回 true 时才会接收该文件。
在接收的过程中，每隔一个预定义的时间或者大小，均将调用下列方法：

	getReceiveListener().onSchedule(header,receivedBytes,totalBytes);
	
当文件成功接收完毕后，将调用：
	
	getReceiveListener().afterReceive(header);

---------------------------
### 文件发送者

定义一个文件接收服务器接口为： **IFileSendServer**

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
	

--------------------------
### 文件传输协议（TCP实现：net.wyxj.transfer.tcp）

#### 文件描述
文件采用 FileHeader 描述

	private int fileID;
	private int fileType;
	private String fileName;
	private long fileLength;
	private int checkType;
	private String checkCode;	

该类可以通过下列方法完成与字符串的转换（json格式，依赖于gson库）：

	public String toString();
	public static FileHeader fromString(String json);	


#### 基本流程
文件传输的基本流程：

服务器端流程：

	1. 启动文件接收服务器（开一个线程），监听连接请求。
	2. 收到连接请求，新开线程处理该连接，继续监听请求。
	3. 在新线程中，接收数据，尝试解析为 FileHeader格式。
	4. 解析成功则调用 beforeReceive ，得到 true 确认后开始接收数据。
	5. 在接收过程中将时刻回调方法，并时刻准备终止文件接收。

客户端流程:

	1. 根据文件发送请求，创建新的线程用于发送文件
	2. 连接服务器，发送 FileHeader ，检查回复。
	3. 如果收到回复 "Allow\r\n"，那么发送文件内容。
	4. 如果收到回复 "Disallow\r\n",那么发送失败。
	5. 传输过程中收到 "Abort\r\n",此时中断发送。收到 "OK\r\n"代表发送成功。
	6. 文件发送完毕后，主动断开连接。

#### 连接复用设计方案：（v3.0考虑）

该方案将允许一个Socket连接多次传输文件。


-------------------------
### 消息传递机制

#### 文件接收监听器 ：    **IFileReceiveListener**
	
	public boolean beforeReceive(FileHeader header);
	
	public void afterReceive(FileHeader header,boolean succeed);
	
	public void onSchedule(FileHeader header,long receivedBytes,long totalBytes);

**说明**：

	1. 用户通过 FileTransfer.setReceiveListener 设置文件接收监听器。
		不设置最终会创建一个默认的监听器： new ReceiveAdapter()。

	2. FileTransfer 的具体实现为 FileTransferImpl 。
		FileTransferImpl 不覆盖文件接收监听器的方法。

	3. FileTransferImpl 将启动一个 IFileReceiveServer 。
		同样，这是一个接口，实际上将启动 TcpFileReceiveServer。
		FileTransferImpl 会将 IReceiveListener 监听器传给 TcpFileReceiveServer。

	4. TcpFileReceiveServer 会把监听器传给新开的线程 ListenThread。
		在该线程中，因为接收文件在另一个线程中。
		因此需要监听文件接收情况，并时刻终止接收。
		所以创建了一个监听器用以传给接收线程 TransferThread。
	
	5. 称上述监听器为 TransferThread.TransferListener 。
		ListenThread 在实现上述监听器时，会将情况最终通过 ReceiveListener
		通知调用者。

对于 IFileReceiveListener 和  TransferThread.TransferListener 应该认为是同一种监听器，目的均为处理文件接收的情况。

	
#### 文件发送监听器 :    **ISendListener**

	/**
	 * 在收到一个文件发送请求时，将调用该方法。如果返回false，将拒绝接收。
	 * @return
	 */
	public void beforeSend(FileHeader header);
	/**
	 * 在一个文件接收完毕后，将调用该方法。
	 * @return
	 */
	public void afterSend(FileHeader header,boolean succeed);
	/**
	 * 通知调用者当前发送的进度，以字节数表示。<br/>
	 * @param sentBytes 已经接收的字节数	
	 * @param totolBytes 文件的总大小
	 */
	public void onSchedule(FileHeader header,long sentBytes,long totalBytes);

**说明**
	
文件发送监听类接口，定义了一个监听类的接口 。<br/>
该类用于当文件传输者在发送文件过程中通知用户。


#### 传输状态监听器 ：    **ITransferListener<T\>**

	public boolean beforeTransfer(T thread, FileHeader header);

	public void afterTransfer(T thread, FileHeader header,
			boolean succeed);

	public void onSchedule(T thread, FileHeader header,
			long receivedBytes, long totalBytes);

	/** 线程进入时必须调用该方法 */
	public void start(T thread);

	/** 线程退出时必须调用该方法 */
	public void end(T thread);
	

#### 服务状态监听器 ：    **IServerListener**

 * 回调监听器，用于通知 发送者或者接收者 当前的状态。
 * 首先 启动服务器，成功调用： startServer() 。
 * 失败调用 ： startFailed() 然后退出。
 * 每当收到一个连接时将调用： startConnect()，连接关闭时调用： closeConnect()
 * 上述调用在新线程中调用，不在监听线程中。
 * 当 启动成功后，服务关闭前将调用 ：beforeCloseServer()
 * 关闭完成后调用： afterCloseServer()

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


---------------------------------
### 发送与接收的中止和暂停(v3.0考虑)

#### 文件中止传输方案


#### 文件断点续传方案


