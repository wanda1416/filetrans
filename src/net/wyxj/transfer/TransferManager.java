package net.wyxj.transfer;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import net.wyxj.transfer.def.IReceiveListener;
import net.wyxj.transfer.def.ISendListener;

/**
 * 文件管理类，用于管理文件传输状态。 创建时需要传入一个文件工作者，TransferManager将会将内部的监听器注册到文件工作者中。
 * 用于也可以将自定义的监听器传给 TransferManager ，同样可以实现监听。 该类的目的在于向用户提供一个可以快捷管理文件发送与接收的功能。
 * 包括如下功能： 1. 获取当前正在发送或者接收的文件列表以及传输进度； 2. 通过文件ID、文件名终止特定文件的发送和接收； 3.
 * 
 * @author 辉
 * 
 */
public class TransferManager {

	private ISendListener sendListener = null;

	private IReceiveListener receiveListener = null;

	public TransferManager(FileTransfer transfer) {
		transfer.setReceiveListener(myReceiver);
		transfer.setSendListener(mySender);
		sendTransSet = new HashSet<FileHeader>();
		recvTransSet = new HashSet<FileHeader>();
		sendSchedule = new HashMap<Integer,Long>();
		recvSchedule = new HashMap<Integer,Long>();
		sendID2Info = new HashMap<Integer,FileHeader>();
		recvID2Info = new HashMap<Integer,FileHeader>();
	}

	public IReceiveListener getReceiveListener() {
		return receiveListener;
	}

	public void setReceiveListener(IReceiveListener receiveListener) {
		this.receiveListener = receiveListener;
	}

	public ISendListener getSendListener() {
		return sendListener;
	}

	public void setSendListener(ISendListener sendListener) {
		this.sendListener = sendListener;
	}

	private HashSet<FileHeader> sendTransSet = null;

	public ArrayList<FileHeader> getSendList() {
		ArrayList<FileHeader> list = new ArrayList<FileHeader>(
				sendTransSet.size());
		synchronized (sendTransSet) {
			for (FileHeader header : sendTransSet) {
				list.add(header);
			}
		}
		return list;
	}
	
	public ArrayList<String> getSendNames() {
		ArrayList<String> list = new ArrayList<String>(
				sendTransSet.size());
		synchronized (sendTransSet) {
			for (FileHeader header : sendTransSet) {
				list.add(header.getFileName());
			}
		}
		return list;
	}

	private HashSet<FileHeader> recvTransSet = null;

	public ArrayList<FileHeader> getRecvList() {
		ArrayList<FileHeader> list = new ArrayList<FileHeader>(
				sendTransSet.size());
		synchronized (recvTransSet) {
			for (FileHeader header : sendTransSet) {
				list.add(header);
			}
		}
		return list;
	}
	
	public ArrayList<String> getRecvNames() {
		ArrayList<String> list = new ArrayList<String>(
				sendTransSet.size());
		synchronized (recvTransSet) {
			for (FileHeader header : sendTransSet) {
				list.add(header.getFileName());
			}
		}
		return list;
	}
	
	public int getSendIdFromName(String filename){
		synchronized (sendTransSet) {
			for (FileHeader header : sendTransSet) {
				if (header.getFileName() == filename){
					return header.getFileID();
				}
			}
		}		
		return -1;
	}
	
	public int getRecvIdFromName(String filename){
		synchronized (recvTransSet) {
			for (FileHeader header : recvTransSet) {
				if (header.getFileName() == filename){
					return header.getFileID();
				}
			}
		}		
		return -1;
	}

	private HashMap<Integer,Long> sendSchedule;
	
	private HashMap<Integer,Long> recvSchedule;
	
	public long getSendSchedule(int fileID){
		return sendSchedule.get(fileID);
	}
	
	public long getRecvSchedule(int fileID){
		return recvSchedule.get(fileID);
	}	
	
	public double getSendRate(int fileID){
		double sent = sendSchedule.get(fileID);
		double all = getHeaderFromSID(fileID).getFileLength();
		return sent/all;
	}
	
	public double getRecvRate(int fileID){
		double sent = recvSchedule.get(fileID);
		double all = getHeaderFromRID(fileID).getFileLength();
		return sent/all;
	}	

	private HashMap<Integer,FileHeader> sendID2Info = null;
	
	private HashMap<Integer,FileHeader> recvID2Info = null;
	
	public FileHeader getHeaderFromSID(int fileID){
		return sendID2Info.get(fileID);
	}
	
	public FileHeader getHeaderFromRID(int fileID){
		return recvID2Info.get(fileID);
	}
	
	private ISendListener mySender = new ISendListener() {

		@Override
		public void beforeSend(FileHeader header) {
			synchronized (sendTransSet) {
				sendTransSet.add(header);
			}
			sendSchedule.put(header.getFileID(), 0L);
			sendID2Info.put(header.getFileID(), header);
			if (getSendListener() != null) {
				getSendListener().beforeSend(header);
			}
		}

		@Override
		public void afterSend(FileHeader header, boolean succeed) {
			synchronized (sendTransSet) {
				sendTransSet.remove(header);
			}
			sendSchedule.remove(header.getFileID());
			sendID2Info.remove(header.getFileID());			
			if (getSendListener() != null) {
				getSendListener().afterSend(header, succeed);
			}
		}

		@Override
		public void onSchedule(FileHeader header, long sentBytes,
				long totalBytes) {
			sendSchedule.put(header.getFileID(), sentBytes);
			if (getSendListener() != null) {
				getSendListener().onSchedule(header, sentBytes, totalBytes);
			}
		}

	};

	private IReceiveListener myReceiver = new IReceiveListener() {

		@Override
		public boolean beforeReceive(FileHeader header) {
			synchronized (recvTransSet) {
				recvTransSet.add(header);
			}
			if (getReceiveListener() != null) {
				boolean result = getReceiveListener().beforeReceive(header);
				if (result == false)
					return false;
			}
			recvSchedule.put(header.getFileID(), 0L);
			recvID2Info.put(header.getFileID(), header);
			return true;
		}

		@Override
		public void afterReceive(FileHeader header, boolean succeed) {
			synchronized (recvTransSet) {
				recvTransSet.remove(header);
			}
			recvSchedule.remove(header.getFileID());
			recvID2Info.remove(header.getFileID());
			if (getReceiveListener() != null) {
				getReceiveListener().afterReceive(header, succeed);
			}
		}

		@Override
		public void onSchedule(FileHeader header, long receivedBytes,
				long totalBytes) {
			recvSchedule.put(header.getFileID(), receivedBytes);
			if (getReceiveListener() != null) {
				getReceiveListener().onSchedule(header, receivedBytes,
						totalBytes);
			}
		}

	};

}
