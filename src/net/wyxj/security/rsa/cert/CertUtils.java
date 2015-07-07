package net.wyxj.security.rsa.cert;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

public class CertUtils {
		
	public static Certificate readPublicKey(String filename) throws FileNotFoundException{
		CertificateFactory factory;
		try {
			factory = CertificateFactory.getInstance("X.509");
		} catch (CertificateException e) {
			return null;
		}
		FileInputStream fis = new FileInputStream(filename);
		Certificate cert;
		try {
			cert = factory.generateCertificate(fis);
		} catch (CertificateException e) {
			return null;
		}
		return cert;
	}
	
}
