package net.wyxj.broadcast;

public enum Reason {
	/** 主机发出登录请求  */
	LOGIN,
	/** 主机在线心跳消息 */
	ONLINE ,
	/** 收到新主机的定时广播 */
	EXIT ,
	/** 主机超时，被判定下线 */
	TIMEOUT ,
	/** 主机发出下线消息 */
	ALREADY
}
