package net.wyxj.security.rsa.key;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import net.wyxj.security.symmetric.MD5;

public class BRSKeyWriter extends AbstractRSAKeyWriter{

	@Override
	public byte[] writePublicKey(RSAPublicKey key) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		BigInteger modulus = key.getModulus();
		BigInteger publicExponent = key.getPublicExponent();
		try {
			dos.writeShort(modulus.toByteArray().length);
			dos.writeShort(publicExponent.toByteArray().length);
			dos.write(modulus.toByteArray());
			dos.write(publicExponent.toByteArray());
			byte[] data = baos.toByteArray();
			byte[] check = MD5.toByteArray(data);
			baos.reset();
			dos.writeInt(check.length + data.length);
			dos.write(check);
			dos.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return baos.toByteArray();
	}

	@Override
	public byte[] writePrivateKey(RSAPrivateKey key) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		BigInteger modulus = key.getModulus();
		BigInteger privateExponent = key.getPrivateExponent();
		try {
			dos.writeShort(modulus.toByteArray().length);
			dos.writeShort(privateExponent.toByteArray().length);
			dos.write(modulus.toByteArray());
			dos.write(privateExponent.toByteArray());
			byte[] data = baos.toByteArray();
			byte[] check = MD5.toByteArray(data);
			baos.reset();
			dos.writeInt(check.length + data.length);
			dos.write(check);
			dos.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return baos.toByteArray();
	}

}
