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
	
	// ���ֽ����鵽ʮ�������ַ���ת��
	/**
	 * ת���ֽ�������Ϊ16���ƴ�д�ַ���
	 * @param b  �ֽ�������
	 * @return 16���������ַ���,��д
	 */
	public static String bytes2HexString(byte[] b) {
		byte[] buff = new byte[2 * b.length];
		for (int i = 0; i < b.length; i++) {
			buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
			buff[2 * i + 1] = hex[b[i] & 0x0f];
		}
		return new String(buff);
	}

	// ��ʮ�������ַ������ֽ�����ת��
	/**
	 * ת���ֽ�������Ϊ16����Сд�ַ���
	 * @param b  �ֽ�������
	 * @return 16���������ַ���,Сд
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
	 * �Ƚ����� byte[] �����Ƿ���ȣ�ֻ�е�ͬΪnull���߳�������Լ���Ӧλ�����ʱ�ŷ���true 
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