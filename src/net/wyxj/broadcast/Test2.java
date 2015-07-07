package net.wyxj.broadcast;

//import java.io.BufferedReader;
//import java.io.InputStreamReader;
import java.util.HashSet;

public class Test2 {
	
	public static void main(String arg[]) throws Exception {
//		BufferedReader reader = new BufferedReader(new InputStreamReader(
//				System.in));		
		HashSet<HostInfo> hostSet = new HashSet<HostInfo>();
		HostInfo info1 = new HostInfo().setAddress("127.0.0.1").setPort(31416).setName("wanghui");
		hostSet.add(info1);
		HostInfo info2 = new HostInfo().setAddress("127.0.0.2").setPort(31416).setName("wanghui");
		hostSet.add(info2);
		HostInfo info3 = new HostInfo().setAddress("127.0.0.1").setPort(31416).setName("wanghui2");
		hostSet.add(info3);
		HostInfo info4 = new HostInfo().setAddress("127.0.0.1").setPort(31415).setName("wanghui");
		hostSet.add(info4);
		System.out.println(hostSet.toString());
		
//		HostManager manager = new HostManager();
//		manager.start();		
//		while (true) {
//			String cmd = reader.readLine();			
//			if(cmd.equals("shutdown")){
//				manager.shutdown();	
//			}else if(cmd.equals("start")){
//				manager.start();	
//			}else if(cmd.equals("show")){
//				HostInfo[] hosts = manager.getAllHost();
//				for(HostInfo info : hosts){
//					System.out.println("Host : " + info.toString());
//				}
//			}else if(cmd.equals("exit")){
//				break;
//			}
//		}			
	}
	
}
