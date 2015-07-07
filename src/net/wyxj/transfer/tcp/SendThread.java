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
		// ���������߳�
		getListener().start(this);
		FileHeader header = new FileHeader(getSendTask().getFile());
		if (!header.getCheckCode().equals("null")) {
			MyLogger.println("У���룺" + header.getCheckCode());
		}
		// �����ǰ����ָ���˶����ķ��ͼ���������ô֪ͨ�ü�����
		if (getSendTask().getListener() != null) {
			getSendTask().getListener().beforeSend(header);
		}
		// ֪ͨ�������߳����񼴽���ʼ
		getListener().beforeTransfer(this, header);
		boolean sendResult = false;
		// ����ļ���Ϣ�Ƿ���Ч
		if (header.getFileType() == FileHeader.TYPE_INVAILD
				|| header.getFileType() == FileHeader.TYPE_DIR) {
			// ����ļ���Ч����ΪĿ¼����ô�˳�����ͬʱ�����ú����ķ�����
			MyLogger.println("�������ļ�(" + header.getFileName() + ")��Ч���������");
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
			MyLogger.println("�޷�������Զ��(" + getSendTask().getRemote()
					+ ")�����ӣ��������");
			e.printStackTrace(MyLogger.logger);
			onExit(header, sendResult);
			return;
		}
		try {
			out.write(header.toString().getBytes(Constant.DEFAULT_ENCODE));
		} catch (IOException e) {
			MyLogger.println("����(" + header.getFileName() + ")����ʧ�ܣ��������");
			e.printStackTrace(MyLogger.logger);
			onExit(header, sendResult);
			return;
		}
		String res = readReply(in);
		if (res != null && res.equals("Allow\r\n")) {
			// ������ͬ������ļ�
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(getSendTask().getFile());
			} catch (FileNotFoundException e) {
				MyLogger.println("�޷���ȡ�ļ�(" + header.getFileName() + ")���������");
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
			// TODO �������ܾ����ո��ļ�

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
					// �˴�ʧ�ܣ���ζ���ļ���ȡ��ϻ���ʧ�ܣ����ֱ���˳�
					break;
				}
				out.write(buffer, 0, result);
				count += result;
				notify += result;
				remain -= result;
				// ����֪ͨ�ļ����ͽ���
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
					MyLogger.println("������ʧ�ܣ��������");
					return null;
				}
				break;
			} catch (SocketTimeoutException e) {
				times++;
				if (times >= Constant.DEFAULT_TRY_TIMES) {
					break;
				}
			} catch (IOException e) {
				MyLogger.println("������ʧ�ܣ��������");
				e.printStackTrace(MyLogger.logger);
				return null;
			}
		}
		String res = new String(data, 0, len ,Constant.DEFAULT_ENCODE);
		return res;
	}

}
