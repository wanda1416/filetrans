package net.wyxj.security.rsa;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import net.wyxj.security.rsa.key.IRSAKeyReader;
import net.wyxj.security.rsa.key.IRSAKeyWriter;
import net.wyxj.security.rsa.key.PEMKeyReader;
import net.wyxj.security.rsa.key.PEMKeyWriter;

/**
 * 提供用于RSA算法的一些通用工具，包括： 根据 BigInteger 构造 Key 将 Key 转换为 byte[] ,或者由 byte[] 转换为
 * Key
 * 
 * @author 辉
 * 
 */
public class RSAUtils {

	/**
	 * 根据给定的 X.509 格式的数据生成RSA公钥
	 * @param src X.509格式的公钥数据
	 * @return RSA公钥
	 */
	public static RSAPublicKey generatePublicKey(byte[] src){
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(src);
		KeyFactory keyFactory;
		try {
			keyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}  
		RSAPublicKey key;
	    try {
			key = (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (InvalidKeySpecException e) {
			return null;
		}
		return key;
	}
	
	/**
	 * 根据给定的 PKCS#8 格式的数据生成RSA私钥
	 * @param src PKCS#8格式的私钥数据
	 * @return RSA私钥
	 */
	public static RSAPrivateKey generatePrivateKey(byte[] src){
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(src);
		KeyFactory keyFactory;
		try {
			keyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}  
		RSAPrivateKey key;
	    try {
			key = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (InvalidKeySpecException e) {
			return null;
		}
		return key;		
	}
	
	/**
	 * 根据给定的 <B>模数</B>和 <B>指数</B>生成一个RSA公钥
	 * 
	 * @param modulus
	 * @param publicExponent
	 * @return RSA公钥
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static RSAPublicKey generatePublicKey(BigInteger modulus,
			BigInteger publicExponent) {
		RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, publicExponent);
		KeyFactory keyFactory;
		PublicKey key;
		try {
			keyFactory = KeyFactory.getInstance("RSA");
			key = keyFactory.generatePublic(spec);
		} catch (NoSuchAlgorithmException e) {
			return null;
		} catch (InvalidKeySpecException e) {
			return null;
		}
		return (RSAPublicKey) key;
	}

	/**
	 * 根据给定的 <B>模数</B>和 <B>指数</B>生成一个RSA私钥
	 * 
	 * @param modulus
	 * @param privateExponent
	 * @return RSA私钥
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static RSAPrivateKey generatePrivateKey(BigInteger modulus,
			BigInteger privateExponent) {
		RSAPrivateKeySpec spec = new RSAPrivateKeySpec(modulus, privateExponent);
		KeyFactory keyFactory;
		PrivateKey key;
		try {
			keyFactory = KeyFactory.getInstance("RSA");
			key = keyFactory.generatePrivate(spec);
		} catch (NoSuchAlgorithmException e) {
			return null;
		} catch (InvalidKeySpecException e) {
			return null;
		}
		return (RSAPrivateKey) key;
	}

	public static void toFile(String filename,RSAPublicKey key) throws IOException{
		FileUtils.writeToFile(filename, toByteArray(key));
	}

	public static void toFile(String filename,RSAPrivateKey key) throws IOException{
		FileUtils.writeToFile(filename, toByteArray(key));
	}

	public static RSAPublicKey getPublicKey(String filename)
			throws IOException{
		return getPublicKey(FileUtils.readByteArray(filename));
	}

	public static RSAPrivateKey getPrivateKey(String filename)
			throws IOException{
		return getPrivateKey(FileUtils.readByteArray(filename));
	}

	public static RSAPublicKey getPublicKey(InputStream input)
			throws IOException{
		return RSAUtils.getPublicKey(FileUtils.readByteArray(input));
	}

	public static RSAPrivateKey getPrivateKey(InputStream input)
			throws IOException{
		return RSAUtils.getPrivateKey(FileUtils.readByteArray(input));
	}
	
	public static byte[] toByteArray(RSAPublicKey key) {
		IRSAKeyWriter writer = new PEMKeyWriter();
		return writer.writePublicKey(key);
	}

	public static byte[] toByteArray(RSAPrivateKey key) {
		IRSAKeyWriter writer = new PEMKeyWriter();
		return writer.writePrivateKey(key);
	}

	public static RSAPublicKey getPublicKey(byte[] src) {
		IRSAKeyReader reader = new PEMKeyReader();
		return reader.readPublicKey(src);
	}

	public static RSAPrivateKey getPrivateKey(byte[] src) {
		IRSAKeyReader reader = new PEMKeyReader();
		return reader.readPrivateKey(src);
	}

	/**
	 * 比较两个 byte[] 对象是否相等，只有当同为null或者长度相等以及对应位置相等时才返回true
	 * 
	 * @param src
	 * @param dest
	 * @return
	 */
	public static boolean equals(byte[] src, byte[] dest) {
		if (src == null && dest == null) {
			return true;
		}
		if (src == null || dest == null || src.length != dest.length) {
			return false;
		}
		for (int i = 0; i < src.length; i++) {
			if (src[i] != dest[i]) {
				return false;
			}
		}
		return true;
	}
}
