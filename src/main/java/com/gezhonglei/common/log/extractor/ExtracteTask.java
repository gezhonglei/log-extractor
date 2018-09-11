package com.gezhonglei.common.log.extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
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
			result = new Result(status, values, message);
			parser.notify(this);
		}
	}

	private List<Entity> parse(String lineText) {
		List<Entity> values = new ArrayList<>();
		if(StringUtil.isEmpty(lineText)) return values;
		
		boolean matched = false;
		for (EntityRule rule : this.config.getRules()) {
			matched = false;
			if(StringUtil.isEmpty(rule.getMatchText())) continue;
			
			if(rule.isUseRegex()) {
				Pattern pattern = rule.getPattern();
				Matcher matcher = pattern.matcher(lineText);
				matched = matcher.find();
			} else {
				String findStr = rule.getMatchText();
				if(rule.isIgnoreCase()) {
					matched = lineText.toUpperCase(Locale.US).contains(findStr.toUpperCase(Locale.US));
				} else {
					matched = lineText.contains(findStr);
				}
			}
			
			if(matched) {
				Entity entity = new Entity();
				//entity.setSource(lineText);
				entity.setRuleName(rule.getName());
				
				String value;
				Map<String, Object> commonProps = new HashMap<>();
				for (PropRule commonProp : config.getCommonPropRules()) {
					value = commonProp.extractFrom(lineText);
					commonProps.put(commonProp.getName(), value);
				}
				entity.setCommonProps(commonProps);
				
				Map<String, Object> props = new HashMap<>();
				for (PropRule propRule : rule.getPropRules()) {
					value = propRule.extractFrom(lineText);
					props.put(propRule.getName(), value);
				}
				entity.setProps(props);
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
