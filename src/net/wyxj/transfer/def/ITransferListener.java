package net.wyxj.transfer.def;

import net.wyxj.transfer.FileHeader;


public interface ITransferListener<T> {
	
	public boolean beforeTransfer(T thread, FileHeader header);

	public void afterTransfer(T thread, FileHeader header,
			boolean succeed);

	public void onSchedule(T thread, FileHeader header,
			long receivedBytes, long totalBytes);

	/** 线程进入时必须调用该方法 */
	public void start(T thread);

	/** 线程退出时必须调用该方法 */
	public void end(T thread);
}
