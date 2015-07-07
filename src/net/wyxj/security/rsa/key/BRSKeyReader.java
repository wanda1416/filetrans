package net.wyxj.security.rsa.key;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import net.wyxj.security.rsa.RSAUtils;
import net.wyxj.security.symmetric.MD5;

public class BRSKeyReader extends AbstractRSAKeyReader {

	@Override
	public RSAPublicKey readPublicKey(byte[] src) {
		if (src == null || src.length <= 21) {
			return null;
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(src);
		DataInputStream dis = new DataInputStream(bais);
		try {
			int total = dis.readInt();
			byte[] check = new byte[16];
			dis.read(check);
			int lenMod = dis.readShort();
			int lenPub = dis.readShort();
			byte[] mod = new byte[lenMod];
			byte[] pub = new byte[lenPub];
			// 读取两个素数的数据
			dis.readFully(mod);
			dis.readFully(pub);
			// 检查 total 是否相等
			if (total != lenMod + lenPub + 20) {
				return null;
			}
			// 校验 data的MD5 是否与check相等
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeShort(lenMod);
			dos.writeShort(lenPub);
			dos.write(mod);
			dos.write(pub);
			byte[] check2 = MD5.toByteArray(baos.toByteArray());
			if (RSAUtils.equals(check, check2) == false) {
				return null;
			}
			BigInteger modulus = new BigInteger(mod);
			BigInteger publicExponent = new BigInteger(pub);
			return RSAUtils.generatePublicKey(modulus, publicExponent);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public RSAPrivateKey readPrivateKey(byte[] src) {
		if (src == null || src.length <= 21) {
			return null;
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(src);
		DataInputStream dis = new DataInputStream(bais);
		try {
			int total = dis.readInt();
			byte[] check = new byte[16];
			dis.read(check);
			int lenMod = dis.readShort();
			int lenPub = dis.readShort();
			byte[] mod = new byte[lenMod];
			byte[] pri = new byte[lenPub];
			// 读取两个素数的数据
			dis.readFully(mod);
			dis.readFully(pri);
			// 检查 total 是否相等
			if (total != lenMod + lenPub + 20) {
				return null;
			}
			// 校验 data的MD5 是否与check相等
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeShort(lenMod);
			dos.writeShort(lenPub);
			dos.write(mod);
			dos.write(pri);
			byte[] check2 = MD5.toByteArray(baos.toByteArray());
			if (RSAUtils.equals(check, check2) == false) {
				return null;
			}
			BigInteger modulus = new BigInteger(mod);
			BigInteger privateExponent = new BigInteger(pri);
			return RSAUtils.generatePrivateKey(modulus, privateExponent);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
