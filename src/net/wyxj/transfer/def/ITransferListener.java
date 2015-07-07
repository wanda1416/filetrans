package net.wyxj.transfer.def;

import net.wyxj.transfer.FileHeader;


public interface ITransferListener<T> {
	
	public boolean beforeTransfer(T thread, FileHeader header);

	public void afterTransfer(T thread, FileHeader header,
			boolean succeed);

	public void onSchedule(T thread, FileHeader header,
			long receivedBytes, long totalBytes);

	/** �߳̽���ʱ������ø÷��� */
	public void start(T thread);

	/** �߳��˳�ʱ������ø÷��� */
	public void end(T thread);
}
