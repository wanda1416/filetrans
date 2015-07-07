package net.wyxj.transfer.def;

import net.wyxj.transfer.FileHeader;

/**
 * 文件发送监听类接口，定义了一个监听类的接口 。<br/>
 * 该类用于当文件传输者在发送文件过程中通知用户。
 * @author 辉
 */
public interface ISendListener {
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

}
