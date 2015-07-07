package net.wyxj.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 提供了3种返回类型，接受4种输入参数，总计12种静态方法来计算文件、字符串或者字节数据的MD5值
 * 例如： MD5.toString("wyxj19940611root") = "d1d9da9b8358ef03bb15e99033f9191d"
 * @author 辉
 *
 */
public class MD5 {
	
	public static final int CACHE_SIZE = 4096 * 10; 
	
	private static MessageDigest md;
	
	static{
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public static String toString(File file) throws IOException {
		return CommonUtils.byteArrayToHexString(toByteArray(file));
	}

	public static byte[] toByteArray(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		int length = -1;
		byte[] cache = new byte[CACHE_SIZE];
		while ((length = fis.read(cache)) != -1) {
			md.update(cache, 0, length);
		}
		fis.close();
		return md.digest();
	}

	public static String toBase64(File file) throws IOException {
		return Base64.encode(toByteArray(file));
	}

	public static String toBase64(String origin, String charset)
			throws UnsupportedEncodingException {
		return Base64.encode(toByteArray(origin.getBytes(charset)));
	}

	public static String toBase64(String origin) {
		return Base64.encode(toByteArray(origin.getBytes()));
	}

	public static String toBase64(byte[] origin) {
		return Base64.encode(toByteArray(origin));
	}
	
	public static String toString(String origin, String charset)
			throws UnsupportedEncodingException {
		return toString(origin.getBytes(charset));
	}

	public static String toString(String origin) {
		return toString(origin.getBytes());
	}

	public static String toString(byte[] origin) {
		return CommonUtils.byteArrayToHexString(toByteArray(origin));
	}

	public static byte[] toByteArray(String origin) {
		return toByteArray(origin.getBytes());
	}

	public static byte[] toByteArray(byte[] origin) {
		byte[] result = md.digest(origin);
		return result;
	}
	
	public static byte[] toByteArray(String origin, String charset)
			throws UnsupportedEncodingException {
		return toByteArray(origin.getBytes(charset));
	}
}