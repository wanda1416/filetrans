package net.wyxj.security.rsa;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public abstract class AbstractRSAWorker implements IRSAWorker {

	protected static Cipher mCipher;

	static {
		try {
			mCipher = Cipher.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}

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
		// TODO Auto-generated method stub
		return new String(decrypt(src), charset);
	}

}
