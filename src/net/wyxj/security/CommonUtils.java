package net.wyxj.security;

public class CommonUtils {

	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	
	private final static String[] upperHexDigits = { "0", "1", "2", "3", "4", "5",
		"6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };

	/**
	 * 转换字节型数组为16进制小写字符串
	 * @param b  字节型数组
	 * @return 16进制数的字符串,小写
	 */
	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			int n = b[i];
			if (n < 0){
				n = 256 + n;
			}
			int d1 = n / 16;
			int d2 = n % 16;
			String str = hexDigits[d1] + hexDigits[d2];
			resultSb.append(str);
		}
		return resultSb.toString();
	}

	/**
	 * 转换字节型数组为16进制大写字符串
	 * @param b  字节型数组
	 * @return 16进制数的字符串,大写
	 */
	public static String byteArrayToUpperHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			int n = b[i];
			if (n < 0){
				n = 256 + n;
			}
			int d1 = n / 16;
			int d2 = n % 16;
			String str = upperHexDigits[d1] + upperHexDigits[d2];
			resultSb.append(str);
		}
		return resultSb.toString();
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
