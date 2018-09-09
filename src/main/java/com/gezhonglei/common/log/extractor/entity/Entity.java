package com.gezhonglei.common.log.extractor.entity;

import java.util.HashMap;
import java.util.Map;

public class Entity {
	private String ruleName;
	private String source;
	private Map<String, Object> props;
	private Map<String, Object> commonProps;
	
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public Map<String, Object> getProps() {
		return props;
	}
	public void setProps(Map<String, Object> props) {
		this.props = props;
	}
	public Map<String, Object> getCommonProps() {
		return commonProps;
	}
	public void setCommonProps(Map<String, Object> commonProps) {
		this.commonProps = commonProps;
	}
	
	public Map<String, Object> getAllProps() {
		Map<String, Object> allProps = new HashMap<>();
		allProps.putAll(commonProps);
		allProps.putAll(props);
		return allProps;
	}
	public Object getPropValue(String mainKey) {
		if(this.props.containsKey(mainKey)) {
			return this.props.get(mainKey);
		}
		if(this.commonProps.containsKey(mainKey)) {
			return this.commonProps.get(mainKey);
		}
		return null;
	}
}
