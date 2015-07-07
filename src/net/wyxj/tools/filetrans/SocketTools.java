package net.wyxj.tools.filetrans;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

public class SocketTools {

	public static String getLANAddress() {
		ArrayList<String> ipList = getLocalAddressList();
		for (int i = 0; i < ipList.size(); i++) {
			if (ipList.get(i).startsWith("192.168.")) {
				return ipList.get(i);
			}
		}
		for (int i = 0; i < ipList.size(); i++) {
			if (ipList.get(i).startsWith("10.")) {
				return ipList.get(i);
			}
		}
		if (ipList.size() >= 1) {
			return ipList.get(0);
		}
		return "127.0.0.1";
	}

	public static ArrayList<String> getLocalAddressList() {
		ArrayList<String> ipList = new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface
					.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces
						.nextElement();
				Enumeration<InetAddress> addresses = netInterface
						.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ip = (InetAddress) addresses.nextElement();
					if (ip != null && ip.getAddress().length == 4) {
						ipList.add(ip.getHostAddress());
					}
				}
			}
		} catch (Exception e) {
			System.err.println("error ： 获取ip地址列表时发生错误");
		}
		return ipList;
	}
	
}


