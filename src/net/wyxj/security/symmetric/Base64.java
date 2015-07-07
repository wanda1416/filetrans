package net.wyxj.security.symmetric;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Base64 {

	private static char[] legalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
			.toCharArray();

	public static final int MODE_STAND = 1;
	public static final int MODE_NOLINE = 2;
	public static final int MODE_REGEX = 3;
	public static final int MODE_URL = 4;
	public static final int MODE_SSH = 5;

	private static final int MODE_DEFAULT = MODE_STAND;
	private static int cur_mode = MODE_DEFAULT;

	public static void setMode(int m) throws IllegalArgumentException {
		if (m != MODE_STAND && m != MODE_NOLINE	&& m != MODE_URL && m != MODE_SSH) {
			throw new IllegalArgumentException("no this mode");
		}
		cur_mode = m;
	}

	public static int getMode() {
		return cur_mode;
	}

	public static void resetMode() {
		cur_mode = MODE_DEFAULT;
	}

	public static String encode(byte[] data, int mode) {
		Base64.setMode(mode);
		String result = encode(data);
		Base64.resetMode();
		return result;
	}

	/**
	 * 将字符串进行base64编码
	 * 
	 * @param data
	 * @return
	 */
	public static String encode(byte[] data) {
		int start = 0;
		int len = data.length;
		StringBuilder buf = new StringBuilder(data.length * 3 / 2);

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

			if ((cur_mode == MODE_SSH) && n++ >= 15) {
				n = 0;
				buf.append("\n");
			}

			if ((cur_mode == MODE_STAND) && n++ >= 18) {
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

		if (cur_mode == MODE_URL) {
			for (int j = 0; j < buf.length(); j++) {
				char ch = buf.charAt(j);
				if (ch == '+') {
					buf.setCharAt(j, '-');
				} else if (ch == '/') {
					buf.setCharAt(j, '*');
				}
			}
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
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] decode(String str) {
		StringBuilder parse = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch == '\r' || ch == '\n' || ch == ' ') {
				continue;
			} else if (ch == '-') {
				parse.append('+');
			} else if (ch == '*') {
				parse.append('/');
			} else {
				parse.append(ch);
			}
		}
		str = parse.toString();
		byte[] decodedBytes = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			decode(str, bos);
			decodedBytes = bos.toByteArray();
		} catch (IOException e) {
			System.err.println("Error while decoding BASE64: " + e.toString());
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
				System.err.println("Error while decoding BASE64: "
						+ e.toString());
			}
		}
		return decodedBytes;
	}

	private static void decode(String str, OutputStream os) throws IOException {
		int i = 0;
		int len = str.length();
		while (true) {
			while (i < len && (str.charAt(i) <= ' '))
				i++;
			if (i == len)
				break;
			int tri = (decode(str.charAt(i)) << 18)
					+ (decode(str.charAt(i + 1)) << 12)
					+ (decode(str.charAt(i + 2)) << 6)
					+ (decode(str.charAt(i + 3)));
			os.write((tri >> 16) & 255);
			if (str.charAt(i + 2) == '=')
				break;
			os.write((tri >> 8) & 255);
			if (str.charAt(i + 3) == '=')
				break;
			os.write(tri & 255);
			i += 4;
		}
	}

}