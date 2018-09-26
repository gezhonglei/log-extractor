package com.gezhonglei.common.log.extractor.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.codehaus.jackson.annotate.JsonProperty;

public class LogSource {
	
	private String encode = "UTF-8";
	private int bufferSize = 1024;  // KB	
	
	private String path;
	private String filter;
	@JsonProperty("commonProps")
	private List<PropRule> commonPropRules;
	private List<EntityRule> rules;
	
	
	public String getEncode() {
		return encode;
	}
	public void setEncode(String encode) {
		this.encode = encode;
	}
	public int getBufferSize() {
		return bufferSize;
	}
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	public List<EntityRule> getRules() {
		return Optional.ofNullable(rules).orElseGet(ArrayList::new);
	}
	public void setRules(List<EntityRule> rules) {
		this.rules = rules;
	}
	public List<PropRule> getCommonPropRules() {
		return Optional.ofNullable(commonPropRules).orElseGet(ArrayList::new);
	}
	public void setCommonPropRules(List<PropRule> commonPropRules) {
		this.commonPropRules = commonPropRules;
	}
	
	public EntityRule getRule(String name) {
		return this.rules.stream().filter(p-> p.getName().equals(name)).findFirst().orElse(null);
	}

	public boolean containsRule(String ruleName) {
		return this.rules.stream().anyMatch(p-> p.getName().equals(ruleName));
	}
	
	public Map<String, PropRule> getAllPropRule(String ruleName) {
		Map<String, PropRule> propRules = new HashMap<>();
		EntityRule rule = this.getRule(ruleName);
		if(rule != null) {
			this.getCommonPropRules().forEach(p-> propRules.put(p.getName(), p));
			rule.getPropRules().forEach(p-> propRules.put(p.getName(), p));
		}
		return propRules;
	}
}
