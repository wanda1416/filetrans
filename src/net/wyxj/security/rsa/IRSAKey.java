package net.wyxj.security.rsa;

import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * RSA Key �Ĺ����ӿڣ����ڶ��巵�ع�Կ��˽Կ����ģ����ָ���ķ���
 * @author ��
 *
 */
public interface IRSAKey {
	
	/**
	 * �ж��Ƿ�Ϊ��Կ
	 * @return
	 */
	public boolean isPublicKey();
	
	/**
	 * �ж��Ƿ�Ϊ˽Կ
	 * @return
	 */
	public boolean isPrivaryKey();
	
	/**
	 * ���� RSA Key ��ģ��
	 * @return
	 */
	public BigInteger getModulus();
	
	/**
	 * ���� RSA Key ��ָ��
	 * @return
	 */
	public BigInteger getExponent();	
	
	/**
	 * ��ȡ��Կ��û�з���null
	 * @return
	 */
	public RSAPublicKey getPublicKey();
	
	/**
	 * ��ȡ˽Կ��û�з���null
	 * @return
	 */
	public RSAPrivateKey getPrivateKey();
	

}
