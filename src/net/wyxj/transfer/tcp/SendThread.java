package net.wyxj.transfer.tcp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import net.wyxj.transfer.Constant;
import net.wyxj.transfer.FileHeader;
import net.wyxj.transfer.MyLogger;
import net.wyxj.transfer.def.ITransferListener;

public class SendThread extends Thread {

	public SendThread(SendTask task) {
		setSendTask(task);
	}

	private SendTask sendTask = null;

	public SendTask getSendTask() {
		return sendTask;
	}

	public void setSendTask(SendTask sendTask) {
		this.sendTask = sendTask;
	}

	private ITransferListener<SendThread> listener = null;

	public ITransferListener<SendThread> getListener() {
		return listener;
	}

	public void setListener(ITransferListener<SendThread> listener) {
		this.listener = listener;
	}

	private Socket socket = null;

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	private boolean exit = false;

	public boolean isExit() {
		return exit;
	}

	public void exit() {
		exit(true);
	}

	public void exit(boolean interrupt) {
		if (interrupt && this.isAlive()) {
			this.interrupt();
		}
		this.exit = true;
	}

	public void abort() {
		exit();
	}

	public void onExit(FileHeader header, boolean result) {
		if (getSendTask().getListener() != null) {
			getSendTask().getListener().afterSend(header, result);
		}
		getListener().afterTransfer(this, header, result);
		getListener().end(this);
	}

	@Override
	public void run() {
		// 启动发送线程
		getListener().start(this);
		FileHeader header = new FileHeader(getSendTask().getFile());
		if (!header.getCheckCode().equals("null")) {
			MyLogger.println("校验码：" + header.getCheckCode());
		}
		// 如果当前任务指定了独立的发送监听器，那么通知该监听器
		if (getSendTask().getListener() != null) {
			getSendTask().getListener().beforeSend(header);
		}
		// 通知发送主线程任务即将开始
		getListener().beforeTransfer(this, header);
		boolean sendResult = false;
		// 检查文件信息是否有效
		if (header.getFileType() == FileHeader.TYPE_INVAILD
				|| header.getFileType() == FileHeader.TYPE_DIR) {
			// 如果文件无效或者为目录，那么退出任务，同时将调用后续的方法。
			MyLogger.println("待发送文件(" + header.getFileName() + ")无效，任务结束");
			onExit(header, sendResult);
			return;
		}
		OutputStream out = null;
		InputStream in = null;
		try {
			setSocket(new Socket(getSendTask().getRemote().getAddress(),
					getSendTask().getRemote().getPort()));
			getSocket().setSoTimeout(Constant.DEFAULT_SOCKET_TIMEOUT);
			out = getSocket().getOutputStream();
			in = getSocket().getInputStream();
		} catch (IOException e) {
			MyLogger.println("无法建立与远端(" + getSendTask().getRemote()
					+ ")的连接，任务结束");
			e.printStackTrace(MyLogger.logger);
			onExit(header, sendResult);
			return;
		}
		try {
			out.write(header.toString().getBytes(Constant.DEFAULT_ENCODE));
		} catch (IOException e) {
			MyLogger.println("请求(" + header.getFileName() + ")发送失败，任务结束");
			e.printStackTrace(MyLogger.logger);
			onExit(header, sendResult);
			return;
		}
		String res = readReply(in);
		if (res != null && res.equals("Allow\r\n")) {
			// 服务器同意接收文件
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(getSendTask().getFile());
			} catch (FileNotFoundException e) {
				MyLogger.println("无法读取文件(" + header.getFileName() + ")，任务结束");
				e.printStackTrace(MyLogger.logger);
				exit();
			}
			
			sendResult = writeToOutputStream(fis,out,header);
			
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace(MyLogger.logger);
			}
		} else if (res != null && res.equals("Disallow\r\n")) {
			// TODO 服务器拒绝接收该文件

		}
		if (getSendTask().getListener() != null) {
			getSendTask().getListener().afterSend(header, sendResult);
		}
		getListener().afterTransfer(this, header, sendResult);
		try {
			out.close();
			in.close();
			getSocket().close();
		} catch (IOException e) {
			e.printStackTrace(MyLogger.logger);
		}
		getListener().end(this);
	}
	
	public boolean writeToOutputStream(InputStream fis,OutputStream out,
			FileHeader header){
		byte[] buffer = new byte[Constant.DEFAULT_BUFFER_SIZE];
		long count = 0;
		long notify = 0;
		long remain = header.getFileLength();
		while (isExit() == false) {
			try {
				int len = Math.min(Constant.DEFAULT_BUFFER_SIZE, (int) remain);
				int result = fis.read(buffer, 0, len);
				if (result <= 0) {
					// 此处失败，意味着文件读取完毕或者失败，因此直接退出
					break;
				}
				out.write(buffer, 0, result);
				count += result;
				notify += result;
				remain -= result;
				// 用于通知文件发送进度
				while (notify >= Constant.DEFAULT_INTERVAL_SIZE) {
					notify -= Constant.DEFAULT_INTERVAL_SIZE;
					if (getSendTask().getListener() != null) {
						getSendTask().getListener().onSchedule(header,
								count, header.getFileLength());
					}
					getListener().onSchedule(this, header, count,
							header.getFileLength());
				}
				if (count >= header.getFileLength()) {
					return true;
				}
			} catch (IOException e) {
				e.printStackTrace(MyLogger.logger);
				break;
			} // end catch
		} // end while
		return false;
	}

	public String readReply(InputStream in) {
		byte[] data = new byte[Constant.DEFAULT_BUFFER_SIZE];
		int len = 0;
		int times = 0;
		while (!isExit()) {
			try {
				len = in.read(data, 0, Constant.DEFAULT_BUFFER_SIZE);
				if (len <= 0) {
					MyLogger.println("读数据失败，任务结束");
					return null;
				}
				break;
			} catch (SocketTimeoutException e) {
				times++;
				if (times >= Constant.DEFAULT_TRY_TIMES) {
					break;
				}
			} catch (IOException e) {
				MyLogger.println("读数据失败，任务结束");
				e.printStackTrace(MyLogger.logger);
				return null;
			}
		}
		String res = new String(data, 0, len ,Constant.DEFAULT_ENCODE);
		return res;
	}

}
