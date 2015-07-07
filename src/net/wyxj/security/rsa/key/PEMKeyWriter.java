package net.wyxj.security.rsa.key;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import net.wyxj.security.symmetric.Base64;

public class PEMKeyWriter extends AbstractRSAKeyWriter {


	public static final String PUBLIC_START = "-----BEGIN RSA PUBLIC KEY-----";
	public static final String PUBLIC_END = "-----END RSA PUBLIC KEY-----";

	public static final String PRIVATE_START = "-----BEGIN RSA PRIVATE KEY-----";
	public static final String PRIVATE_END = "-----END RSA PRIVATE KEY-----";
	
	@Override
	public byte[] writePublicKey(RSAPublicKey key) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(baos));
		writer.print(PUBLIC_START);
		writer.print("\n");
		writer.print(Base64.encode(key.getEncoded(),Base64.MODE_SSH));
		writer.print("\n");
		writer.print(PUBLIC_END);
		writer.print("\n");
		writer.flush();
		return baos.toByteArray();
	}
	
	@Override
	public byte[] writePrivateKey(RSAPrivateKey key) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(baos));
		writer.print(PRIVATE_START);
		writer.print("\n");
		writer.print(Base64.encode(key.getEncoded(),Base64.MODE_SSH));
		writer.print("\n");
		writer.print(PRIVATE_END);
		writer.print("\n");
		writer.flush();
		return baos.toByteArray();
	}

}
