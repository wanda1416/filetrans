package net.wyxj.transfer;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.wyxj.security.CRC32;
import net.wyxj.security.MD5;
import net.wyxj.security.SHA1;

public class FileHeader {

	public static final int TYPE_INVAILD = 0;
	public static final int TYPE_FILE = 1;
	public static final int TYPE_DIR = 2;
	
	public static final String INVAILD_CHECK = "null";
	
	public static final int CHECK_NO = 0;
	public static final int CHECK_MD5 = 1;
	public static final int CHECK_SHA1 = 2;
	public static final int CHECK_CRC16 = 3;
	public static final int CHECK_CRC32 = 4;
	
	public static Random random = new Random();
	public static Gson gson = new Gson();
	
	/**
	 * �ļ�ID��������ɣ����ڱ�ʶ�ļ�
	 */
	private int fileID;
	/**
	 * �ļ�����
	 */
	private int fileType;
	/**
	 * �ļ���
	 */
	private String fileName;
	/**
	 * �ļ�����
	 */
	private long fileLength;
	/**
	 * �ļ�У����
	 */
	private int checkType;
	/**
	 * �ļ�У����
	 */
	private String checkCode;
	
	public FileHeader(File file,int checkType) {
		this.initialize(file,checkType);
	}
	
	public FileHeader(File file) {
		this.initialize(file,Constant.DEFAULT_FILE_CHECK);
	}

	public FileHeader(String filePath,int checkType) {
		this.initialize(new File(filePath),checkType);
	}
	
	public FileHeader(String filePath) {
		this.initialize(new File(filePath),Constant.DEFAULT_FILE_CHECK);
	}
	
	private void initialize(File file,int checkType){
		// ����һ��������ļ�ID
		setFileID(Math.abs(random.nextInt()));
		// �����ļ���
		setFileName(file.getName());
		// ����У������
		setCheckType(checkType);
		// ���һ����Ч��У����
		setCheckCode(INVAILD_CHECK);
		// ���濪ʼʶ���ļ����͵���Ϣ������������ļ�����
		if (!file.exists()){
			// �ļ�������
			setFileType(TYPE_INVAILD);
		}else if( file.isDirectory() ) {
			// Ŀ¼, Ŀǰ��֧��
			setFileType(TYPE_DIR);
		} else if( !file.canRead()){
			// �ļ����ɶ�
			setFileType(TYPE_INVAILD);			
		} else {
			setFileType(TYPE_FILE);
			setFileLength(file.length());
		}
		// ���濪ʼ�����ļ�У����
		if (getFileType() == TYPE_FILE && getCheckType() != CHECK_NO) {
			switch(getCheckType()){
			case CHECK_MD5:
				try {
					String md5 = MD5.toString(file);
					setCheckCode(md5);
				} catch (IOException e) {
					e.printStackTrace(MyLogger.logger);
				}
				break;
			case CHECK_SHA1:
				try {
					String sha1 = SHA1.toString(file);
					setCheckCode(sha1);
				} catch (IOException e) {
					e.printStackTrace(MyLogger.logger);
				}
				break;				
			case CHECK_CRC16:
				try {
					String crc32 = CRC32.toString(file);
					setCheckCode(crc32);					
				} catch (IOException e) {
					e.printStackTrace(MyLogger.logger);
				}					
				break;				
			case CHECK_CRC32:
				try {
					String crc32 = CRC32.toString(file);
					setCheckCode(crc32);					
				} catch (IOException e) {
					e.printStackTrace(MyLogger.logger);
				}				
				break;
			default:
				break;			
			}
		}
	}

	public int getFileID() {
		return fileID;
	}

	public void setFileID(int fileID) {
		this.fileID = fileID;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getFileLength() {
		return fileLength;
	}

	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}

	public String getCheckCode() {
		return checkCode;
	}

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	public int getFileType() {
		return fileType;
	}

	public void setFileType(int fileType) {
		this.fileType = fileType;
	}

	public int getCheckType() {
		return checkType;
	}

	public void setCheckType(int checkType) {
		this.checkType = checkType;
	}
	
	public String toString(){
		return gson.toJson(this);
	}

	public static FileHeader fromString(String json) {
		FileHeader header = null;
		try {
			header = gson.fromJson(json, FileHeader.class);
		} catch (JsonSyntaxException e) {
			return null;
		}
		return header;
	}

}
