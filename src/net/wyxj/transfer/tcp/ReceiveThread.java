package net.wyxj.transfer.tcp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import net.wyxj.security.symmetric.CRC32;
import net.wyxj.security.symmetric.MD5;
import net.wyxj.security.symmetric.SHA1;
import net.wyxj.transfer.Configuration;
import net.wyxj.transfer.Constant;
import net.wyxj.transfer.FileHeader;
import net.wyxj.transfer.MyLogger;
import net.wyxj.transfer.def.ITransferListener;

public class ReceiveThread extends Thread {

	private Socket socket;

	private Configuration config;

	private ITransferListener<ReceiveThread> listener;

	public ReceiveThread(Socket socket, Configuration config) {
		setSocket(socket);
		setConfig(config);
	}

	private boolean exit = false;

	private boolean abort = false;

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

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public ITransferListener<ReceiveThread> getListener() {
		return listener;
	}

	public void setListener(ITransferListener<ReceiveThread> listener) {
		this.listener = listener;
	}

	public String getReceivePath() {
		return getConfig().getReceivePath();
	}

	public void abort() {
		setAbort(true);
	}

	public boolean isAbort() {
		return abort;
	}

	public void setAbort(boolean abort) {
		this.abort = abort;
	}

	public Configuration getConfig() {
		return config;
	}

	public void setConfig(Configuration config) {
		this.config = config;
	}

	@Override
	public void run() {
		getListener().start(this);
		OutputStream out = null;
		InputStream in = null;
		// �򿪵����Ͷ˵�IO��
		try {
			getSocket().setSoTimeout(Constant.DEFAULT_SOCKET_TIMEOUT);
			in = getSocket().getInputStream();
			out = getSocket().getOutputStream();
		} catch (IOException e) {
			e.printStackTrace(MyLogger.logger);
			getListener().end(this);
			return;
		}
		// ������ͻ��˵Ľ�������
		int times = 0;
		byte[] data = new byte[Constant.DEFAULT_BUFFER_SIZE];
		while (isExit() == false) {
			try {
				int len = in.read(data, 0, Constant.DEFAULT_BUFFER_SIZE);
				if (len <= 0) {
					times++;
					try {
						sleep(Constant.DEFAULT_SLEEP_TIME);
					} catch (InterruptedException e) {
						break;
					}
					if (times >= Constant.DEFAULT_TRY_TIMES) {
						break;
					}
					continue;
				}
				times = 0;
				String json = new String(data, 0, len, Constant.DEFAULT_ENCODE);
				FileHeader header = FileHeader.fromString(json);
				if (header == null) {
					// ��ȡ��������Ч��������
					continue;
				}
				// ѯ���Ƿ���ո��ļ��������Ҫ�����ļ����������޸�header����
				// beforeReceive �� afterReceive ����ͬʱ�Ⱥ���ã����� beforeReceive==false
				if (getListener().beforeTransfer(this, header)) {
					boolean result = false;
					if (header.getFileType() == FileHeader.TYPE_FILE) {
						// ������ in �ж�ȡһ���ļ�
						out.write("Allow\r\n".getBytes());
						result = receiveFile(header, in);
						if (result) {
							out.write("OK\r\n".getBytes());
							// TODO �ɹ����յ�һ���ļ���Ĵ�������
							// ��ע�ⲻ�ܴ����� ListenThread�Ķ���.

							exit(false);
						} else {
							out.write("Abort\r\n".getBytes());
						}
					} else {
						// TODO:����Ŀ¼�����,Ŀǰ�Ĳ����ǲ�����Ŀ¼

						out.write("Disallow\r\n".getBytes());
					}
					getListener().afterTransfer(this, header, result);
				} else {
					// TODO: �ܾ����ո��ļ�

					out.write("Disallow\r\n".getBytes());
					continue;
				}
			} catch (SocketTimeoutException e) {
				times++;
				// ���� DEFAULT_TRY_TIMES ���ղ����κ����ݣ�����Ϊ����ʧ��
				if (times >= Constant.DEFAULT_TRY_TIMES) {
					break;
				}
			} catch (IOException e) {
				break;
			}
		} // end while
			// �����˳�
		try {
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace(MyLogger.logger);
		}
		getListener().end(this);
	}

	/**
	 * �����ļ�
	 * 
	 * @param header
	 *            �ļ���Ϣ
	 * @param in
	 *            ������
	 * @return �ļ��Ƿ�ɹ�д��
	 */
	private boolean receiveFile(FileHeader header, InputStream in) {
		File fileRecv = new File(getReceivePath() + "/" + header.getFileName());
		if (!checkFile(fileRecv)) {
			// �����Ϊ fileRecv �޷�д�룬��ô�����ء�
			return false;
		}

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fileRecv);
		} catch (FileNotFoundException e) {
			MyLogger.println("�޷����ļ���" + fileRecv.toString() + "���ļ�����ʧ��");
			e.printStackTrace(MyLogger.logger);
			return false;
		}

		long count = readFromInputStream(fos, in, header);

		try {
			fos.close();
		} catch (IOException e) {
			e.printStackTrace(MyLogger.logger);
			return false;
		}
		// ���� checkFile ����ļ��Ƿ���ճɹ�
		return checkFile(header, fileRecv, count);
	}

	public long readFromInputStream(OutputStream fos, InputStream in,
			FileHeader header) {
		int times = 0;
		long count = 0;
		long notify = 0;
		long remain = header.getFileLength();
		byte[] buffer = new byte[Constant.DEFAULT_BUFFER_SIZE];
		// ������Ϣѭ��ģ��
		while (isExit() == false) {
			if (isAbort() == true) {
				// �յ��ļ��жϽ��ձ�־,������˳�
				setAbort(false);
				break;
			}
			try {
				int len = Math.min(Constant.DEFAULT_BUFFER_SIZE, (int) remain);
				int result = in.read(buffer, 0, len);
				if (result <= 0) {
					times++;
					try {
						sleep(Constant.DEFAULT_SLEEP_TIME);
					} catch (InterruptedException e) {
						break;
					}
					if (times >= Constant.DEFAULT_TRY_TIMES) {
						break;
					}
					continue;
				}
				times = 0;
				fos.write(buffer, 0, result);
				count += result;
				notify += result;
				remain -= result;
				// ����֪ͨ�ļ��������
				while (notify >= Constant.DEFAULT_INTERVAL_SIZE) {
					notify -= Constant.DEFAULT_INTERVAL_SIZE;
					getListener().onSchedule(this, header, count,
							header.getFileLength());
				}
				if (count >= header.getFileLength()) {
					break;
				}
			} catch (SocketTimeoutException e) {
				times++;
				// ���� DEFAULT_TRY_TIMES ���ղ����κ����ݣ�����Ϊ����ʧ��
				if (times >= Constant.DEFAULT_TRY_TIMES) {
					break;
				}
			} catch (IOException e) {
				e.printStackTrace(MyLogger.logger);
				break;
			}
		}
		return count;
	}

	public boolean verifyFile(File file,int checkType,String checkCode){
		if (checkType == FileHeader.CHECK_NO) {
			return true;
		}
		String check = "null";
		try {
			switch (checkType) {
			case FileHeader.CHECK_MD5:
				check = MD5.toString(file);
				break;
			case FileHeader.CHECK_SHA1:
				check = SHA1.toString(file);
				break;
			case FileHeader.CHECK_CRC16:
			case FileHeader.CHECK_CRC32:
				check = CRC32.toString(file);
				break;		
			default:
				return false;
			}
		} catch (IOException e) {
			return false;
		}
		if (check.equals(checkCode)) {
			return true;
		}
		return false;
	}
	
	public boolean checkFile(FileHeader header, File fileRecv, long count) {
		// �ļ��Ѿ�������ϣ����濪ʼ����ļ���
		if (count == header.getFileLength()) {
			// �ļ�����ɹ�,���濪ʼУ���ļ�����ѡѡ��
			if (verifyFile(fileRecv, header.getCheckType(), header.getCheckCode())){
				return true;
			}else{
				if (fileRecv.exists()) {
					fileRecv.delete();
				}
				System.out.println("�ļ�("+header.getFileName()+")У��ʧ�ܣ�");
				return false;
			}
		} else if (count > header.getFileLength()) {
			// �յ������ݶ���,����ǰ���޶��˶�ȡ����������˲�Ӧ�ó����������
			MyLogger.println("�ļ���" + fileRecv.toString() + "��д�����ݳ����ļ�����");
			return false;
		} else {
			// �յ������ݲ���,�����Ƿ�����ļ�
			MyLogger.println("�ļ���" + fileRecv.toString() + "��д�����ݵ����ļ�����");
			if (fileRecv.exists()) {
				fileRecv.delete();
			}
			return false;
		}
	}

	public boolean checkFile(File fileRecv) {
		if (fileRecv.exists() && fileRecv.isDirectory()) {
			// ����ͬ��Ŀ¼ʱ �� �ܾ�����Ŀ¼
			MyLogger.println("�޷������ļ���" + fileRecv.toString() + "������ͬ��Ŀ¼");
			return false;
		}
		if (fileRecv.exists() && fileRecv.isFile()) {
			/*
			 * TODO: ����ļ���ͻ�������� config �����Ƿ񸲸��ļ��� �����������ļ���ע�⣬�û�Ҳ����ͨ���޸� header
			 * ���������ļ��� ���ҽ��� config �涨������ʱ���û��ſ��Ե���ָ���Ƿ񸲸�ĳ���ļ���
			 */
			// ��ǰ�Ĳ����ǣ� ֱ�Ӹ���

		}
		if (!fileRecv.exists()) {
			File dir = fileRecv.getParentFile();
			// ����Ҳ����ļ�������Ŀ¼����ô�ʹ���Ŀ¼
			if (!dir.exists() && !dir.mkdirs()) {
				return false;
			}
			try {
				fileRecv.createNewFile();
			} catch (IOException e) {
				MyLogger.println("�޷������ļ���" + fileRecv.toString() + "���ļ�����ʧ��");
				e.printStackTrace(MyLogger.logger);
				return false;
			}
		}
		// �����ļ��Ƿ��д
		if (!fileRecv.canWrite()) {
			MyLogger.println("�޷������ļ���" + fileRecv.toString() + "���ļ��޷�д��");
			return false;
		}
		return true;
	}

}
