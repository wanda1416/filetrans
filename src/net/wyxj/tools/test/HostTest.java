package net.wyxj.tools.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import net.wyxj.broadcast.HostInfo;
import net.wyxj.broadcast.HostListener;
import net.wyxj.broadcast.HostManager;
import net.wyxj.broadcast.Message;
import net.wyxj.broadcast.Reason;

public class HostTest {

	public static HostManager hostManager = null;

	public static void main(String arg[]) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));

		hostManager = new HostManager();
		hostManager.setHostListener(new HostListener() {

			@Override
			public void onAdd(HostInfo info, Reason reason) {
				System.out.println("Message : 主机 " + info.getName()
						+ " 上线 , 当前在线主机 : " + hostManager.getHostSet().size());
			}

			@Override
			public void onDelete(HostInfo info, Reason reason) {
				System.out.println("Message : 主机 " + info.getName()
						+ " 下线, 当前在线主机 : " + hostManager.getHostSet().size());
			}

			@Override
			public void onDefault(HostInfo info, Message message) {
				switch(message.getType()){
				case 100:
					String data = message.getString("data");
					System.out.println("Message : " + data);					
					break;
				default:
					System.out.println("Message Type : " + message.getType());
					break;
				}
			}
		});

		hostManager.start();
		
		Message msg = new Message();
		msg.setType(100);
		
		while (true) {
			String cmd = reader.readLine();
			if (cmd.equals("shutdown")) {
				hostManager.shutdown();
			} else if (cmd.equals("start")) {
				hostManager.start();
			} else if (cmd.equals("show")) {
				HostInfo[] hosts = hostManager.getAllHost();
				for (HostInfo info : hosts) {
					System.out.println("Host : " + info.toString());
				}
			} else if (cmd.startsWith("send")) {
				String str = cmd.substring(5, cmd.length());
				msg.putString("data", str);
				hostManager.sendMessage(msg);
			} else if (cmd.equals("exit")) {
				break;
			}
		}

	}

}
