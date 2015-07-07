package net.wyxj.transfer.def;

import net.wyxj.transfer.FileHeader;

/**
 * 文件接收监听类接口，定义了一个监听类的接口 。<br/>
 * 该类用于当文件传输者在接收文件过程中通知用户。
 * @author 辉
 */
public interface IReceiveListener {
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
	
}
