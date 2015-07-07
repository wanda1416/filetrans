package net.wyxj.tools.test;

import java.io.File;

import net.wyxj.security.symmetric.AES;
import net.wyxj.security.symmetric.Base64;
import net.wyxj.security.symmetric.DES;
import net.wyxj.security.symmetric.MD5;

public class SymmetricTest {

	public static void main(String[] args) {
		desTest();
	}
	
	public static void desTest(){
		String input = "key1.crt";
		String output = "key2.crt";
		try {
			DES.encryptFile(input , "output", "password");
			DES.decryptFile("output" , output , "password");
			System.out.println(MD5.toString(new File(input)));
			System.out.println(MD5.toString(new File(output)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void dataTest(){
		String password = "haha1234567";
		String message = "wanghui";
		
		AES aes = new AES(password);
		byte[] decrypted = aes.encrypt(message);		
		String msg1 = Base64.encode(decrypted);
		System.out.println(message + " -> " + msg1);
		
		AES aes1 = new AES(password);
		String msg2 = aes1.decryptToString(Base64.decode(msg1));
		System.out.println(msg1 + " -> " + msg2);
	}

}
