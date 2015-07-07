package net.wyxj.security.symmetric;

import java.io.UnsupportedEncodingException;

public abstract class AbstractCipherWorker implements ICipherWorker {

	@Override
	public byte[] encrypt(String src) {
		return encrypt(src.getBytes());
	}

	@Override
	public byte[] encrypt(String src, String charset)
			throws UnsupportedEncodingException {
		return encrypt(src.getBytes(charset));
	}

	@Override
	public String decryptToString(byte[] src) {
		return new String(decrypt(src));
	}

	@Override
	public String decryptToString(byte[] src, String charset)
			throws UnsupportedEncodingException {
		return new String(decrypt(src), charset);
	}

}
