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
 * �ṩ����RSA�㷨��һЩͨ�ù��ߣ������� ���� BigInteger ���� Key �� Key ת��Ϊ byte[] ,������ byte[] ת��Ϊ
 * Key
 * 
 * @author ��
 * 
 */
public class RSAUtils {

	/**
	 * ���ݸ����� X.509 ��ʽ����������RSA��Կ
	 * @param src X.509��ʽ�Ĺ�Կ����
	 * @return RSA��Կ
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
	 * ���ݸ����� PKCS#8 ��ʽ����������RSA˽Կ
	 * @param src PKCS#8��ʽ��˽Կ����
	 * @return RSA˽Կ
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
	 * ���ݸ����� <B>ģ��</B>�� <B>ָ��</B>����һ��RSA��Կ
	 * 
	 * @param modulus
	 * @param publicExponent
	 * @return RSA��Կ
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
	 * ���ݸ����� <B>ģ��</B>�� <B>ָ��</B>����һ��RSA˽Կ
	 * 
	 * @param modulus
	 * @param privateExponent
	 * @return RSA˽Կ
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
	 * �Ƚ����� byte[] �����Ƿ���ȣ�ֻ�е�ͬΪnull���߳�������Լ���Ӧλ�����ʱ�ŷ���true
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
