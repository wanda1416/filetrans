package net.wyxj.security;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DES extends AbstractCipherWorker {

	private String m_key;

	public DES(String key) {
		m_key = key;
	}

	/**
	 * ���ܺ�������һ�����ļ���Ϊ���Ĳ�����
	 * 
	 * @param encryptString
	 *            �����ܵ�����
	 * @param encryptKey1
	 *            ���ܵ���Կ
	 * @return ����
	 * @throws Exception
	 */
	public static void encryptInputToOutput(InputStream is, OutputStream os,
			String password) throws Exception {
		Cipher cipher = getCipher(password, Cipher.ENCRYPT_MODE);
		CipherInputStream input = new CipherInputStream(is, cipher);
		int length = -1;
		byte[] cache = new byte[1024];
		while ((length = input.read(cache)) != -1) {
			os.write(cache, 0, length);
		}
		input.close();
	}

	public static void decryptInputToOutput(InputStream is, OutputStream os,
			String password) throws Exception {
		Cipher cipher = getCipher(password, Cipher.DECRYPT_MODE);
		CipherInputStream input = new CipherInputStream(is, cipher);
		int length = -1;
		byte[] cache = new byte[1024];
		while ((length = input.read(cache)) != -1) {
			os.write(cache, 0, length);
		}
		input.close();
	}

	public static long encryptFile(String input, String output, String password)
			throws FileNotFoundException, Exception {
		long start = System.currentTimeMillis();
		encryptInputToOutput(new FileInputStream(input), new FileOutputStream(
				output), password);
		return System.currentTimeMillis() - start;
	}

	public static long decryptFile(String input, String output, String password)
			throws FileNotFoundException, Exception {
		long start = System.currentTimeMillis();
		decryptInputToOutput(new FileInputStream(input), new FileOutputStream(
				output), password);
		return System.currentTimeMillis() - start;
	}

	/**
	 * @deprecated
	 * @param password
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] get64BitPassword(String password)
			throws NoSuchAlgorithmException {
		byte[] result1 = MD5.toByteArray(password);
		byte[] result2 = new byte[8];
		for (int i = 0; i < 8; i++) {
			result2[i] = result1[i];
			result2[i] += result1[i + 8];
		}
		return result2;
	}

	private static Cipher getCipher(String password, int mode) {
		byte[] md5 = MD5.toByteArray(password);
		byte[] encryptKey = new byte[8];
		byte[] iv = new byte[8];
		System.arraycopy(md5, 0, encryptKey, 0, 8);
		System.arraycopy(md5, 8, iv, 0, 8);
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(encryptKey, "DES");
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(mode, key, zeroIv);
			return cipher;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public byte[] encrypt(byte[] src) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = getCipher(m_key, Cipher.ENCRYPT_MODE);
		byte[] encryptedData = cipher.doFinal(src);
		return encryptedData;
	}

	@Override
	public byte[] decrypt(byte[] src) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = getCipher(m_key, Cipher.DECRYPT_MODE);
		byte decryptedData[] = cipher.doFinal(src);
		return decryptedData;
	}

	public static byte[] encrypt(byte[] src, String key)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException {
		Cipher cipher = getCipher(key, Cipher.ENCRYPT_MODE);
		byte[] encryptedData = cipher.doFinal(src);
		return encryptedData;
	}

	public static byte[] decrypt(byte[] src, String key)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException {
		Cipher cipher = getCipher(key, Cipher.DECRYPT_MODE);
		byte decryptedData[] = cipher.doFinal(src);
		return decryptedData;
	}

}