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
 * RSAMaker���ڲ���RSA����Կ��������Կ��˽Կ�� <br/>
 * ͬʱ�ṩ�������� encrypt �� decrypt �򵥵Ķ������ṩ���ܺͽ��ܹ��ܡ� <br/>
 * �������ɵ�keyΪ�����������˲�����ֱ��ʹ�ø���ļ��ܺͽ��ܷ�����
 * @author ��
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
			throw new InvalidParameterException("key��bit����Ӧ����500��16384֮��");
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
	 * ��byte������ù�Կ���ܡ�
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
	 * �Բ��ø�ʵ��encrypt�������ܺ��byte�������
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
	 * ��ȡ RSA key �Ĺ���ָ����һ��Ϊ65537
	 * @return ����ָ��
	 */
	public BigInteger getPublicExponent() {
		return mPublic.getPublicExponent();
	}

	/**
	 * ��ȡ RSA key ��˽��ָ��
	 * @return ˽��ָ��
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
	 * ��ȡ��Կ
	 * @return ��Կ
	 */
	@Override
	public RSAPublicKey getPublicKey() {
		return mPublic;
	}

	/**
	 * ��ȡ˽Կ
	 * @return ˽Կ
	 */
	@Override
	public RSAPrivateKey getPrivateKey() {
		return mPrivate;
	}	
	
	/**
	 * ��ȡ RSA key ��ģ�� 
	 * @return ģ��
	 */
	@Override
	public BigInteger getModulus() {
		return mPublic.getModulus();
	}

}
