package net.wyxj.transfer.impl;

import net.wyxj.transfer.FileHeader;
import net.wyxj.transfer.def.IReceiveListener;

public class ReceiveAdapter implements IReceiveListener {

	@Override
	public boolean beforeReceive(FileHeader header) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void afterReceive(FileHeader header,boolean succeed) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSchedule(FileHeader header, long receivedBytes, long totalBytes) {
		// TODO Auto-generated method stub

	}

}
