
### 广播消息设计文档
----------------

##### 客户端管理
------------------

	该模块负责管理所有在线的主机。

主机信息：
	
	address		ip地址
	port		监听端口（TCP端口）
	name		主机名
	type		消息类型

消息定义：
	
	MSG_ADD			主机上线
	MSG_NOTIFY		主机在线(通知新主机)
	MSG_ONLINE		主机在线(定时广播)
	MSG_DELETE		主机下线

当收到 MSG_ADD 消息后，所有客户端都应该立刻广播一次在线消息 MSG_NOTIFY。

自上线开始每隔 10 s，所有的主机都应该广播一次 MSG_ONLINE。


    