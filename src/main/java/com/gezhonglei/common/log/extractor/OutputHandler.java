package com.gezhonglei.common.log.extractor;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.gezhonglei.common.log.extractor.config.EntityIndex;
import com.gezhonglei.common.log.extractor.config.EntityRule;
import com.gezhonglei.common.log.extractor.config.ExtracteConfig;
import com.gezhonglei.common.log.extractor.config.JoinRule;
import com.gezhonglei.common.log.extractor.config.OutputRule;
import com.gezhonglei.common.log.extractor.entity.DataRow;
import com.gezhonglei.common.log.extractor.entity.DataSet;
import com.gezhonglei.common.log.extractor.entity.DataTable;
import com.gezhonglei.common.log.extractor.entity.Entity;
import com.gezhonglei.common.log.extractor.entity.Field;
import com.gezhonglei.common.log.extractor.entity.ParseResult;

public class OutputHandler {
	
	private ParseResult parseEntity;
	private ExtracteConfig config;
	private Map<String,Map<String, EntityIndex>> ruleIndexes;
	
	public OutputHandler(ParseResult parseEntity, ExtracteConfig config) {
		this.parseEntity = parseEntity;
		this.config = config;
	}
	
	public DataSet handle2() {
		createIndexesForJoinEntity();

		DataSet dataSet = new DataSet();
		List<OutputRule> output = this.config.getOutput();
		for (OutputRule outputRule : output) {
			//dataSet.addTable(handle(outputRule));
			dataSet.addTable(handle2(outputRule));
		}
		return dataSet;
	}
	
	public DataSet handle() {

		DataSet dataSet = new DataSet();
		List<OutputRule> output = this.config.getOutput();
		for (OutputRule outputRule : output) {
			dataSet.addTable(handle(outputRule));
		}
		return dataSet;
	}
	
	private void createIndexesForJoinEntity() {
		ruleIndexes = new HashMap<>(); // <outputName, <joinName, EntityIndex>>
		Map<String, EntityIndex> joinIndexes = new HashMap<>(); // <joinName+fields, EntityIndex>
		for (OutputRule outputRule : this.config.getOutput()) {
			// <joinRuleName, [fields]>
			Map<String, EntityIndex> index = new HashMap<>();
			outputRule.getJoinRules().forEach(joinRule -> {
				List<String> mapingFields = joinRule.getKeyMapping().keySet().stream().distinct().collect(Collectors.toList());
				String key = joinRule.getJoinRuleName() + mapingFields;
				EntityIndex entityIndex = joinIndexes.get(key);
				if(entityIndex == null) {
					List<Entity> result = parseEntity.getResult(joinRule.getJoinRuleName());
					entityIndex = new EntityIndex(key, joinRule, mapingFields, result);
					joinIndexes.put(joinRule.getJoinRuleName(), entityIndex);
				}
				index.put(joinRule.getJoinRuleName(), entityIndex);
			});
			ruleIndexes.put(outputRule.getName(), index);
		}
	}
	
	private DataTable handle2(OutputRule outputRule) {
		DataTable table = new DataTable(outputRule.getName());
		table.setFields(getFields(outputRule));
		
		Map<String, FieldMapping> fieldToRuleMapping = getFieldToRuleMapping(outputRule);
		String mainRuleName = outputRule.getMainRule();
		List<Entity> result = parseEntity.getResult(mainRuleName);
		List<String> fields = outputRule.getOutputFields(config);
		
		Object value = null;
		for (Entity entity : result) {
			DataRow row = table.newRow();
			for (int i = 0, len = fields.size(); i < len; i++) {
				FieldMapping mapping = fieldToRuleMapping.get(fields.get(i));
				if(mapping != null) {
					List<EntityRule> rules = mapping.getRules();
					if(rules.stream().anyMatch(p-> p.getName().equals(mainRuleName))) {
						value = entity.getPropValue(mapping.getPropName());
						row.setValue(mapping.fieldIndex, value);
					} else {
						for (EntityRule entityRule : rules) {
							EntityIndex index = ruleIndexes.get(outputRule.getName()).get(entityRule.getName());
							if(index != null) {
								List<String> indexPropValues = index.getRefValue(entity);
								List<Entity> joinEntity = index.getEntityList(indexPropValues);
								if(null != joinEntity && !joinEntity.isEmpty()) {
									value = joinEntity.get(0).getPropValue(mapping.getPropName());
									row.setValue(mapping.fieldIndex, value);
								}
							}
						}
					}
				}
			}
		}
		return table;
	}
	
	private DataTable handle(OutputRule outputRule) {
		DataTable table = new DataTable(outputRule.getName());
		table.setFields(getFields(outputRule));
		
		Map<String, FieldMapping> fieldToRuleMapping = getFieldToRuleMapping(outputRule);
		String mainRuleName = outputRule.getMainRule();
		List<String> fields = outputRule.getOutputFields(config);
		List<Entity> result = parseEntity.getResult(mainRuleName);
		Object value = null;
		for (Entity entity : result) {
			DataRow row = table.newRow();
			for (int i = 0, len = fields.size(); i < len; i++) {
				FieldMapping mapping = fieldToRuleMapping.get(fields.get(i));
				if(mapping != null) {
					List<EntityRule> rules = mapping.getRules();
					if(rules.stream().anyMatch(p-> p.getName().equals(mainRuleName))) {
						value = entity.getPropValue(mapping.getPropName());
						row.setValue(mapping.fieldIndex, value);
					} else {
						for (EntityRule entityRule : rules) {
							Entity joinEntity = findJoinEntity(entity, outputRule.getJoinRule(entityRule.getName()));
							if(null != joinEntity) {
								value = joinEntity.getPropValue(mapping.getPropName());
								row.setValue(mapping.fieldIndex, value);
							}
						}
					}
				}
			}
		}
		
		return table;
	}
	
	private Entity findJoinEntity(Entity mainEntity, JoinRule joinRule) {
		Map<String, String> keyMapping = joinRule.getKeyMapping();
		Map<String, Object> conditions = new HashMap<>();
		keyMapping.forEach((k, mainKey)->{
			conditions.put(k, mainEntity.getPropValue(mainKey));
		});
		
		List<Entity> result = parseEntity.getResult(joinRule.getJoinRuleName());
		return result.stream().filter(p-> isMathed(p, conditions)).findFirst().orElse(null);
	}
	
	private boolean isMathed(Entity entity, Map<String, Object> conditions) {
		return conditions.entrySet().stream().allMatch(entry -> {
			return entry.getValue() != null && entry.getValue().equals(entity.getPropValue(entry.getKey()));
		});
	}

	private Map<String, FieldMapping> getFieldToRuleMapping(OutputRule outputRule) {
		Map<String, FieldMapping> results = new HashMap<>();
		List<String> fields = outputRule.getOutputFields(config);
		String mainRuleName = outputRule.getMainRule();
		List<JoinRule> joinRules = outputRule.getJoinRules();
		
		//add props from joinRules
		if(joinRules != null) {
			joinRules.forEach(joinRule-> {
				String ruleName = joinRule.getJoinRuleName();
				EntityRule rule = config.getRule(ruleName);
				rule.getPropRules().forEach(prop-> {
					addRule(prop.getName(), prop.getName(), rule, results);
					//results.put(prop.getName(), new FieldMapping(prop.getName(), rule, prop.getName()));
				});
				joinRule.getFieldAlias().forEach((f,alias)-> {
					addRule(alias, f, rule, results);
					//results.put(alias, new FieldMapping(alias, rule, f));
				});
			});
		}
		// add props from mainRule
		EntityRule rule = config.getRule(mainRuleName);
		outputRule.getMainRuleFieldAlias().forEach((f,alias)-> {
			addRule(alias, f, rule, results);
			//results.put(alias, new FieldMapping(alias, rule, f));
		});
		config.getCommonPropRules().forEach(prop-> {
			addRule(prop.getName(), prop.getName(), rule, results);
			//results.put(prop.getName(), new FieldMapping(prop.getName(), rule, prop.getName()));
		});
		rule.getPropRules().forEach(prop-> {
			addRule(prop.getName(), prop.getName(), rule, results);
			//results.put(prop.getName(), new FieldMapping(prop.getName(), rule, prop.getName()));
		});
		// remove non-output fields
		for (String field : fields) {
			if(!results.containsKey(field)) {
				results.remove(field);
			} else {
				results.get(field).setFieldIndex(fields.indexOf(field));
			}
		}
		return results;
	}
	
	private void addRule(String field, String propName, EntityRule rule, Map<String, FieldMapping> results) {
		FieldMapping fieldMapping = results.get(field);
		if(fieldMapping == null) {
			fieldMapping = new FieldMapping(field, propName);
			results.put(field, fieldMapping);
		}
		List<EntityRule> rules = fieldMapping.getRules();
		if(!rules.contains(rule)) {
			rules.add(rule);
		}
	}
	
	private List<Field> getFields(OutputRule outputRule) {
		return outputRule.getOutputFields(config).stream().map(Field::new).collect(Collectors.toList());
	}
	
	static class FieldMapping {
		private String field;
		private int fieldIndex;
		private List<EntityRule> rules;
		private String propName;
		
		public FieldMapping(String field, String propName) {
			super();
			this.field = field;
			this.rules = new LinkedList<>();
			this.propName = propName;
		}
		
		public String getField() {
			return field;
		}
		public void setField(String field) {
			this.field = field;
		}
		public List<EntityRule> getRules() {
			return rules;
		}
		public void setRules(List<EntityRule> rule) {
			this.rules = rule;
		}
		public String getPropName() {
			return propName;
		}
		public void setPropName(String propName) {
			this.propName = propName;
		}

		public int getFieldIndex() {
			return fieldIndex;
		}

		public void setFieldIndex(int fieldIndex) {
			this.fieldIndex = fieldIndex;
		}
	}
}
