package net.wyxj.transfer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class MyLogger {

	// public static PrintStream logger = System.err; 
	public static PrintStream logger = new PrintStream(new SpaceStream());
 	
	public static synchronized void println(String msg){
		System.err.println(msg);
		System.err.flush();
	}
	
	public static synchronized void print(String msg){
		System.err.print(msg);
	}
	
	public static class SpaceStream extends OutputStream {

		@Override
		public void write(int b) throws IOException {
			// 什么都不做
			
		}
		
	}
	
}
