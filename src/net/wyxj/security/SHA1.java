package net.wyxj.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1 {
	
	public static final int CACHE_SIZE = 4096 * 10; 
	
	/**
	 * �����ļ���SHA1ֵ������40�ֽڳ����ַ���
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public static String toString(File file) throws IOException{
		return CommonUtils.byteArrayToHexString(toByteArray(file));
	}
	
	/**
	 * �����ļ���SHA1ֵ������20�ֽڳ���byte����
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] toByteArray(File file) throws IOException{
		FileInputStream fis = new FileInputStream(file);
		int length = -1;
		byte[] cache = new byte[CACHE_SIZE];
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		while((length=fis.read(cache))!=-1){
			md.update(cache, 0, length);
		}
		fis.close();
		return md.digest();
	}
	
	/**
	 * ���ַ�����SHA1����Ϊ40�ֽڳ���ʮ�������ַ���
	 * 
	 * @param origin
	 *            �����ܵ��ַ���
	 * @return 40�ֽڵ�16�����ַ�������
	 * @throws NoSuchAlgorithmException 
	 */
	public static String toString(String origin) {
		return toString(origin.getBytes());
	}
	
	/**
	 * ���ַ�����SHA1����Ϊ40�ֽڳ���ʮ�������ַ���
	 * 
	 * @param origin
	 *            �����ܵ��ַ���
	 * @return 40�ֽڵ�16�����ַ�������
	 * @throws NoSuchAlgorithmException 
	 */
	public static String toString(byte[] origin){
		return CommonUtils.byteArrayToHexString(toByteArray(origin));
	}

	/**
	 * ���ַ�����SHA1����Ϊ20�ֽڳ��Ķ���������
	 * 
	 * @param origin
	 *            �����ܵ��ַ���
	 * @return 20�ֽڵ��ֽ����飬��160λ��
	 * @throws NoSuchAlgorithmException 
	 */
	public static byte[] toByteArray(String origin){
		return toByteArray(origin.getBytes());
	}
	
	/**
	 * ���ַ�����SHA1����Ϊ20�ֽڳ��Ķ���������
	 * 
	 * @param origin
	 *            �����ܵ��ַ���
	 * @return 20�ֽڵ��ֽ����飬��160λ��
	 * @throws NoSuchAlgorithmException 
	 */
	public static byte[] toByteArray(byte[] origin){
		byte[] result = null;
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		result = md.digest(origin);
		return result;
	}	
	
}