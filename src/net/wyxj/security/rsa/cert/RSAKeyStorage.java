package net.wyxj.security.rsa.cert;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * RSA√‹‘øø‚
 * 
 * @author wanda1416
 * 
 */
public class RSAKeyStorage {

	private KeyStore m_keystore;
	private String m_filename;
	private String m_password;

	private RSAKeyStorage(KeyStore keystore, String filename, String password) {
		this.m_filename = filename;
		this.m_password = password;
		this.m_keystore = keystore;
	}

	public void save() {
		try {
			save(m_filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void save(String filename) throws IOException {
		StoreUtils.saveKeyStoreToFile(filename, m_keystore, m_password);
	}

	public void close() {
		m_keystore = null;
	}

	public static RSAKeyStorage open(String filename, String password) {
		KeyStore keystore = StoreUtils.loadKeyStoreFromFile(filename, password);
		return new RSAKeyStorage(keystore, filename, password);
	}

	public static RSAKeyStorage create(String filename, String password) {
		KeyStore keystore = StoreUtils.createNewKeyStore();
		return new RSAKeyStorage(keystore, filename, password);
	}

	public List<String> lists() {
		List<String> list = new ArrayList<String>();
		Enumeration<String> e;
		try {
			e = m_keystore.aliases();
		} catch (KeyStoreException e1) {
			return list;
		}
		while (e.hasMoreElements()) {
			list.add(e.nextElement());
		}
		return list;
	}

	public RSAPrivateKey getPrivateKey(String keyname, String keypass) {
		KeyStore.PrivateKeyEntry pkEntry;
		try {
			pkEntry = (KeyStore.PrivateKeyEntry) m_keystore.getEntry(keyname,
					new KeyStore.PasswordProtection(keypass.toCharArray()));
		} catch (NoSuchAlgorithmException | UnrecoverableEntryException
				| KeyStoreException e) {
			return null;
		}
		return (RSAPrivateKey) pkEntry.getPrivateKey();
	}

	public Certificate getCertificate(String keyname, String keypass) {
		KeyStore.PrivateKeyEntry pkEntry;
		try {
			pkEntry = (KeyStore.PrivateKeyEntry) m_keystore.getEntry(keyname,
					new KeyStore.PasswordProtection(keypass.toCharArray()));
		} catch (NoSuchAlgorithmException | UnrecoverableEntryException
				| KeyStoreException e) {
			return null;
		}
		return pkEntry.getCertificate();
	}

	public RSAPublicKey getPublicKey(String keyname, String keypass) {
		KeyStore.PrivateKeyEntry pkEntry;
		try {
			pkEntry = (KeyStore.PrivateKeyEntry) m_keystore.getEntry(keyname,
					new KeyStore.PasswordProtection(keypass.toCharArray()));
		} catch (NoSuchAlgorithmException | UnrecoverableEntryException
				| KeyStoreException e) {
			return null;
		}
		return (RSAPublicKey) pkEntry.getCertificate().getPublicKey();
	}

}
