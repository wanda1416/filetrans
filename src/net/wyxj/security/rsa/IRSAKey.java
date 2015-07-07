package net.wyxj.security.rsa;

import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * RSA Key 的公共接口，用于定义返回公钥、私钥或者模数、指数的方法
 * @author 辉
 *
 */
public interface IRSAKey {
	
	/**
	 * 判断是否为公钥
	 * @return
	 */
	public boolean isPublicKey();
	
	/**
	 * 判断是否为私钥
	 * @return
	 */
	public boolean isPrivaryKey();
	
	/**
	 * 返回 RSA Key 的模数
	 * @return
	 */
	public BigInteger getModulus();
	
	/**
	 * 返回 RSA Key 的指数
	 * @return
	 */
	public BigInteger getExponent();	
	
	/**
	 * 获取公钥，没有返回null
	 * @return
	 */
	public RSAPublicKey getPublicKey();
	
	/**
	 * 获取私钥，没有返回null
	 * @return
	 */
	public RSAPrivateKey getPrivateKey();
	

}
