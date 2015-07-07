package net.wyxj.security.rsa.key;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import net.wyxj.security.rsa.RSAUtils;
import net.wyxj.security.symmetric.Base64;

public class PEMKeyReader extends AbstractRSAKeyReader {

	public static final String PUBLIC_START = "-----BEGIN RSA PUBLIC KEY-----";
	public static final String PUBLIC_END = "-----END RSA PUBLIC KEY-----";

	public static final String PRIVATE_START = "-----BEGIN RSA PRIVATE KEY-----";
	public static final String PRIVATE_END = "-----END RSA PRIVATE KEY-----";

	@Override
	public RSAPublicKey readPublicKey(byte[] src) {
		String content = new String(src);
		String[] arr = content.split("\n");
		if (arr == null || arr.length <= 2) {
			return null;
		}
		if (!arr[0].equals(PUBLIC_START)
				|| !arr[arr.length - 1].equals(PUBLIC_END)) {
			return null;
		}
		StringBuilder base = new StringBuilder();
		for (int i = 1; i < arr.length - 1; i++) {
			base.append(arr[i]);
		}
		byte[] data = Base64.decode(base.toString());
		RSAPublicKey key = RSAUtils.generatePublicKey(data);
		return key;
	}

	@Override
	public RSAPrivateKey readPrivateKey(byte[] src) {
		String content = new String(src);
		String[] arr = content.split("\n");
		if (arr.length <= 2) {
			return null;
		}
		if (!arr[0].equals(PRIVATE_START)
				|| !arr[arr.length - 1].equals(PRIVATE_END)) {
			return null;
		}
		StringBuilder base = new StringBuilder();
		for (int i = 1; i < arr.length - 1; i++) {
			base.append(arr[i]);
		}
		byte[] data = Base64.decode(base.toString());
		RSAPrivateKey key = RSAUtils.generatePrivateKey(data);
		return key;
	}

}
