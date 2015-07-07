package net.wyxj.security.symmetric;

import java.io.UnsupportedEncodingException;

public interface ICipherWorker {

	public byte[] encrypt(String src);

	public byte[] encrypt(byte[] src);

	public byte[] encrypt(String src, String charset)
			throws UnsupportedEncodingException;

	public byte[] decrypt(byte[] src);

	public String decryptToString(byte[] src);

	public String decryptToString(byte[] src, String charset)
			throws UnsupportedEncodingException;

}
