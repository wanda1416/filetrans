package net.wyxj.security.rsa.key;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import net.wyxj.security.rsa.RSAUtils;

public class DERKeyReader extends AbstractRSAKeyReader {

	@Override
	public RSAPublicKey readPublicKey(byte[] src) {
		return RSAUtils.generatePublicKey(src);
	}

	@Override
	public RSAPrivateKey readPrivateKey(byte[] src) {
		return RSAUtils.generatePrivateKey(src);
	}

}
