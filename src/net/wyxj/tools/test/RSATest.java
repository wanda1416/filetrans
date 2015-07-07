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
		System.out.println("原文： " + str);
		System.out.println("密文： " + de);
		System.out.println("解密： "
				+ decryptor.decryptToString(Base64.decode(de)));
		
	}

}
