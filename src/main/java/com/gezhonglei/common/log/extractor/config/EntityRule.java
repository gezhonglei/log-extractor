package com.gezhonglei.common.log.extractor.config;

import java.util.Map;
import java.util.regex.Pattern;

public class EntityRule {
	private String name;
	private String matchText;
	private boolean useRegex;
	private boolean ignoreCase;
	private Map<String, IPropRule> propRules;
	
	private transient Pattern pattern;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMatchText() {
		return matchText;
	}
	
	public Pattern getPattern() {
//		if(this.useRegex) {
//			if(pattern == null) {
//				pattern = Pattern.compile(matchText);
//			}
//		} else {
//			pattern = null;
//		}
		return !this.useRegex ? null : pattern == null ? Pattern.compile(matchText) : pattern;
	}
	
	public void setMatchText(String matchText) {
		this.matchText = matchText;
		if(this.useRegex) {
			this.pattern = Pattern.compile(matchText);
		}
	}
	public boolean isUseRegex() {
		return useRegex;
	}
	public void setUseRegex(boolean useRegex) {
		this.useRegex = useRegex;
	}
	public boolean isIgnoreCase() {
		return ignoreCase;
	}
	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}
	public Map<String, IPropRule> getPropRules() {
		return propRules;
	}
	public void setPropRules(Map<String, IPropRule> props) {
		this.propRules = props;
	}
	
	
}
