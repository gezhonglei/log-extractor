package com.gezhonglei.common.log.extractor;

import java.io.IOException;

import com.gezhonglei.common.log.extractor.config.ExtracteConfig;
import com.gezhonglei.common.log.extractor.entity.ParseResult;
import com.gezhonglei.common.util.FileUtil;
import com.gezhonglei.common.util.JsonUtil;

public class Main {
	
	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		ExtracteConfig config = getConfigFromFile();
		if(config != null) {
			LogExtractor parser = new LogExtractor(config);
			ParseResult result = parser.parse();
			parser.writeResult(result);
			long endTime = System.currentTimeMillis();
			System.out.println("Total Cost: " + (endTime - startTime) + "ms");
		}
		System.exit(0);
	}
	
	private static ExtracteConfig getConfigFromFile() {
		String file = Main.class.getClassLoader().getResource("config.json").getFile();
		try {
			String json = FileUtil.getAllContent(file, "UTF-8");
			return JsonUtil.fromJson(json, ExtracteConfig.class);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
