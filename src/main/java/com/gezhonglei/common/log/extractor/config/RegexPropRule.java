package com.gezhonglei.common.log.extractor.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexPropRule implements IPropRule {
	private String name;
	private String type;
	private String regex;
	private int groupIndex;
	
	private transient Pattern pattern;
	
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
		this.pattern = Pattern.compile(regex);
	}
	public int getGroupIndex() {
		return groupIndex;
	}
	public void setGroupIndex(int groupIndex) {
		this.groupIndex = groupIndex;
	}
	
	@Override
	public ParseMode mode() {
		return ParseMode.Regex;
	}
	
	@Override
	public String extractFrom(String text) {
		if(pattern != null) {
			Matcher matcher = pattern.matcher(text);
			if(matcher.find()) {
				return this.groupIndex > 0 && this.groupIndex <= matcher.groupCount() ? 
						matcher.group(this.groupIndex) : matcher.group();
			}
		}
		return null;
	}
	@Override
	public String getType() {
		return this.type;
	}
	@Override
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
