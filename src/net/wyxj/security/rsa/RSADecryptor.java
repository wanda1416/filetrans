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

public class RSADecryptor extends AbstractRSAWorker implements IRSAKey {

	private RSAPublicKey mPublic;
	private RSAPrivateKey mPrivate;

	private int mType;
	public final static int TYPE_NOKEY = 0;
	public final static int TYPE_PUBLIC = 1;
	public final static int TYPE_PRIVATE = 2;

	public RSADecryptor() {
		mType = RSADecryptor.TYPE_NOKEY;
	}

	public RSADecryptor(RSAPublicKey key) {
		mType = RSADecryptor.TYPE_PUBLIC;
		mPublic = key;
	}

	public RSADecryptor(RSAPrivateKey key) {
		mType = RSADecryptor.TYPE_PRIVATE;
		mPrivate = key;
	}

	public void setKey(RSAPublicKey key) {
		mType = RSADecryptor.TYPE_PUBLIC;
		mPublic = key;
	}

	public void setKey(RSAPrivateKey key) {
		mType = RSADecryptor.TYPE_PRIVATE;
		mPrivate = key;
	}

	public RSAKey getKey() {
		if (mType == RSADecryptor.TYPE_NOKEY) {
			return null;
		} else if (mType == RSADecryptor.TYPE_PUBLIC) {
			return (RSAKey) mPublic;
		} else {
			return (RSAKey) mPrivate;
		}
	}

	@Override
	public byte[] encrypt(byte[] src) {
		return null;
	}

	@Override
	public byte[] decrypt(byte[] src) {
		if (mType == RSADecryptor.TYPE_NOKEY) {
			return null;
		} else if (mType == RSADecryptor.TYPE_PUBLIC) {
			try {
				mCipher.init(Cipher.DECRYPT_MODE, mPublic);
			} catch (InvalidKeyException e) {
				return null;
			}
		} else {
			try {
				mCipher.init(Cipher.DECRYPT_MODE, mPrivate);
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
	public boolean isPublicKey() {
		return mType == RSADecryptor.TYPE_PUBLIC;
	}

	@Override
	public boolean isPrivaryKey() {
		return mType == RSADecryptor.TYPE_PRIVATE;
	}

	@Override
	public BigInteger getModulus() {
		if (mType == RSADecryptor.TYPE_NOKEY) {
			return null;
		} else if (mType == RSADecryptor.TYPE_PUBLIC) {
			return mPublic.getModulus();
		} else {
			return mPrivate.getModulus();
		}
	}

	@Override
	public BigInteger getExponent() {
		if (mType == RSADecryptor.TYPE_NOKEY) {
			return null;
		} else if (mType == RSADecryptor.TYPE_PUBLIC) {
			return mPublic.getPublicExponent();
		} else {
			return mPrivate.getPrivateExponent();
		}
	}

	@Override
	public RSAPublicKey getPublicKey() {
		if (mType == RSADecryptor.TYPE_NOKEY
				|| mType == RSADecryptor.TYPE_PRIVATE) {
			return null;
		} else {
			return mPublic;
		}
	}

	@Override
	public RSAPrivateKey getPrivateKey() {
		if (mType == RSADecryptor.TYPE_NOKEY
				|| mType == RSADecryptor.TYPE_PUBLIC) {
			return null;
		} else {
			return mPrivate;
		}
	}

	public static RSADecryptor fromByteArray(byte[] src, int type)
			throws InvalidKeySpecException {
		if (type == RSADecryptor.TYPE_NOKEY) {
			return null;
		} else if (type == RSADecryptor.TYPE_PUBLIC) {
			return new RSADecryptor(RSAUtils.getPublicKey(src));
		} else {
			return new RSADecryptor(RSAUtils.getPrivateKey(src));
		}
	}

	public static RSADecryptor fromByteArray(InputStream src, int type)
			throws InvalidKeySpecException, IOException {
		if (type == RSADecryptor.TYPE_NOKEY) {
			return null;
		} else if (type == RSADecryptor.TYPE_PUBLIC) {
			return new RSADecryptor(RSAUtils.getPublicKey(src));
		} else {
			return new RSADecryptor(RSAUtils.getPrivateKey(src));
		}
	}

	public static RSADecryptor fromByteArray(String filename, int type)
			throws InvalidKeySpecException, IOException {
		if (type == RSADecryptor.TYPE_NOKEY) {
			return null;
		} else if (type == RSADecryptor.TYPE_PUBLIC) {
			return new RSADecryptor(RSAUtils.getPublicKey(filename));
		} else {
			return new RSADecryptor(RSAUtils.getPrivateKey(filename));
		}
	}

	public byte[] toByteArray() {
		if (mType == RSADecryptor.TYPE_NOKEY) {
			return null;
		} else if (mType == RSADecryptor.TYPE_PUBLIC) {
			return RSAUtils.toByteArray(mPublic);
		} else {
			return RSAUtils.toByteArray(mPrivate);
		}
	}

}
