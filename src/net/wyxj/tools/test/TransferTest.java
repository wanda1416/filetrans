package net.wyxj.tools.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

import net.wyxj.broadcast.HostInfo;
import net.wyxj.broadcast.HostListener;
import net.wyxj.broadcast.HostManager;
import net.wyxj.broadcast.Message;
import net.wyxj.broadcast.Reason;
import net.wyxj.tools.filetrans.ConfigTools;
import net.wyxj.transfer.Configuration;
import net.wyxj.transfer.FileHeader;
import net.wyxj.transfer.FileTransfer;
import net.wyxj.transfer.TransferManager;
import net.wyxj.transfer.def.IReceiveListener;
import net.wyxj.transfer.def.ISendListener;
import net.wyxj.transfer.impl.ReceiveAdapter;

public class TransferTest {

	public static FileTransfer transfer = null;

	public static InetSocketAddress remote = null;
	
	public static TransferManager manager = null;

	public static void main(String arg[]) throws Exception {
		
		ConfigTools.setConfigPath(".");
		Configuration config = new Configuration();
		config.setReceivePath("./recv");

		final HostManager hostManager = new HostManager();
		hostManager.setHostListener(new HostListener() {

			@Override
			public void onAdd(HostInfo info, Reason reason) {
				System.out.println("Message : ���� " + info.getName()
						+ " ���� , ��ǰ�������� : " + hostManager.getHostSet().size());
			}

			@Override
			public void onDelete(HostInfo info, Reason reason) {
				System.out.println("Message : ���� " + info.getName()
						+ " ����, ��ǰ�������� : " + hostManager.getHostSet().size());
			}

			@Override
			public void onDefault(HostInfo info, Message message) {

			}
		});

		hostManager.start();

		IReceiveListener receiver = new ReceiveAdapter() {
			long time = 0;
			int rate = 0;

			@Override
			public boolean beforeReceive(FileHeader header) {
				time = System.currentTimeMillis();
				return true;
			}

			@Override
			public void afterReceive(FileHeader header, boolean succeed) {
				System.out.println("�����ļ���" + header.getFileName() + ",��С��"
						+ header.getFileLength() / 1024 / 1024 + "MB.��ʱ��"
						+ (System.currentTimeMillis() - time) + "ms");
				rate = 0;
			}

			@Override
			public void onSchedule(FileHeader header, long sentBytes,
					long totalBytes) {
				int r = (int) (100 * (double) (sentBytes) / totalBytes);
				if (r >= rate + 10) {
					System.out.println("�ļ����ս��ȣ�" + r + "%" + ",ID:"
							+ header.getFileID());
					rate = r;
				}
				if (r > 100) {
					transfer.abortRecv(header.getFileID());
				}
			}
		};

		ISendListener sender = new ISendListener() {
			int rate = 0;

			@Override
			public void beforeSend(FileHeader header) {
				System.out.println("���������ļ���" + header.getFileName() + ",ID:"
						+ header.getFileID());
			}

			@Override
			public void afterSend(FileHeader header, boolean succeed) {
				System.out
						.println("�ļ���" + header.getFileName() + ",����"
								+ (succeed ? "�ɹ�" : "ʧ��") + ",ID:"
								+ header.getFileID());
				rate = 0;
			}

			@Override
			public void onSchedule(FileHeader header, long sentBytes,
					long totalBytes) {
				int r = (int) (100 * (double) (sentBytes) / totalBytes);
				if (r >= rate + 10) {
					System.out.println("�ļ����ͽ��ȣ�" + r + "%" + ",ID:"
							+ header.getFileID());
					rate = r;
				}
				if (r > 100) {
					transfer.abortSend(header.getFileID());
				}
			}
		};

		transfer = FileTransfer.newInstance();
		manager = new TransferManager(transfer);
		manager.setSendListener(sender);
		manager.setReceiveListener(receiver);
		transfer.start(config);

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		
		while (true) {
			String cmd = reader.readLine();
			if (cmd.equals("exit")) {
				System.out.println("���" + cmd + " - �жϷ�����");
				hostManager.shutdown();
				transfer.shutdown();
				break;
			} else if (cmd.equals("show")) {
				HostInfo[] hosts = hostManager.getAllHost();
				for (HostInfo info : hosts) {
					System.out.println("Host : " + info.toString());
				}
			}
			if (cmd.startsWith("sendfile")) {
				String[] cmds = cmd.split(" ");
				for (int i = 1; i < cmds.length; i++) {
					transfer.sendFile(remote, new File(cmds[i]));
				}
			}
		}

		config.saveConfig();
		transfer.destroy();
	}

	public static void println(String data) {
		System.out.println(data);
	}

	public static void println(int data) {
		System.out.println(data);
	}

}

// ReceiveAdapter listener = new ReceiveAdapter() {
// @Override
// public boolean beforeReceive(FileHeader header) {
// println("�յ��ļ���������:"+header.getFileName());
// return true;
// }
// @Override
// public void afterReceive(FileHeader header,boolean succeed) {
// println("�ļ����ս��:"+header.getFileName()+"\t"+(succeed?"�ɹ�":"ʧ��"));
// }
// @Override
// public void onSchedule(FileHeader header, long sentBytes, long totalBytes) {
// System.out.println("�ļ�(" + header.getFileName() + ")���ͽ��ȣ�"
// + (double) (sentBytes) / totalBytes);
// }
// };
// transfer.setReceiveListener(listener);

