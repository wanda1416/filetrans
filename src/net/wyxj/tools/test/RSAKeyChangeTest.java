package net.wyxj.tools.test;

import java.io.FileNotFoundException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import net.wyxj.security.rsa.cert.CertUtils;
import net.wyxj.security.rsa.cert.RSAKeyStorage;
import net.wyxj.security.symmetric.Base64;

public class RSAKeyChangeTest {

	public static void main(String[] args) {
		RSAKeyStorage storage = RSAKeyStorage.open("test.keystore", "1234567890"); 
		
		RSAPrivateKey pri = storage.getPrivateKey("key1", "123456");
		RSAPublicKey pub = cert();
		
		checkKeyPair(pub,pri);		
	}
	
	public static RSAPublicKey cert(){
		Certificate cert;
		try {
			cert = CertUtils.readPublicKey("key1.crt");
		} catch (FileNotFoundException e) {
			System.err.println("找不到文件!");
			return null;
		}
		RSAPublicKey pub = (RSAPublicKey) cert.getPublicKey();
		return pub;
	}
	
	public static void checkKeyPair(RSAPublicKey pub,RSAPrivateKey pri) {

		byte[] data = "Hello,World! This is a test message, the result must be true."
				.getBytes();
		Signature signer;
	    
		try {
			signer = Signature.getInstance("SHA1withRSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return;
		}
		
		try {
			signer.initSign(pri);
			signer.update(data);
			byte[] signeddata = signer.sign();
			System.out.println("消息：" + new String(data));
			System.out.println("签名: " + Base64.encode(signeddata) );
			
			// 模拟篡改消息或者签名：
			//data[0] ++;
			
			signer.initVerify(pub);
			signer.update(data);
			boolean result = signer.verify(signeddata);
			System.out.println("消息验证结果：" + result );
			
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		}	

	}

}
