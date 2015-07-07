package net.wyxj.security.rsa.key;

import java.io.IOException;
import java.io.OutputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import net.wyxj.security.rsa.FileUtils;

public abstract class AbstractRSAKeyWriter implements IRSAKeyWriter {

	@Override
	public void writePublicKey(String filename, RSAPublicKey key)
			throws IOException{
		FileUtils.writeToFile(filename, writePublicKey(key));
	}

	@Override
	public void writePrivateKey(String filename, RSAPrivateKey key)
			throws IOException{
		FileUtils.writeToFile(filename, writePrivateKey(key));
	}

	@Override
	public void writePublicKey(OutputStream output, RSAPublicKey key)
			throws IOException{
		output.write(writePublicKey(key));
	}

	@Override
	public void writePrivateKey(OutputStream output, RSAPrivateKey key)
			throws IOException {
		output.write(writePrivateKey(key));
	}

}
