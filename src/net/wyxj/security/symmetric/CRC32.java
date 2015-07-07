package net.wyxj.security.symmetric;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 提供了4种静态方法来计算文件、字符串或者字节数据的CRC32值
 * 
 * @author 辉
 * 
 */
public class CRC32 {

	public static final int CACHE_SIZE = 32 * 1024;

	private static java.util.zip.CRC32 crc32;

	static {
		crc32 = new java.util.zip.CRC32();
	}
	
	public static String toString(File file) throws IOException{
		return Long.toHexString( getResult(file) );
	}

	public static long getResult(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		int length = -1;
		byte[] cache = new byte[CACHE_SIZE];
		while ((length = fis.read(cache)) != -1) {
			crc32.update(cache, 0, length);
		}
		fis.close();
		long result = crc32.getValue();
		crc32.reset();
		return result;
	}

	public static long getResult(String origin, String charset)
			throws UnsupportedEncodingException {
		return getResult(origin.getBytes(charset));
	}

	public static long getResult(String filename) throws IOException {
		return getResult(new File(filename));
	}

	public static long getResult(byte[] origin) {
		crc32.update(origin);
		long result = crc32.getValue();
		crc32.reset();
		return result;
	}

}