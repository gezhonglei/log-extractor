package com.gezhonglei.common.log.extractor.config;

public class ConstPropRule extends PropRule {

	private String value;
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String extractFrom(String text) {
		return value;
	}

}
