package net.wyxj.security.rsa.key;

import java.io.IOException;
import java.io.OutputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public interface IRSAKeyWriter {

	public void writePublicKey(String filename, RSAPublicKey key)
			throws IOException;

	public void writePrivateKey(String filename, RSAPrivateKey key)
			throws IOException;

	public void writePublicKey(OutputStream output, RSAPublicKey key)
			throws IOException;

	public void writePrivateKey(OutputStream output, RSAPrivateKey key)
			throws IOException;

	public byte[] writePublicKey(RSAPublicKey key);

	public byte[] writePrivateKey(RSAPrivateKey key);

}
