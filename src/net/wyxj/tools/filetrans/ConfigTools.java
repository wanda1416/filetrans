package net.wyxj.tools.filetrans;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import net.wyxj.transfer.Constant;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ConfigTools {

	private static String configPath = ".";
	
	private static String hostName = "localhost";

	public static String getHostName() {
		return hostName;
	}

	public static void setHostName(String hostName) {
		ConfigTools.hostName = hostName;
	}

	public static void setConfigPath(String path) {
		configPath = path;
	}
	
	public static String getConfigPath() {
		return configPath;
	}
	
	public static Map<String, String> readStoreInfo() {
		Gson gson = new Gson();
		String settings = null;
		try {
			byte[] data = FileUtils.readByteArray(configPath + "/"
					+ Constant.DEFAULT_CONFIGURATION_FILE);
			if (data == null) {
				return null;
			}
			settings = new String(data,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (settings != null) {
			try {
				@SuppressWarnings("unchecked")
				Map<String, String> info = gson.fromJson(settings,
						HashMap.class);
				return info;
			} catch (JsonSyntaxException e) {
				return null;
			}
		}
		return null;
	}

	public static void writeStoreInfo(Map<String, String> info) {
		Gson gson = new Gson();
		String settings = gson.toJson(info);
		try {
			FileUtils.writeToFile(configPath + "/"
					+ Constant.DEFAULT_CONFIGURATION_FILE,
					settings.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
