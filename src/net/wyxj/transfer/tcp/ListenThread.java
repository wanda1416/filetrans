package net.wyxj.transfer.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import net.wyxj.transfer.Configuration;
import net.wyxj.transfer.Constant;
import net.wyxj.transfer.FileHeader;
import net.wyxj.transfer.MyLogger;
import net.wyxj.transfer.def.IReceiveListener;
import net.wyxj.transfer.def.IServerListener;
import net.wyxj.transfer.def.ITransferListener;

public class ListenThread extends Thread {

	private Configuration config;

	private IReceiveListener listener;

	private HashSet<ReceiveThread> threadSet;

	private HashMap<Integer, ReceiveThread> connectMap;

	private boolean exit = false;

	private ServerSocket fileServerSocket = null;

	public ListenThread(Configuration config) {
		setConfig(config);
		setThreadSet(new HashSet<ReceiveThread>());
		setConnectMap(new HashMap<Integer, ReceiveThread>());
	}

	private IServerListener recall;

	@Override
	public void run() {
		// ����������
		try {
			fileServerSocket = new ServerSocket(getConfig().getLocalPort(),
					getConfig().getMaxBacklog(),
					InetAddress.getByName(getConfig().getLocalAddress()));
			System.out.println(String.format("������(%s:%d)�����ɹ�", getConfig()
					.getLocalAddress(), getConfig().getLocalPort()));
			fileServerSocket.setSoTimeout(Constant.DEFAULT_SOCKET_TIMEOUT);
		} catch (IOException e) {
			MyLogger.println("����������ʧ�ܣ�����˿��Ƿ�ռ��");
			e.printStackTrace(MyLogger.logger);
			if (getServerListener() != null) {
				getServerListener().startFailed();
			}
			return;
		}
		// �����������ɹ�
		if (getServerListener() != null) {
			getServerListener().startServer();
		}
		while (isExit() == false) {
			try {
				// �˴�����һ�� Socket������Socket���ڸ��߳��¹رա�
				Socket socket = fileServerSocket.accept();
				String m_clientIP = socket.getInetAddress().getHostAddress();
				int m_clientPort = socket.getPort();
				MyLogger.println(String.format("�������յ����Կͻ���(%s:%d)����������",
						m_clientIP, m_clientPort));
				// һ���̴߳���һ���ͻ��˵�����
				ReceiveThread transfer = new ReceiveThread(socket,
						getConfig());
				transfer.setListener(getTransferListener());
				// ���������߳�
				transfer.start();
			} catch (SocketTimeoutException e) {
				continue;
			} catch (IOException e) {
				e.printStackTrace(MyLogger.logger);
				break;
			}
		}
		if (getServerListener() != null) {
			getServerListener().beforeCloseServer();
		}
		// �ر���
		try {
			if (fileServerSocket != null)
				fileServerSocket.close();
			System.out.println("�ļ����շ������ر�,�����˳�");
		} catch (IOException e) {
			e.printStackTrace(MyLogger.logger);
		}
		if (getServerListener() != null) {
			getServerListener().afterCloseServer();
		}
		return;
	}

	public boolean isExit() {
		return exit;
	}

	public void exit() {
		if( this.isAlive()){
			this.interrupt();
		}
		for (ReceiveThread thread : getThreadSet()) {
			thread.exit();
		}
		this.exit = true;
	}

	public Configuration getConfig() {
		return config;
	}

	public void setConfig(Configuration config) {
		this.config = config;
	}

	public HashSet<ReceiveThread> getThreadSet() {
		return threadSet;
	}

	public void setThreadSet(HashSet<ReceiveThread> threadSet) {
		this.threadSet = threadSet;
	}

	public IReceiveListener getReceiveListener() {
		return listener;
	}

	public void setReceiveListener(IReceiveListener listener) {
		this.listener = listener;
	}

	public ITransferListener<ReceiveThread> getTransferListener() {
		return m_transferListener;
	}

	private ITransferListener<ReceiveThread> m_transferListener = new ITransferListener<ReceiveThread>() {
		
		/**
		 * �ļ�����ǰ�Ļص����������÷������� false ʱ��Socket���ܾ����ո��ļ��� <br/>
		 * �������� true ʱ�� getConnectMap() �Ż���� fileID �� transferThread��
		 */
		@Override
		public boolean beforeTransfer(ReceiveThread thread, FileHeader header) {
			boolean result = getReceiveListener().beforeReceive(header);
			if (result) {
				MyLogger.println("������ [" + thread.getSocket().toString()
						+ "] �����ļ���" + header.getFileName());
				getConnectMap().put(header.getFileID(), thread);
			} else {
				MyLogger.println("�Ѿ��ܾ�[" + thread.getSocket().toString()
						+ "] �����ļ���" + header.getFileName());
			}
			return result;
		}

		@Override
		public void afterTransfer(ReceiveThread thread, FileHeader header,
				boolean succeed) {
			MyLogger.println("�� [" + thread.getSocket().toString() + "] �����ļ���"
					+ header.getFileName() + (succeed ? "�ɹ�" : "ʧ��"));
			getConnectMap().remove(header.getFileID());
			getReceiveListener().afterReceive(header, succeed);
		}

		@Override
		public void onSchedule(ReceiveThread thread, FileHeader header,
				long receivedBytes, long totalBytes) {
			getReceiveListener().onSchedule(header, receivedBytes, totalBytes);
		}

		@Override
		public void start(ReceiveThread thread) {
			MyLogger.println("�����̣߳�" + thread.toString() + "����");
			getThreadSet().add(thread);
			if (getServerListener() != null) {
				getServerListener().startConnect(thread.getSocket());
			}
		}

		@Override
		public void end(ReceiveThread thread) {
			MyLogger.println("�����̣߳�" + thread.toString() + "�ر�");
			getThreadSet().remove(thread);
			if (getServerListener() != null) {
				getServerListener().closeConnect(thread.getSocket());
			}
			try {
				thread.getSocket().close();
			} catch (IOException e) {
				MyLogger.println("�ر�Socketʱ�����쳣:"
						+ thread.getSocket().toString());
				e.printStackTrace(MyLogger.logger);
			}
		}
	};

	public void abort(int fileID) {
		ReceiveThread thread = getConnectMap().get(fileID);
		if (thread != null) {
			MyLogger.println("�ļ�(" + fileID + "):�Ѿ��жϽ���");
			thread.abort();
		} else {
			MyLogger.println("�ļ�(" + fileID + "):����δ��ʼ,�����Ѿ�����");
		}
	}

	public void abortAll() {
		for (Entry<Integer, ReceiveThread> entry : getConnectMap().entrySet()) {
			ReceiveThread thread = entry.getValue();
			thread.abort();
		}
		MyLogger.println("�Ѿ��ж��������ļ��Ľ���");
	}

	public HashMap<Integer, ReceiveThread> getConnectMap() {
		return connectMap;
	}

	public void setConnectMap(HashMap<Integer, ReceiveThread> connectMap) {
		this.connectMap = connectMap;
	}

	public IServerListener getServerListener() {
		return recall;
	}

	public void setServerListener(IServerListener recall) {
		this.recall = recall;
	}

}
