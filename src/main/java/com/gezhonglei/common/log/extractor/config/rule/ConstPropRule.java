package com.gezhonglei.common.log.extractor.config.rule;

import com.gezhonglei.common.log.extractor.config.PropRule;

public class ConstPropRule extends PropRule {

	private Object value;
	
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public Object extractFrom(String text) {
		return value;
	}

}
