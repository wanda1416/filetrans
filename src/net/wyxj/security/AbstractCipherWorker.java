package net.wyxj.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public abstract class AbstractCipherWorker implements ICipherWorker {

	@Override
	public byte[] encrypt(String src) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		// TODO Auto-generated method stub
		return encrypt(src.getBytes());
	}

	@Override
	public byte[] encrypt(String src, String charset)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return encrypt(src.getBytes(charset));
	}

	@Override
	public String decryptToString(byte[] src) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		// TODO Auto-generated method stub
		return new String(decrypt(src));
	}

	@Override
	public String decryptToString(byte[] src, String charset)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return new String(decrypt(src), charset);
	}

}
