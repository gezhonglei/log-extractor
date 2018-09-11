package com.gezhonglei.common.log.extractor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
	
	public OutputHandler(ParseResult parseEntity, ExtracteConfig config) {
		this.parseEntity = parseEntity;
		this.config = config;
	}
	
	public DataSet handle() {
		DataSet dataSet = new DataSet();
		List<OutputRule> output = this.config.getOutput();
		for (OutputRule outputRule : output) {
			dataSet.addTable(handle(outputRule));
		}
		return dataSet;
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
					if(entity.getRuleName().equals(mapping.getRule().getName())) {
						value = entity.getPropValue(mapping.getPropName());
						row.setValue(mapping.fieldIndex, value);
					} else {
						Entity joinEntity = findEntity(entity, outputRule.getJoinRule(mapping.getRule().getName()));
						if(null != joinEntity) {
							value = joinEntity.getPropValue(mapping.getPropName());
							row.setValue(mapping.fieldIndex, value);
						}
					}
				}
			}
		}
		
		return table;
	}
	
	private Entity findEntity(Entity mainEntity, JoinRule joinRule) {
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
					results.put(prop.getName(), new FieldMapping(prop.getName(), rule, prop.getName()));
				});
				joinRule.getFieldAlias().forEach((f,alias)-> {
					results.put(alias, new FieldMapping(alias, rule, f));
				});
			});
		}
		// add props from mainRule
		EntityRule rule = config.getRule(mainRuleName);
		rule.getPropRules().forEach(prop-> {
			results.put(prop.getName(), new FieldMapping(prop.getName(), rule, prop.getName()));
		});
		outputRule.getMainRuleFieldAlias().forEach((f,alias)-> {
			results.put(alias, new FieldMapping(alias, rule, f));
		});
		config.getCommonPropRules().forEach(prop-> {
			results.put(prop.getName(), new FieldMapping(prop.getName(), rule, prop.getName()));
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
	
	private List<Field> getFields(OutputRule outputRule) {
		return outputRule.getOutputFields(config).stream().map(Field::new).collect(Collectors.toList());
	}
	
	static class FieldMapping {
		private String field;
		private int fieldIndex;
		private EntityRule rule;
		private String propName;
		
		public FieldMapping(String field, EntityRule rule, String propName) {
			super();
			this.field = field;
			this.rule = rule;
			this.propName = propName;
		}
		
		public String getField() {
			return field;
		}
		public void setField(String field) {
			this.field = field;
		}
		public EntityRule getRule() {
			return rule;
		}
		public void setRule(EntityRule rule) {
			this.rule = rule;
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
