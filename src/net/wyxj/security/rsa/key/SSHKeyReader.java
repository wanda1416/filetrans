package net.wyxj.security.rsa.key;

import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

import net.wyxj.security.rsa.RSAUtils;
import net.wyxj.security.symmetric.Base64;

public class SSHKeyReader extends AbstractRSAKeyReader {

	public static int decodeUInt32(byte[] key, int start_index) {
		byte[] test = Arrays.copyOfRange(key, start_index, start_index + 4);
		return new BigInteger(test).intValue();
	}

	@Override
	public RSAPublicKey readPublicKey(byte[] src) {
		String content = new String(src);
		String[] arr = content.split(" ");
		if (arr == null || arr.length != 3) {
			return null;
		}
		byte[] data = Base64.decode(arr[1]);
		RSAPublicKey key = RSAUtils.generatePublicKey(data);
		return key;
	}

	@Override
	public RSAPrivateKey readPrivateKey(byte[] src) {
		String content = new String(src);
		String[] arr = content.split(" ");
		if (arr == null || arr.length != 3) {
			return null;
		}
		byte[] data = Base64.decode(arr[1]);
		RSAPrivateKey key = RSAUtils.generatePrivateKey(data);
		return key;
	}

}
