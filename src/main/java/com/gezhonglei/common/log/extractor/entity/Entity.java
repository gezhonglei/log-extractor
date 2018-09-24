package com.gezhonglei.common.log.extractor.entity;

import java.util.HashMap;
import java.util.Map;

public class Entity {
	private String ruleName;
	private String source;
	private Map<String, Object> props = new HashMap<>();
	
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
	
	public Map<String, Object> getAllProps() {
		return new HashMap<>(props);
	}
	public void setPropValue(String propName, Object value) {
		this.props.put(propName, value);
	}
	public Object getPropValue(String propName) {
		return this.props.get(propName);
	}
}
