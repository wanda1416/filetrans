package net.wyxj.security.rsa;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class RSAEncryptor extends AbstractRSAWorker implements IRSAKey {

	private RSAPublicKey mPublic;
	private RSAPrivateKey mPrivate;

	private int mType;
	public final static int TYPE_NOKEY = 0;
	public final static int TYPE_PUBLIC = 1;
	public final static int TYPE_PRIVATE = 2;

	public RSAEncryptor() {
		mType = RSAEncryptor.TYPE_NOKEY;
	}

	public RSAEncryptor(RSAPublicKey key) {
		mType = RSAEncryptor.TYPE_PUBLIC;
		mPublic = key;
	}

	public RSAEncryptor(RSAPrivateKey key) {
		mType = RSAEncryptor.TYPE_PRIVATE;
		mPrivate = key;
	}

	public RSAKey getKey() {
		if (mType == RSAEncryptor.TYPE_NOKEY) {
			return null;
		} else if (mType == RSAEncryptor.TYPE_PUBLIC) {
			return (RSAKey) mPublic;
		} else {
			return (RSAKey) mPrivate;
		}
	}

	@Override
	public byte[] encrypt(byte[] src) {
		if (mType == RSAEncryptor.TYPE_NOKEY) {
			return null;
		} else if (mType == RSAEncryptor.TYPE_PUBLIC) {
			try {
				mCipher.init(Cipher.ENCRYPT_MODE, mPublic);
			} catch (InvalidKeyException e) {
				return null;
			}
		} else {
			try {
				mCipher.init(Cipher.ENCRYPT_MODE, mPrivate);
			} catch (InvalidKeyException e) {
				return null;
			}
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
		return null;
	}

	@Override
	public boolean isPublicKey() {
		return mType == RSAEncryptor.TYPE_PUBLIC;
	}

	@Override
	public boolean isPrivaryKey() {
		return mType == RSAEncryptor.TYPE_PRIVATE;
	}

	@Override
	public BigInteger getModulus() {
		if (mType == RSAEncryptor.TYPE_NOKEY) {
			return null;
		} else if (mType == RSAEncryptor.TYPE_PUBLIC) {
			return mPublic.getModulus();
		} else {
			return mPrivate.getModulus();
		}
	}

	@Override
	public BigInteger getExponent() {
		if (mType == RSAEncryptor.TYPE_NOKEY) {
			return null;
		} else if (mType == RSAEncryptor.TYPE_PUBLIC) {
			return mPublic.getPublicExponent();
		} else {
			return mPrivate.getPrivateExponent();
		}
	}

	@Override
	public RSAPublicKey getPublicKey() {
		if (mType == RSAEncryptor.TYPE_NOKEY
				|| mType == RSAEncryptor.TYPE_PRIVATE) {
			return null;
		} else {
			return mPublic;
		}
	}

	@Override
	public RSAPrivateKey getPrivateKey() {
		if (mType == RSAEncryptor.TYPE_NOKEY
				|| mType == RSAEncryptor.TYPE_PUBLIC) {
			return null;
		} else {
			return mPrivate;
		}
	}

	public static RSAEncryptor fromByteArray(byte[] src, int type)
			throws InvalidKeySpecException {
		if (type == RSAEncryptor.TYPE_NOKEY) {
			return null;
		} else if (type == RSAEncryptor.TYPE_PUBLIC) {
			return new RSAEncryptor(RSAUtils.getPublicKey(src));
		} else {
			return new RSAEncryptor(RSAUtils.getPrivateKey(src));
		}
	}

	public static RSAEncryptor fromByteArray(InputStream src, int type)
			throws InvalidKeySpecException, IOException {
		if (type == RSAEncryptor.TYPE_NOKEY) {
			return null;
		} else if (type == RSAEncryptor.TYPE_PUBLIC) {
			return new RSAEncryptor(RSAUtils.getPublicKey(src));
		} else {
			return new RSAEncryptor(RSAUtils.getPrivateKey(src));
		}
	}

	public static RSAEncryptor fromByteArray(String filename, int type)
			throws InvalidKeySpecException, IOException {
		if (type == RSAEncryptor.TYPE_NOKEY) {
			return null;
		} else if (type == RSAEncryptor.TYPE_PUBLIC) {
			return new RSAEncryptor(RSAUtils.getPublicKey(filename));
		} else {
			return new RSAEncryptor(RSAUtils.getPrivateKey(filename));
		}
	}

	public byte[] toByteArray() {
		if (mType == RSAEncryptor.TYPE_NOKEY) {
			return null;
		} else if (mType == RSAEncryptor.TYPE_PUBLIC) {
			return RSAUtils.toByteArray(mPublic);
		} else {
			return RSAUtils.toByteArray(mPrivate);
		}
	}

}
