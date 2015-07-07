package net.wyxj.tools.test;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import net.wyxj.security.rsa.RSADecryptor;
import net.wyxj.security.rsa.RSAEncryptor;
import net.wyxj.security.rsa.key.PEMKeyReader;
import net.wyxj.security.symmetric.Base64;

public class RSAKeyTest {

	public static void main(String[] args) {

		PEMKeyReader pem = new PEMKeyReader();
		
		RSAPublicKey pub = null;
		RSAPrivateKey pri = null;
		try {
			pub = pem.readPublicKey("public.key");
			pri = pem.readPrivateKey("private.key");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(pri==null){
			System.out.println("无法解析私钥");
		}		
		if(pub==null){
			System.out.println("无法解析公钥");
		}
		
		RSAEncryptor encryptor = new RSAEncryptor(pri);
		byte[] en = encryptor.encrypt("wanghui");
		
		System.out.println(Base64.encode(en));
		
		RSADecryptor decryptor = new RSADecryptor(pub);
		byte[] de = decryptor.decrypt(en);
		System.out.println(new String(de));
		
	}

}
