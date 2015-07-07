package net.wyxj.security.rsa;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class RSAPrivateWorker extends AbstractRSAWorker implements IRSAKey {

	private RSAPrivateKey mPrivate;

	public RSAPrivateWorker(RSAPrivateKey privateKey) {
		mPrivate = privateKey;
	}

	public RSAPrivateWorker(BigInteger modulus, BigInteger privateExponent)
			throws InvalidKeySpecException {
		mPrivate = RSAUtils.generatePrivateKey(modulus, privateExponent);
	}

	@Override
	public byte[] encrypt(byte[] src) {
		try {
			mCipher.init(Cipher.ENCRYPT_MODE, mPrivate);
		} catch (InvalidKeyException e1) {
			return null;
		}
		byte[] dest;
		try {
			dest = mCipher.doFinal(src);
		} catch (IllegalBlockSizeException e) {
			return null;
		} catch (BadPaddingException e) {
			return null;
		}
		return dest;
	}

	@Override
	public byte[] decrypt(byte[] src) {
		try {
			mCipher.init(Cipher.DECRYPT_MODE, mPrivate);
		} catch (InvalidKeyException e) {
			return null;
		}
		byte[] dest;
		try {
			dest = mCipher.doFinal(src);
		} catch (IllegalBlockSizeException e) {
			return null;
		} catch (BadPaddingException e) {
			return null;
		}
		return dest;
	}

	@Override
	public boolean isPublicKey() {
		return false;
	}

	@Override
	public boolean isPrivaryKey() {
		return true;
	}

	@Override
	public BigInteger getModulus() {
		return mPrivate.getModulus();
	}

	@Override
	public BigInteger getExponent() {
		return mPrivate.getPrivateExponent();
	}

	@Override
	public RSAPublicKey getPublicKey() {
		return null;
	}

	@Override
	public RSAPrivateKey getPrivateKey() {
		return mPrivate;
	}

	public static RSAPrivateWorker fromByteArray(byte[] src)
			throws InvalidKeySpecException {
		return new RSAPrivateWorker(RSAUtils.getPrivateKey(src));
	}

	public static RSAPrivateWorker fromInputStream(InputStream src)
			throws InvalidKeySpecException, IOException {
		return new RSAPrivateWorker(RSAUtils.getPrivateKey(src));
	}
	
	public static RSAPrivateWorker fromInputStream(String filename)
			throws InvalidKeySpecException, IOException {
		return new RSAPrivateWorker(RSAUtils.getPrivateKey(filename));
	}
	
	public byte[] toByteArray() {
		return RSAUtils.toByteArray(mPrivate);
	}

}
