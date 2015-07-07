package net.wyxj.transfer.def;

public abstract class AbstractFileSendServer implements IFileSendServer {

	private ISendListener sendListener = null;
	
	public AbstractFileSendServer(){
		
	}
	
	@Override
	public final ISendListener getSendListener() {
		// TODO Auto-generated method stub
		return sendListener;
	}

	@Override
	public void setSendListener(ISendListener sendListener) {
		// TODO Auto-generated method stub
		this.sendListener = sendListener;
	}

}
