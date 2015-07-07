package net.wyxj.security.symmetric;

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

public class AES extends AbstractCipherWorker {

	private String m_key;

	public AES(String password) {
		m_key = password;
	}

	/**
	 * 加密函数，将一个明文加密为密文并返回
	 * 
	 * @param encryptString
	 *            待加密的明文
	 * @param encryptKey1
	 *            加密的密钥
	 * @return 密文
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

	private static Cipher getCipher(String password, int mode) {
		byte[] iv = MD5.toByteArray(password);
		byte[] encryptKey = MD5.toByteArray(iv);
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(encryptKey, "AES");
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(mode, key, zeroIv);
			return cipher;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public byte[] encrypt(byte[] src) {
		Cipher cipher = getCipher(m_key, Cipher.ENCRYPT_MODE);
		byte[] encryptedData;
		try {
			encryptedData = cipher.doFinal(src);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			return null;
		}
		return encryptedData;
	}

	@Override
	public byte[] decrypt(byte[] src) {
		Cipher cipher = getCipher(m_key, Cipher.DECRYPT_MODE);
		byte decryptedData[];
		try {
			decryptedData = cipher.doFinal(src);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			return null;
		}
		return decryptedData;
	}

	public static byte[] encrypt(byte[] src, String password)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException {
		Cipher cipher = getCipher(password, Cipher.ENCRYPT_MODE);
		byte[] encryptedData = cipher.doFinal(src);
		return encryptedData;
	}

	public static byte[] decrypt(byte[] src, String password)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException {
		Cipher cipher = getCipher(password, Cipher.DECRYPT_MODE);
		byte decryptedData[] = cipher.doFinal(src);
		return decryptedData;
	}

}