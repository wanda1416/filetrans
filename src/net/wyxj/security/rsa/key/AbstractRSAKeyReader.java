package net.wyxj.security.rsa.key;

import java.io.IOException;
import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import net.wyxj.security.rsa.FileUtils;

public abstract class AbstractRSAKeyReader implements IRSAKeyReader {

	@Override
	public RSAPublicKey readPublicKey(String filename) throws IOException {
		return readPublicKey(FileUtils.readByteArray(filename));
	}

	@Override
	public RSAPrivateKey readPrivateKey(String filename) throws IOException {
		return readPrivateKey(FileUtils.readByteArray(filename));
	}

	@Override
	public RSAPublicKey readPublicKey(InputStream input) throws IOException {
		return readPublicKey(FileUtils.readByteArray(input));
	}

	@Override
	public RSAPrivateKey readPrivateKey(InputStream input) throws IOException {
		return readPrivateKey(FileUtils.readByteArray(input));
	}

}
