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
	 * 文件ID，随机生成，用于标识文件
	 */
	private int fileID;
	/**
	 * 文件类型
	 */
	private int fileType;
	/**
	 * 文件名
	 */
	private String fileName;
	/**
	 * 文件长度
	 */
	private long fileLength;
	/**
	 * 文件校验码
	 */
	private int checkType;
	/**
	 * 文件校验码
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
		// 生成一个随机的文件ID
		setFileID(Math.abs(random.nextInt()));
		// 设置文件名
		setFileName(file.getName());
		// 设置校验类型
		setCheckType(checkType);
		// 填充一个无效的校验码
		setCheckCode(INVAILD_CHECK);
		// 下面开始识别文件类型等信息，并尝试填充文件长度
		if (!file.exists()){
			// 文件不存在
			setFileType(TYPE_INVAILD);
		}else if( file.isDirectory() ) {
			// 目录, 目前不支持
			setFileType(TYPE_DIR);
		} else if( !file.canRead()){
			// 文件不可读
			setFileType(TYPE_INVAILD);			
		} else {
			setFileType(TYPE_FILE);
			setFileLength(file.length());
		}
		// 下面开始计算文件校验码
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
