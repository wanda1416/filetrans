package net.wyxj.security.rsa.key;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class DERKeyWriter extends AbstractRSAKeyWriter {

	@Override
	public byte[] writePublicKey(RSAPublicKey key) {
		return key.getEncoded();
	}

	@Override
	public byte[] writePrivateKey(RSAPrivateKey key) {
		return key.getEncoded();
	}

}
