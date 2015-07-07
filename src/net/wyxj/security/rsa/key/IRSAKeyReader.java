package net.wyxj.security.rsa.key;

import java.io.IOException;
import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public interface IRSAKeyReader {

	public RSAPublicKey readPublicKey(String filename) throws IOException;

	public RSAPrivateKey readPrivateKey(String filename) throws IOException;

	public RSAPublicKey readPublicKey(InputStream input) throws IOException;

	public RSAPrivateKey readPrivateKey(InputStream input) throws IOException;

	public RSAPublicKey readPublicKey(byte[] src);

	public RSAPrivateKey readPrivateKey(byte[] src);

}
