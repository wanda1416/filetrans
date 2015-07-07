package net.wyxj.broadcast;

/**
 * 主机在线情况监听器
 * 
 * @author 辉
 * 
 */
public interface HostListener {

	/**
	 * 主机上线消息，不可屏蔽
	 * 
	 * @param info
	 * @param reason
	 */
	public void onAdd(HostInfo info, Reason reason);

	/**
	 * 主机下线消息，不可屏蔽
	 * 
	 * @param info
	 * @param reason
	 */
	public void onDelete(HostInfo info, Reason reason);

	/**
	 * 
	 * @param info
	 * @param type
	 */
	public void onDefault(HostInfo info, Message message);

}
