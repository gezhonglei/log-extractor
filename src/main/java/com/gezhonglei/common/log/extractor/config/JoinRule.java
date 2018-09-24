package com.gezhonglei.common.log.extractor.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
		return Optional.ofNullable(keyMapping).orElseGet(HashMap::new);
	}
	public void setKeyMapping(Map<String, String> keyMapping) {
		this.keyMapping = keyMapping;
	}
	public Map<String, String> getFieldAlias() {
		return Optional.ofNullable(fieldAlias).orElseGet(HashMap::new);
	}
	public void setFieldAlias(Map<String, String> fieldAlias) {
		this.fieldAlias = fieldAlias;
	}
	
}
