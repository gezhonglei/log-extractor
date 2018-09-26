package com.gezhonglei.common.log.extractor.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import com.gezhonglei.common.log.extractor.config.rule.MatchPropRule;

public class EntityRule {
	private String name;
	private String matchText;
	private String[] matchTexts;
	private boolean useRegex;
	private boolean ignoreCase;
	
	@JsonProperty("props")
	private List<PropRule> propRules;
	
	@JsonIgnore
	private MatchPropRule rule;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMatchText() {
		return matchText;
	}
	
	public void setMatchText(String matchText) {
		this.matchText = matchText;
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
	
	public boolean matched(String text) {
		return this.getMatchRule().isMatched(text);
	}
	
	private MatchPropRule getMatchRule() {
		if(this.rule == null) {
			this.reloadMatchRule();
		}
		return this.rule;
	}
	
	/**
	 * <b>reloadMatchRule</b> is required after the properties is modified
	 */
	public void reloadMatchRule() {
		this.rule = new MatchPropRule();
		this.rule.setMatchText(this.matchText);
		this.rule.setMatchTexts(this.matchTexts);
		this.rule.setUseRegex(this.useRegex);
		this.rule.setIgnoreCase(this.ignoreCase);
	}
}
