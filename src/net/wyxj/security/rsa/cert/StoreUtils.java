package net.wyxj.security.rsa.cert;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class StoreUtils {

	public static KeyStore createNewKeyStore() {
		KeyStore keystore;
		try {
			keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		} catch (KeyStoreException e) {
			return null;
		}
		try {
			keystore.load(null);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return keystore;
	}

	public static KeyStore loadKeyStoreFromFile(String filename, String password) {
		KeyStore keystore;
		try {
			keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		} catch (KeyStoreException e) {
			return null;
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filename);
			keystore.load(fis, password.toCharArray());
		} catch (FileNotFoundException e) {
			return null;
		} catch (NoSuchAlgorithmException e) {
			return null;
		} catch (CertificateException e) {
			return null;
		} catch (IOException e) {
			return null;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return keystore;
	}

	public static void saveKeyStoreToFile(String filename, KeyStore keystore,
			String password) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filename);
			keystore.store(fos, password.toCharArray());
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
