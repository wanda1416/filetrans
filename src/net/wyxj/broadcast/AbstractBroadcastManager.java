package net.wyxj.broadcast;

import java.nio.charset.Charset;

public abstract class AbstractBroadcastManager implements IBroadcastManager {

	public static final Charset DEFAULT_ENCODE = Charset.forName("UTF-8");
	
	@Override
	public boolean sendBroadcastMessage(int port, String data) {
		return sendBroadcastMessage(port,
				data.getBytes(DEFAULT_ENCODE));
	}

	@Override
	public boolean sendBroadcastMessage(int port, Message msg) {
		return sendBroadcastMessage(port,msg.toString());
	}

}
