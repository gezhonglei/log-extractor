package com.gezhonglei.common.log.extractor.config;

import java.util.List;
import java.util.Map;

public class OutputRule {
	private String name;
	private String mainRule;
	private Map<String, String> mainRuleFieldAlias;
	private List<JoinRule> joinRules;
	private List<String> fields;

	public OutputRule() {}
	
	public OutputRule(String mainRule) {
		this.name = mainRule;
		this.mainRule = mainRule;
	}
	
	public OutputRule(String name, String mainRule) {
		this.name = name;
		this.mainRule = mainRule;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public List<String> getFields() {
		return fields;
	}
	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public String getMainRule() {
		return mainRule;
	}

	public void setMainRule(String mainRule) {
		this.mainRule = mainRule;
	}

	public Map<String, String> getMainRuleFieldAlias() {
		return mainRuleFieldAlias;
	}

	public void setMainRuleFieldAlias(Map<String, String> mainRuleFieldAlias) {
		this.mainRuleFieldAlias = mainRuleFieldAlias;
	}

	public List<JoinRule> getJoinRules() {
		return joinRules;
	}

	public void setJoinRules(List<JoinRule> joinRules) {
		this.joinRules = joinRules;
	}
	
	public JoinRule getJoinRule(String name) {
		return joinRules.stream().filter(p-> p.getJoinRuleName().equals(name)).findFirst().orElse(null);
	}
}
