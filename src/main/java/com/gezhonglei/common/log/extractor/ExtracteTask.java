package com.gezhonglei.common.log.extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gezhonglei.common.log.extractor.config.EntityRule;
import com.gezhonglei.common.log.extractor.config.PropRule;
import com.gezhonglei.common.log.extractor.config.ExtracteConfig;
import com.gezhonglei.common.log.extractor.entity.Entity;
import com.gezhonglei.common.log.extractor.entity.Result;
import com.gezhonglei.common.util.ArrayUtil;
import com.gezhonglei.common.util.StringUtil;

public class ExtracteTask implements Runnable {

	private Logger logger = LoggerFactory.getLogger(ExtracteTask.class);

	private File file;
	private ExtracteConfig config;
	private LogExtractor parser;
	
	private Result result;
	
	public ExtracteTask(File file, ExtracteConfig config, LogExtractor parser) {
		this.file = file;
		this.config = config;
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
				isReader = new InputStreamReader(fis, config.getEncode());
				bufReader = new BufferedReader(isReader, config.getBufferSize()*1024);

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
		
		boolean matched = false, matchedTextMode = false;
		int matchedCount = 0;
		for (EntityRule rule : this.config.getRules()) {
			matched = false;
			matchedTextMode = !StringUtil.isEmpty(rule.getMatchText());
			if(!matchedTextMode && ArrayUtil.isEmpty(rule.getMatchTexts())) {
				continue;
			}
			
			if(rule.isUseRegex()) {
				Pattern pattern = rule.getPattern();
				Matcher matcher = pattern.matcher(lineText);
				matched = matcher.find();
			} else {
				matchedCount = 0;
				List<String> matchedTexts = matchedTextMode ? Arrays.asList(rule.getMatchText()) :
					Arrays.asList(rule.getMatchTexts());
				if(rule.isIgnoreCase()) {
					lineText = lineText.toUpperCase(Locale.US);
				}
				for (String findStr : matchedTexts) {
					findStr = rule.isIgnoreCase() ? findStr.toUpperCase(Locale.US) : findStr;
					if(lineText.contains(findStr)) {
						matchedCount++;
					}
				}
				matched = matchedCount == matchedTexts.size();
			}
			
			if(matched) {
				Entity entity = new Entity();
				//entity.setSource(lineText);
				entity.setRuleName(rule.getName());
				
				String value;
				Map<String, PropRule> allPropRule = config.getAllPropRule(rule.getName());
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
