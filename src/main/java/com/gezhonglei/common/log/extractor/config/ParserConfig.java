package com.gezhonglei.common.log.extractor.config;

import java.util.List;
import java.util.Map;

public class ParserConfig {
	private String path;
	private String filter;
	private List<EntityRule> rules;
	private Map<String, IPropRule> commonPropRules;
	
	private String encode = "UTF-8";
	private int bufferSize = 1024;  // KB
	
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
		return rules;
	}
	public void setRules(List<EntityRule> rules) {
		this.rules = rules;
	}
	public Map<String, IPropRule> getCommonPropRules() {
		return commonPropRules;
	}
	public void setCommonPropRules(Map<String, IPropRule> commonPropRules) {
		this.commonPropRules = commonPropRules;
	}
	
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

	
	
}
