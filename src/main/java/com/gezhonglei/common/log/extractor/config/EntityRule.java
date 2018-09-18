package com.gezhonglei.common.log.extractor.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class EntityRule {
	private String name;
	private String matchText;
	private String[] matchTexts;
	private boolean useRegex;
	private boolean ignoreCase;
	@JsonProperty("props")
	private List<PropRule> propRules;
	
	@JsonIgnore
	private Pattern pattern;
	
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
		return !this.useRegex ? null : pattern == null ? (pattern=Pattern.compile(matchText)) : pattern;
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
	public List<PropRule> getPropRules() {
		return Optional.ofNullable(propRules).orElseGet(ArrayList::new);
	}
	public void setPropRules(List<PropRule> props) {
		this.propRules = props;
	}
	public String[] getMatchTexts() {
		return matchTexts;
	}
	public void setMatchTexts(String[] matchTexts) {
		this.matchTexts = matchTexts;
	}
}
