package net.wyxj.broadcast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

import com.google.gson.Gson;

public class CommonUtils {
	
	public static Gson gson = new Gson();	

	public static ArrayList<String> getLocalIPList(){
		ArrayList<String> ipList = new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces
						.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ip = (InetAddress) addresses.nextElement();
					if (ip != null && ip instanceof Inet4Address) {
						ipList.add(ip.getHostAddress());
					}
				}
			}
		} catch (Exception e) {
			System.err.println("获取ip地址列表出错");
		}
		return ipList;
	}
	
	public static String getMainIPAddress(){
		String ip = null;
		ArrayList<String> ipList = getLocalIPList();
		for(int i=0;i<ipList.size();i++){
			if( ipList.get(i).equals("127.0.0.1") == false &&
					ipList.get(i).startsWith("192.168.") ){
				ip = ipList.get(i);
				break;
			}
		}
		if(ip == null){
			ip = "127.0.0.1";
		}
		return ip;
	}
	
	public static InetAddress getBroadcastAddress() {
		InetAddress address;
		try {
			address = InetAddress.getByName(getMainIPAddress());
		} catch (UnknownHostException e) {
			return null;
		}
		if (address != null) {
			String addr = address.getHostAddress();
			if(addr.equals("127.0.0.1")){
				return null;
			}
			String ip = addr.substring(0, addr.lastIndexOf(".")) + ".255";
			try {
				return InetAddress.getByName(ip);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}		
		return null;
	}
	
	
}
