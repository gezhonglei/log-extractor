package com.gezhonglei.common.log.extractor.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ExtracteConfig {
	private String encode = "UTF-8";
	private int bufferSize = 1024;  // KB	
	
	private List<LogSource> sources = new ArrayList<>();
	private String outputPath;
	private List<OutputRule> output = new ArrayList<>();
	
	public Map<String, PropRule> getAllPropRule(String ruleName) {
		Map<String, PropRule> propRules = new HashMap<>();
		LogSource source = this.sources.stream().filter(p-> p.containsRule(ruleName)).findFirst().orElse(null);
		if(source != null) {
			propRules = source.getAllPropRule(ruleName);
		}
		return propRules;
	}
	
	public Optional<LogSource> getSource(String ruleName) {
		return this.sources.stream().filter(p-> null != p.getRule(ruleName)).findFirst();
	}
	
	public EntityRule getRule(String ruleName) {
		Optional<LogSource> source = this.getSource(ruleName);
		return source.map(p-> p.getRule(ruleName)).orElse(null);
	}
	
	public String getOutputPath(String outputName) {
		OutputRule outputRule = output.stream().filter(p-> p.getName().equals(outputName)).findFirst().orElse(null);
		if(outputRule == null) {
			return this.outputPath;
		} 
		// outputRule.ouputPath -> config.outputPath -> entityRule.path
		String path = Optional.ofNullable(outputRule.getOutputPath()).orElse(Optional.ofNullable(this.outputPath).orElse(outputPath));
		if(path == null) {
			path = this.getSource(outputRule.getMainRule()).map(p-> p.getPath()).orElse(null);
		}
		if(path != null) {
			File file = new File(path);
			path = file.isFile() ? file.getParent() : path;
		}
		return path;
	}
	
	public List<LogSource> getSources() {
		return sources;
	}
	public void setSources(List<LogSource> sources) {
		this.sources = sources;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public String getEncode() {
		return encode;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}
	
	public List<OutputRule> getOutput() {
		return Optional.ofNullable(output).orElseGet(ArrayList::new);
	}
	public void setOutput(List<OutputRule> output) {
		this.output = output;
	}
	
	public String getOutputPath() {
		return outputPath;
	}
	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public void addSource(LogSource source) {
		this.sources.add(source);
	}
	
}
