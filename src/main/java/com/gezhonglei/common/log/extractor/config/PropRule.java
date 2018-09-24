package com.gezhonglei.common.log.extractor.config;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;

@JsonTypeInfo(use=Id.NAME, property="mode", defaultImpl=BoundaryPropRule.class)
@JsonSubTypes({@JsonSubTypes.Type(value = BoundaryPropRule.class, name = "boundary"), 
	@JsonSubTypes.Type(value = ConstPropRule.class, name = "const"),
	@JsonSubTypes.Type(value = RegexPropRule.class, name = "regex")})
public abstract class PropRule {
	protected String mode;
	protected String name;
	protected String type;
	private String format;
	
	public abstract String extractFrom(String text);
	
	public ParseMode getMode() {
		return ParseMode.fromValue(mode, ParseMode.Boundary);
	}
	
	public String getType() {
		return this.type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
}
