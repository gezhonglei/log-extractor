package com.gezhonglei.common.log.extractor.config;

public interface IPropRule {
	ParseMode mode();
	String extractFrom(String text);
	String getName();
	String getType();
}
