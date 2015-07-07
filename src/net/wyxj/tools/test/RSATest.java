package net.wyxj.tools.test;

import java.io.IOException;

import net.wyxj.security.rsa.RSADecryptor;
import net.wyxj.security.rsa.RSAEncryptor;
import net.wyxj.security.rsa.RSAMaker;
import net.wyxj.security.rsa.RSAUtils;
import net.wyxj.security.symmetric.Base64;


public class RSATest {

	public static void main(String[] args) throws IOException {
		
		RSAMaker maker = RSAMaker.newInstance();
		RSAEncryptor encryptor = new RSAEncryptor(maker.getPublicKey());
		RSADecryptor decryptor = new RSADecryptor(maker.getPrivateKey());
		RSAUtils.toFile("public.key",encryptor.getPublicKey());
		RSAUtils.toFile("private.key",decryptor.getPrivateKey());
		String str = "wanghui";
		String de = Base64.encode(encryptor.encrypt(str));
		System.out.println("ԭ�ģ� " + str);
		System.out.println("���ģ� " + de);
		System.out.println("���ܣ� "
				+ decryptor.decryptToString(Base64.decode(de)));
		
	}

}
