package net.wyxj.tools.filetrans;

public class HexStringUtils {
	
	private final static byte[] hex = "0123456789ABCDEF".getBytes();

	private static int parse(char c) {
		if (c >= 'a')
			return (c - 'a' + 10) & 0x0f;
		if (c >= 'A')
			return (c - 'A' + 10) & 0x0f;
		return (c - '0') & 0x0f;
	}
	
	// 从字节数组到十六进制字符串转换
	/**
	 * 转换字节型数组为16进制大写字符串
	 * @param b  字节型数组
	 * @return 16进制数的字符串,大写
	 */
	public static String bytes2HexString(byte[] b) {
		byte[] buff = new byte[2 * b.length];
		for (int i = 0; i < b.length; i++) {
			buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
			buff[2 * i + 1] = hex[b[i] & 0x0f];
		}
		return new String(buff);
	}

	// 从十六进制字符串到字节数组转换
	/**
	 * 转换字节型数组为16进制小写字符串
	 * @param b  字节型数组
	 * @return 16进制数的字符串,小写
	 */
	public static byte[] hexString2Bytes(String hexstr) {
		byte[] b = new byte[hexstr.length() / 2];
		int j = 0;
		for (int i = 0; i < b.length; i++) {
			char c0 = hexstr.charAt(j++);
			char c1 = hexstr.charAt(j++);
			b[i] = (byte) ((parse(c0) << 4) | parse(c1));
		}
		return b;
	}
	
	/**
	 * 比较两个 byte[] 对象是否相等，只有当同为null或者长度相等以及对应位置相等时才返回true 
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