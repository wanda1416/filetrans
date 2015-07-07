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
		// 打开到发送端的IO流
		try {
			getSocket().setSoTimeout(Constant.DEFAULT_SOCKET_TIMEOUT);
			in = getSocket().getInputStream();
			out = getSocket().getOutputStream();
		} catch (IOException e) {
			e.printStackTrace(MyLogger.logger);
			getListener().end(this);
			return;
		}
		// 进入与客户端的交互过程
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
					// 读取的数据无效，跳过。
					continue;
				}
				// 询问是否接收该文件，如果需要更改文件名，可以修改header对象
				// beforeReceive 与 afterReceive 必须同时先后调用，除非 beforeReceive==false
				if (getListener().beforeTransfer(this, header)) {
					boolean result = false;
					if (header.getFileType() == FileHeader.TYPE_FILE) {
						// 即将从 in 中读取一个文件
						out.write("Allow\r\n".getBytes());
						result = receiveFile(header, in);
						if (result) {
							out.write("OK\r\n".getBytes());
							// TODO 成功接收到一个文件后的处理动作。
							// ！注意不能代替了 ListenThread的动作.

							exit(false);
						} else {
							out.write("Abort\r\n".getBytes());
						}
					} else {
						// TODO:处理目录的情况,目前的策略是不接收目录

						out.write("Disallow\r\n".getBytes());
					}
					getListener().afterTransfer(this, header, result);
				} else {
					// TODO: 拒绝接收该文件

					out.write("Disallow\r\n".getBytes());
					continue;
				}
			} catch (SocketTimeoutException e) {
				times++;
				// 连续 DEFAULT_TRY_TIMES 次收不到任何数据，就认为连接失败
				if (times >= Constant.DEFAULT_TRY_TIMES) {
					break;
				}
			} catch (IOException e) {
				break;
			}
		} // end while
			// 即将退出
		try {
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace(MyLogger.logger);
		}
		getListener().end(this);
	}

	/**
	 * 接收文件
	 * 
	 * @param header
	 *            文件信息
	 * @param in
	 *            输入流
	 * @return 文件是否成功写入
	 */
	private boolean receiveFile(FileHeader header, InputStream in) {
		File fileRecv = new File(getReceivePath() + "/" + header.getFileName());
		if (!checkFile(fileRecv)) {
			// 如果认为 fileRecv 无法写入，那么将返回。
			return false;
		}

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fileRecv);
		} catch (FileNotFoundException e) {
			MyLogger.println("无法打开文件：" + fileRecv.toString() + "，文件接收失败");
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
		// 调用 checkFile 检查文件是否接收成功
		return checkFile(header, fileRecv, count);
	}

	public long readFromInputStream(OutputStream fos, InputStream in,
			FileHeader header) {
		int times = 0;
		long count = 0;
		long notify = 0;
		long remain = header.getFileLength();
		byte[] buffer = new byte[Constant.DEFAULT_BUFFER_SIZE];
		// 进入消息循环模块
		while (isExit() == false) {
			if (isAbort() == true) {
				// 收到文件中断接收标志,清除并退出
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
				// 用于通知文件传输进度
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
				// 连续 DEFAULT_TRY_TIMES 次收不到任何数据，就认为连接失败
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
		// 文件已经接收完毕，下面开始检查文件：
		if (count == header.getFileLength()) {
			// 文件传输成功,下面开始校验文件，可选选项
			if (verifyFile(fileRecv, header.getCheckType(), header.getCheckCode())){
				return true;
			}else{
				if (fileRecv.exists()) {
					fileRecv.delete();
				}
				System.out.println("文件("+header.getFileName()+")校验失败！");
				return false;
			}
		} else if (count > header.getFileLength()) {
			// 收到的数据多了,由于前文限定了读取的数量，因此不应该出现这种情况
			MyLogger.println("文件：" + fileRecv.toString() + "，写入数据超出文件长度");
			return false;
		} else {
			// 收到的数据不足,考虑是否清除文件
			MyLogger.println("文件：" + fileRecv.toString() + "，写入数据低于文件长度");
			if (fileRecv.exists()) {
				fileRecv.delete();
			}
			return false;
		}
	}

	public boolean checkFile(File fileRecv) {
		if (fileRecv.exists() && fileRecv.isDirectory()) {
			// 存在同名目录时 ， 拒绝覆盖目录
			MyLogger.println("无法创建文件：" + fileRecv.toString() + "，存在同名目录");
			return false;
		}
		if (fileRecv.exists() && fileRecv.isFile()) {
			/*
			 * TODO: 检查文件冲突，将根据 config 决定是否覆盖文件。 还是重命名文件，注意，用户也可以通过修改 header
			 * 来重命名文件。 当且仅当 config 规定允许覆盖时，用户才可以单独指定是否覆盖某个文件。
			 */
			// 当前的策略是： 直接覆盖

		}
		if (!fileRecv.exists()) {
			File dir = fileRecv.getParentFile();
			// 如果找不到文件所属的目录，那么就创建目录
			if (!dir.exists() && !dir.mkdirs()) {
				return false;
			}
			try {
				fileRecv.createNewFile();
			} catch (IOException e) {
				MyLogger.println("无法创建文件：" + fileRecv.toString() + "，文件接收失败");
				e.printStackTrace(MyLogger.logger);
				return false;
			}
		}
		// 测试文件是否可写
		if (!fileRecv.canWrite()) {
			MyLogger.println("无法创建文件：" + fileRecv.toString() + "，文件无法写入");
			return false;
		}
		return true;
	}

}
