package com.gezhonglei.common.log.extractor.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gezhonglei.common.util.StringUtil;

public class DataSet {
	private Map<String, DataTable> tables;
	
	public DataSet() {
		tables = new HashMap<>();
	}

	public DataTable[] getTables() {
		DataTable[] results = new DataTable[tables.size()];
		tables.values().toArray(results);
		return results;
	}

	public void setTables(List<DataTable> tables) {
		this.tables.clear();
		for (DataTable table : tables) {
			this.addTable(table);
		}
	}
	
	public DataTable getTable(String name) {
		return tables.get(name);
	}
	
	public void addTable(String name, DataTable table) {
		this.tables.put(name, table);
	}
	
	public void addTable(DataTable table) {
		String name = table.getName();
		if(StringUtil.isEmpty(name)) {
			name = "table-" + tables.size();
		}
		this.addTable(name, table);
	}
	
}
