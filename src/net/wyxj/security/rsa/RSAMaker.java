package net.wyxj.security.rsa;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

/**
 * RSAMaker用于产生RSA的密钥，包括公钥和私钥。 <br/>
 * 同时提供两个方法 encrypt 和 decrypt 简单的对数据提供加密和解密功能。 <br/>
 * 由于生成的key为随机产生，因此不建议直接使用该类的加密和解密方法。
 * @author 辉
 *
 */
public class RSAMaker extends AbstractRSAWorker implements IRSAKey{

	public static final int DEFAULT_KEY_LENGTH = 2048;
	
	private RSAPublicKey mPublic;
	private RSAPrivateKey mPrivate;

	public RSAMaker(RSAPublicKey publicKey,RSAPrivateKey privateKey){
		mPublic = publicKey;
		mPrivate = privateKey;
	}

	public static RSAMaker newInstance(){
		return new RSAMaker();
		
	}
	
	public static RSAMaker newInstance(int keyLength){
		return new RSAMaker(keyLength);
	}
	
	private RSAMaker() {
		generateKey(DEFAULT_KEY_LENGTH);
	}

	private RSAMaker(int keyLength){
		generateKey(keyLength);
	}

	private void generateKey(int keyLength) {
		if (keyLength < 500 || keyLength > 16384) {
			throw new InvalidParameterException("key的bit长度应该在500到16384之间");
		}
		KeyPairGenerator kpg;
		try {
			kpg = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return;
		}
		kpg.initialize(keyLength,new SecureRandom());
		KeyPair keypair = kpg.generateKeyPair();
		mPublic = (RSAPublicKey) keypair.getPublic();
		mPrivate = (RSAPrivateKey) keypair.getPrivate();
	}

	/**
	 * 对byte数组采用公钥加密。
	 * @param src
	 * @return
	 * @throws InvalidKeyException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws UnsupportedEncodingException
	 */
	@Override
	public byte[] encrypt(byte[] src) {
		try {
			mCipher.init(Cipher.ENCRYPT_MODE, mPublic);
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
	
	/**
	 * 对采用该实例encrypt方法加密后的byte数组解密
	 * @param src
	 * @return
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws UnsupportedEncodingException
	 */
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

	/**
	 * 获取 RSA key 的公共指数，一般为65537
	 * @return 公共指数
	 */
	public BigInteger getPublicExponent() {
		return mPublic.getPublicExponent();
	}

	/**
	 * 获取 RSA key 的私有指数
	 * @return 私有指数
	 */
	public BigInteger getPrivateExponent() {
		return mPrivate.getPrivateExponent();
	}
	
	public RSAPublicWorker getRSAPublicWorker(){
		return new RSAPublicWorker(mPublic);
	}

	public RSAPrivateWorker getRSAPrivateWorker(){
		return new RSAPrivateWorker(mPrivate);
	}

	@Override
	public boolean isPublicKey() {
		return true;
	}

	@Override
	public boolean isPrivaryKey() {
		return true;
	}

	@Override
	public BigInteger getExponent() {
		return null;
	}
	
	/**
	 * 获取公钥
	 * @return 公钥
	 */
	@Override
	public RSAPublicKey getPublicKey() {
		return mPublic;
	}

	/**
	 * 获取私钥
	 * @return 私钥
	 */
	@Override
	public RSAPrivateKey getPrivateKey() {
		return mPrivate;
	}	
	
	/**
	 * 获取 RSA key 的模数 
	 * @return 模数
	 */
	@Override
	public BigInteger getModulus() {
		return mPublic.getModulus();
	}

}
