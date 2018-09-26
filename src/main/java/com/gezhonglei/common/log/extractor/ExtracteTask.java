package com.gezhonglei.common.log.extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gezhonglei.common.log.extractor.config.EntityRule;
import com.gezhonglei.common.log.extractor.config.LogSource;
import com.gezhonglei.common.log.extractor.config.PropRule;
import com.gezhonglei.common.log.extractor.entity.Entity;
import com.gezhonglei.common.log.extractor.entity.Result;
import com.gezhonglei.common.util.StringUtil;

public class ExtracteTask implements Runnable {

	private Logger logger = LoggerFactory.getLogger(ExtracteTask.class);

	private File file;
	private LogSource source;
	private LogExtractor parser;
	
	private Result result;
	
	public ExtracteTask(File file, LogSource source, LogExtractor parser) {
		this.file = file;
		this.source = source;
		this.parser = parser;
	}

	@Override
	public void run() {
		long beginTime = System.currentTimeMillis();
		logger.debug("begin parse: {}", file);
		int status = 0;
		String message = null;
		List<Entity> values = new ArrayList<>();
		try{
			FileInputStream fis = null;
			InputStreamReader isReader = null;
			BufferedReader bufReader = null;
			try {
				fis = new FileInputStream(file);
				isReader = new InputStreamReader(fis, source.getEncode());
				bufReader = new BufferedReader(isReader, source.getBufferSize()*1024);

				while (true) {
					String nextline = bufReader.readLine();
					if (nextline == null) {
						break;
					}
					values.addAll(parse(nextline));
				}
				bufReader.close();
				isReader.close();
			}
			finally {
				if(bufReader != null) {
					bufReader.close();
				}
				if(isReader != null) {
					isReader.close();
				}
				if(fis != null) {
					fis.close();
				}
			}
		} catch(Exception ex) {
			status = 1; // 异常
			message = ex.getMessage();
			logger.error("ParseTask error:{}", ex.getMessage());
		}
		finally {
			long endTime = System.currentTimeMillis();
			logger.debug("end parse: {}, size={},cost={}", file, values.size(), endTime - beginTime);
			result = new Result(status, values, message);
			parser.notify(this);
		}
	}

	private List<Entity> parse(String lineText) {
		List<Entity> values = new ArrayList<>();
		if(StringUtil.isEmpty(lineText)) return values;
		
		for (EntityRule rule : this.source.getRules()) {
			if(rule.matched(lineText)) {
				Entity entity = new Entity();
				//entity.setSource(lineText);
				entity.setRuleName(rule.getName());
				
				Object value;
				Map<String, PropRule> allPropRule = source.getAllPropRule(rule.getName());
				for (PropRule propRule : allPropRule.values()) {
					value = propRule.extractFrom(lineText);
					entity.setPropValue(propRule.getName(), value);
				}
				values.add(entity);
			}
		}
		return values;
	}
	
	public File getFile() {
		return file;
	}

	public Result getResult() {
		return result;
	}

}
