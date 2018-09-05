package com.gezhonglei.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtil {

	public FileUtil() {
	}

	public static String getAllContent(File file, String encode) throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			return getAllContent(fis, encode);
		}
		finally {
			if(null != fis) {
				fis.close();
			}
		}
	}
	
	public static String getAllContent(String filename, String encode) throws IOException {
		return getAllContent(new File(filename), encode);
	}

	public static String getAllContent(InputStream stream, String encode) throws IOException {
		StringBuilder content = new StringBuilder();
		InputStreamReader isReader = null;
		BufferedReader bufReader = null;
		try {
			isReader = new InputStreamReader(stream, encode);
			bufReader = new BufferedReader(isReader);

			while (true) {
				String nextline = bufReader.readLine();
				if (nextline == null) {
					break;
				}
				content.append(nextline).append(System.lineSeparator());
			}
			bufReader.close();
		}
		finally {
			if(bufReader != null) {
				bufReader.close();
			}
			if(isReader != null) {
				isReader.close();
			}
		}
		return content.toString();
	}
}
