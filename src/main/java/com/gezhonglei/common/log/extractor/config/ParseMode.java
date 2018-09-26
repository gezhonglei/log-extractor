package com.gezhonglei.common.log.extractor.config;

public enum ParseMode {
	Boundary("boundary"),
	Constant("const"),
	Matched("match"),
	Regex("regex");
	
	String value;
	ParseMode(String value) {
		this.value = value;
	}
	
	public static ParseMode fromValue(String value, ParseMode defValue) {
		for (ParseMode parseMode : ParseMode.values()) {
			if(parseMode.value.equals(value)) {
				return parseMode;
			}
		}
		return defValue;
	}
	
	public String toValue() {
		return this.value;
	}
}
