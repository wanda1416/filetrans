package net.wyxj.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public interface ICipherWorker {

	public byte[] encrypt(String src) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException;

	public byte[] encrypt(byte[] src) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException;

	public byte[] encrypt(String src, String charset)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException;

	public byte[] decrypt(byte[] src) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException;

	public String decryptToString(byte[] src) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException;

	public String decryptToString(byte[] src, String charset)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException;

}
