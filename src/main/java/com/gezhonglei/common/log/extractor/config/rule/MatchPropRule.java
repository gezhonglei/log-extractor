package com.gezhonglei.common.log.extractor.config.rule;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.gezhonglei.common.log.extractor.config.PropRule;
import com.gezhonglei.common.util.ArrayUtil;
import com.gezhonglei.common.util.StringUtil;

public class MatchPropRule extends PropRule {
	private Object value;
	private String matchText;
	private String[] matchTexts;
	private boolean useRegex = false;
	private boolean ignoreCase = false;

	@JsonIgnore
	private Pattern pattern = null;
	@JsonIgnore
	private String matchStr = null;
	private Pattern getPattern() {
		// create pattern instance while initial or matchText changed
		if(pattern == null || (matchText!= null && matchText.equals(matchStr))) {
			pattern = Pattern.compile(this.matchText);
			matchStr = this.matchText;
		}
		return pattern;
	}
	
	public Object getValue() {
		return value;
	}

	public String getMatchText() {
		return matchText;
	}

	public String[] getMatchTexts() {
		return matchTexts;
	}

	public boolean isUseRegex() {
		return useRegex;
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setMatchText(String matchText) {
		this.matchText = matchText;
	}

	public void setMatchTexts(String[] matchTexts) {
		this.matchTexts = matchTexts;
	}

	public void setUseRegex(boolean useRegex) {
		this.useRegex = useRegex;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	@Override
	public Object extractFrom(String text) {
		return isMatched(text) ? value : null;
	}

	public boolean isMatched(String text) {
		boolean matched = false;
		int matchedCount = 0;
		// use matchText in preference to matchTexts
		boolean matchedTextMode = !StringUtil.isEmpty(this.matchText);
		if(!matchedTextMode && ArrayUtil.isEmpty(this.matchTexts)) {
			return matched;
		}
		
		if(this.useRegex) {
			Pattern pattern = this.getPattern();
			Matcher matcher = pattern.matcher(text);
			matched = matcher.find();
		} else {
			matchedCount = 0;
			List<String> matchedTexts = matchedTextMode ? Arrays.asList(this.matchText) :
				Arrays.asList(this.matchTexts);
			if(this.ignoreCase) {
				text = text.toUpperCase(Locale.US);
			}
			for (String findStr : matchedTexts) {
				findStr = this.ignoreCase ? findStr.toUpperCase(Locale.US) : findStr;
				if(text.contains(findStr)) {
					matchedCount++;
				}
			}
			matched = matchedCount == matchedTexts.size();
		}
		return matched;
	}
}
