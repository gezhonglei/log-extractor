package com.gezhonglei.common.log.extractor.entity;

import java.util.List;

public class DataRow {
	private DataTable table;
	private Object[] values;
	
	public DataRow(DataTable table, List<Object> values) {
		this.table = table;
		this.values = new Object[table.getFields().size()];
	}
	
	public boolean setValue(String name, Object value) {
		Field field = null;
		List<Field> fields = table.getFields();
		boolean status = false;
		for (int i = 0, size = fields.size(); i < size; i++) {
			field = fields.get(i);
			if(field.getName().equals(name)) {
				// TODO: check value type
				values[i] = value;
				status = true;
				break;
			}
		}
		return status;
	}
	
	public Object getValue(String name) {
		Field field = null;
		List<Field> fields = table.getFields();
		for (int i = 0, size = fields.size(); i < size; i++) {
			field = fields.get(i);
			if(field.getName().equals(name)) {
				return values[i];
			}
		}
		return null;
	}
	
	public boolean setValue(int fieldIndex, Object value) {
		if(fieldIndex > -1 && fieldIndex < table.getFields().size()) {
			// TODO: check value type
			values[fieldIndex] = value;
			return true;
		}
		return false;
	}
	
	public Object getValue(int fieldIndex) {
		if(fieldIndex > -1 && fieldIndex < table.getFields().size()) {
			return values[fieldIndex];
		}
		throw new IndexOutOfBoundsException("fieldIndex Out of Bounds");
	}
}
