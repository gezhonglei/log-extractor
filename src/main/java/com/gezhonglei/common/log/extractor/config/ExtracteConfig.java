package com.gezhonglei.common.log.extractor.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ExtracteConfig {
	private String encode = "UTF-8";
	private int bufferSize = 1024;  // KB
	
	private String path;
	private String filter;
	@JsonProperty("commonProps")
	private List<PropRule> commonPropRules;
	private List<EntityRule> rules;
	private List<OutputRule> output;
	private String outputPath;
	
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
	public List<OutputRule> getOutput() {
		return Optional.ofNullable(output).orElseGet(ArrayList::new);
	}
	public void setOutput(List<OutputRule> output) {
		this.output = output;
	}

	
	public EntityRule getRule(String name) {
		return this.rules.stream().filter(p-> p.getName().equals(name)).findFirst().orElse(null);
	}
	
	public String getOutputPath() {
		return outputPath;
	}
	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}
}
