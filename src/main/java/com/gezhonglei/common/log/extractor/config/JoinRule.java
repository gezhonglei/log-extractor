package com.gezhonglei.common.log.extractor.config;

import java.util.Map;

public class JoinRule {
	private String joinRuleName;
	private Map<String, String> keyMapping;
	private Map<String, String> fieldAlias;
	
	public String getJoinRuleName() {
		return joinRuleName;
	}
	public void setJoinRuleName(String joinRuleName) {
		this.joinRuleName = joinRuleName;
	}
	public Map<String, String> getKeyMapping() {
		return keyMapping;
	}
	public void setKeyMapping(Map<String, String> keyMapping) {
		this.keyMapping = keyMapping;
	}
	public Map<String, String> getFieldAlias() {
		return fieldAlias;
	}
	public void setFieldAlias(Map<String, String> fieldAlias) {
		this.fieldAlias = fieldAlias;
	}
	
}
