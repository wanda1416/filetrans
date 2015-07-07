package net.wyxj.security.rsa.key;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import net.wyxj.security.symmetric.Base64;

public class SSHKeyWriter extends AbstractRSAKeyWriter {

	public static final String START = "rsa-ssh";
	public static final String END = "rsa-end";
	
	@Override
	public byte[] writePublicKey(RSAPublicKey key) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(baos));
		writer.print(START);
		writer.print(" ");
		writer.print(Base64.encode(key.getEncoded(),Base64.MODE_SSH));
		writer.print(" ");
		writer.print(END);
		writer.flush();
		return baos.toByteArray();
	}

	@Override
	public byte[] writePrivateKey(RSAPrivateKey key) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(baos));
		writer.print(START);
		writer.print(" ");
		writer.print(Base64.encode(key.getEncoded(),Base64.MODE_SSH));
		writer.print(" ");
		writer.print(END);
		writer.flush();
		return baos.toByteArray();
	}

}
