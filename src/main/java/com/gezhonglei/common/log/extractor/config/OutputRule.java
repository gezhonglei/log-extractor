package com.gezhonglei.common.log.extractor.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.codehaus.jackson.annotate.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputRule {
	private static Logger logger = LoggerFactory.getLogger(OutputRule.class);
	
	private String name;
	private String mainRule;
	private Map<String, String> mainRuleFieldAlias;
	@JsonProperty("joins")
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
		return Optional.ofNullable(fields).orElseGet(ArrayList::new);
	}
	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public String getMainRule() {
		return Optional.ofNullable(mainRule).orElse(name);
	}

	public void setMainRule(String mainRule) {
		this.mainRule = mainRule;
	}

	public Map<String, String> getMainRuleFieldAlias() {
		return Optional.ofNullable(mainRuleFieldAlias).orElseGet(HashMap::new);
	}

	public void setMainRuleFieldAlias(Map<String, String> mainRuleFieldAlias) {
		this.mainRuleFieldAlias = mainRuleFieldAlias;
	}

	public List<JoinRule> getJoinRules() {
		return Optional.ofNullable(joinRules).orElseGet(ArrayList::new);
	}

	public void setJoinRules(List<JoinRule> joinRules) {
		this.joinRules = joinRules;
	}
	
	public JoinRule getJoinRule(String name) {
		return joinRules.stream().filter(p-> p.getJoinRuleName().equals(name)).findFirst().orElse(null);
	}
	
	public List<String> getOutputFields(ExtracteConfig config) {
		List<String> fields = getFields();
		if(fields.isEmpty()) {
			String mainRule = getMainRule();
			return config.getAllPropRule(mainRule).keySet().stream().collect(Collectors.toList());
		}
		return fields;
	}
}
