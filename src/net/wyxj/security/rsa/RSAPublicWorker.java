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

public class RSAPublicWorker extends AbstractRSAWorker implements IRSAKey {

	private RSAPublicKey mPublic;

	public RSAPublicWorker(RSAPublicKey publicKey) {
		mPublic = publicKey;
	}

	public RSAPublicWorker(BigInteger modulus, BigInteger publicExponent)
			throws InvalidKeySpecException {
		mPublic = RSAUtils.generatePublicKey(modulus, publicExponent);
	}

	@Override
	public byte[] encrypt(byte[] src) {
		try {
			mCipher.init(Cipher.ENCRYPT_MODE, mPublic);
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
			mCipher.init(Cipher.DECRYPT_MODE, mPublic);
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
	public boolean isPublicKey() {
		return true;
	}

	@Override
	public boolean isPrivaryKey() {
		return false;
	}

	@Override
	public BigInteger getModulus() {
		return mPublic.getModulus();
	}

	@Override
	public BigInteger getExponent() {
		return mPublic.getPublicExponent();
	}

	@Override
	public RSAPublicKey getPublicKey() {
		return mPublic;
	}

	@Override
	public RSAPrivateKey getPrivateKey() {
		return null;
	}

	public static RSAPublicWorker fromByteArray(byte[] src) throws InvalidKeySpecException {
		return new RSAPublicWorker(RSAUtils.getPublicKey(src));
	}

	public static RSAPublicWorker fromInputStream(InputStream src) throws InvalidKeySpecException, IOException {
		return new RSAPublicWorker(RSAUtils.getPublicKey(src));
	}
	
	public static RSAPublicWorker fromInputStream(String filename) throws InvalidKeySpecException, IOException {
		return new RSAPublicWorker(RSAUtils.getPublicKey(filename));
	}

	public byte[] toByteArray() {
		return RSAUtils.toByteArray(mPublic);
	}

}
