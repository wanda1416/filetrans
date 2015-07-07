package net.wyxj.transfer;

import net.wyxj.transfer.def.IFileReceiveServer;
import net.wyxj.transfer.def.IFileSendServer;
import net.wyxj.transfer.impl.FileTransferImpl;
import net.wyxj.transfer.tcp.TcpFileReceiveServer;
import net.wyxj.transfer.tcp.TcpFileSendServer;

public class TransferFactory {
	
	public static IFileReceiveServer createFileReceiveServer(Configuration config){
		IFileReceiveServer receiver = new TcpFileReceiveServer(config);
		return receiver;
	}
	
	public static IFileSendServer createFileSendServer(){
		IFileSendServer sender = new TcpFileSendServer();
		return sender;
	}
	
	public static FileTransfer createFileTransfer(){
		return new FileTransferImpl();
	}
}
