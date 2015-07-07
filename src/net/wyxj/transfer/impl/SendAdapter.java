package net.wyxj.transfer.impl;

import net.wyxj.transfer.FileHeader;
import net.wyxj.transfer.def.ISendListener;

public class SendAdapter implements ISendListener {

	@Override
	public void beforeSend(FileHeader header) {
		// TODO Auto-generated method stub
	}

	@Override
	public void afterSend(FileHeader header,boolean succeed) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSchedule(FileHeader header, long sentBytes, long totalBytes) {
		// TODO Auto-generated method stub

	}

}
