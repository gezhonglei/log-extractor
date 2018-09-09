package com.gezhonglei.common.log.extractor.entity;

import java.util.ArrayList;
import java.util.List;

public class DataTable {
	private String name;
	private List<Field> fields;
	private List<DataRow> values;
	
	public DataTable() {}
	
	public DataTable(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Field> getFields() {
		return fields;
	}
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	public List<DataRow> getValues() {
		return values;
	}
	public void setValues(List<DataRow> values) {
		this.values = values;
	}
	
	public DataRow getRow(int index) {
		return index > -1 && index < values.size() ? values.get(index) : null;
	}
	
	public DataRow newRow() {
		DataRow row = new DataRow(this, new ArrayList<>(fields.size()));
		values.add(row);
		return row;
	}
	
	public boolean remove(DataRow row) {
		return values.remove(row);
	}
}
