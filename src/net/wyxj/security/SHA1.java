package net.wyxj.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1 {
	
	public static final int CACHE_SIZE = 4096 * 10; 
	
	/**
	 * 计算文件的SHA1值，返回40字节长的字符串
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public static String toString(File file) throws IOException{
		return CommonUtils.byteArrayToHexString(toByteArray(file));
	}
	
	/**
	 * 计算文件的SHA1值，返回20字节长的byte数组
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] toByteArray(File file) throws IOException{
		FileInputStream fis = new FileInputStream(file);
		int length = -1;
		byte[] cache = new byte[CACHE_SIZE];
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		while((length=fis.read(cache))!=-1){
			md.update(cache, 0, length);
		}
		fis.close();
		return md.digest();
	}
	
	/**
	 * 将字符串用SHA1加密为40字节长的十六进制字符串
	 * 
	 * @param origin
	 *            待加密的字符串
	 * @return 40字节的16进制字符串数组
	 * @throws NoSuchAlgorithmException 
	 */
	public static String toString(String origin) {
		return toString(origin.getBytes());
	}
	
	/**
	 * 将字符串用SHA1加密为40字节长的十六进制字符串
	 * 
	 * @param origin
	 *            待加密的字符串
	 * @return 40字节的16进制字符串数组
	 * @throws NoSuchAlgorithmException 
	 */
	public static String toString(byte[] origin){
		return CommonUtils.byteArrayToHexString(toByteArray(origin));
	}

	/**
	 * 将字符串用SHA1加密为20字节长的二进制数组
	 * 
	 * @param origin
	 *            待加密的字符串
	 * @return 20字节的字节数组，共160位。
	 * @throws NoSuchAlgorithmException 
	 */
	public static byte[] toByteArray(String origin){
		return toByteArray(origin.getBytes());
	}
	
	/**
	 * 将字符串用SHA1加密为20字节长的二进制数组
	 * 
	 * @param origin
	 *            待加密的字符串
	 * @return 20字节的字节数组，共160位。
	 * @throws NoSuchAlgorithmException 
	 */
	public static byte[] toByteArray(byte[] origin){
		byte[] result = null;
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		result = md.digest(origin);
		return result;
	}	
	
}