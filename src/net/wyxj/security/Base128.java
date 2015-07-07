package net.wyxj.security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Base128 {
	
	private static byte[] legalChars = null;
	
	static{
		legalChars = new byte[128];
		for(int i=0;i<128;i++){
			legalChars[i]=(byte) i;
		}
	}
	public static final int MODE_STAND = 1;
	public static final int MODE_NOLINE = 2;
	public static final int MODE_REGEX = 4;
	public static final int MODE_URL = 8;
	private static int cur_mode = 1;

	public static void setMode(int m) throws IllegalArgumentException {
		if (m != MODE_STAND || m != MODE_NOLINE || m != MODE_REGEX
				|| m != MODE_URL) {
			throw new IllegalArgumentException("no this mode");
		}
		cur_mode = m;
	}

	public static int getMode() {
		return cur_mode;
	}

	/**
	 * 将字符串进行base64编码
	 * @param data
	 * @return
	 */
	public static String encode(byte[] data) {
		int start = 0;
		int len = data.length;
		StringBuffer buf = new StringBuffer(data.length * 3 / 2);

		int end = len - 3;
		int i = start;
		int n = 0;

		while (i <= end) {
			int d = ((((int) data[i]) & 0x0ff) << 16)
					| ((((int) data[i + 1]) & 0x0ff) << 8)
					| (((int) data[i + 2]) & 0x0ff);

			buf.append(legalChars[(d >> 18) & 63]);
			buf.append(legalChars[(d >> 12) & 63]);
			buf.append(legalChars[(d >> 6) & 63]);
			buf.append(legalChars[d & 63]);

			i += 3;
			if (n++ >= 18 && (cur_mode == MODE_STAND)) {
				n = 0;
				buf.append("\r\n");
			}
		}
		if (i == start + len - 2) {
			int d = ((((int) data[i]) & 0x0ff) << 16)
					| ((((int) data[i + 1]) & 255) << 8);

			buf.append(legalChars[(d >> 18) & 63]);
			buf.append(legalChars[(d >> 12) & 63]);
			buf.append(legalChars[(d >> 6) & 63]);
			buf.append("=");
		} else if (i == start + len - 1) {
			int d = (((int) data[i]) & 0x0ff) << 16;

			buf.append(legalChars[(d >> 18) & 63]);
			buf.append(legalChars[(d >> 12) & 63]);
			buf.append("==");
		}
		return buf.toString();
	}

	private static int decode(char c) {
		if (c >= 'A' && c <= 'Z')
			return ((int) c) - 65;
		else if (c >= 'a' && c <= 'z')
			return ((int) c) - 97 + 26;
		else if (c >= '0' && c <= '9')
			return ((int) c) - 48 + 26 + 26;
		else
			switch (c) {
			case '+':
				return 62;
			case '/':
				return 63;
			case '=':
				return 0;
			default:
				throw new RuntimeException("unexpected code: " + c);
			}
	}

	/**
	 * 对base64编码还原为原来的字符
	 * @param str
	 * @return
	 */
	public static byte[] decode(String str) {
		// clear the symbol "\r\n"
		str = str.replaceAll("\r\n", "");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			decode(str, bos);
		} catch (IOException e) {
			throw new RuntimeException();
		}
		byte[] decodedBytes = bos.toByteArray();
		try {
			bos.close();
			bos = null;
		} catch (IOException ex) {
			System.err.println("Error while decoding BASE64: " + ex.toString());
		}
		return decodedBytes;
	}

	private static void decode(String s, OutputStream os) throws IOException {
		int i = 0;
		int len = s.length();
		while (true) {
			while (i < len && (s.charAt(i) <= ' '))
				i++;
			if (i == len)
				break;
			int tri = (decode(s.charAt(i)) << 18)
					+ (decode(s.charAt(i + 1)) << 12)
					+ (decode(s.charAt(i + 2)) << 6)
					+ (decode(s.charAt(i + 3)));
			os.write((tri >> 16) & 255);
			if (s.charAt(i + 2) == '=')
				break;
			os.write((tri >> 8) & 255);
			if (s.charAt(i + 3) == '=')
				break;
			os.write(tri & 255);
			i += 4;
		}
	}

}