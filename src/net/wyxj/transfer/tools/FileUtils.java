package net.wyxj.transfer.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

	public static void writeToFile(String filename, byte[] data)
			throws IOException {
		FileOutputStream fos = new FileOutputStream(filename);
		fos.write(data);
		fos.close();
	}

	public static byte[] readByteArray(String filename) throws IOException {
		File file = new File(filename);
		if (file.exists() == false || file.isDirectory() == true) {
			return null;
		}
		FileInputStream fis = new FileInputStream(file);
		byte[] data = readByteArray(fis);
		fis.close();
		return data;
	}

	public static byte[] readByteArray(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] data = new byte[4096];
		int len = -1;
		while ((len = is.read(data)) != -1) {
			baos.write(data, 0, len);
		}
		return baos.toByteArray();
	}

}