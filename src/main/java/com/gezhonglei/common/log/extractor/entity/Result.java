package com.gezhonglei.common.log.extractor.entity;

import java.util.List;

public class Result {
	private int statusCode;
	private List<Entity> values;
	private String message;
	
	public Result(int statusCode, List<Entity> values, String message) {
		super();
		this.statusCode = statusCode;
		this.values = values;
		this.message = message;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public List<Entity> getValues() {
		return values;
	}
	public void setValues(List<Entity> values) {
		this.values = values;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
