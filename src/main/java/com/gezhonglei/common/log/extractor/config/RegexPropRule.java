package com.gezhonglei.common.log.extractor.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexPropRule extends PropRule {
	private String regex;
	private int groupIndex;
	
	private transient Pattern pattern;
	
	public RegexPropRule() {
		this.mode = ParseMode.Regex.name();
	}
	
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

}
