package com.gezhonglei.common.log.extractor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;








import org.apache.log4j.chainsaw.Main;

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
		
		Map<String, String> fieldToRuleMapping = getFieldToRuleMapping(outputRule);
		String mainRuleName = outputRule.getMainRule();
		List<String> fields = outputRule.getFields();
		List<Entity> result = parseEntity.getResult(mainRuleName);
		for (Entity entity : result) {
			DataRow row = table.newRow();
			for (int i = 0, len = fields.size(); i < len; i++) {
				String ruleName = fieldToRuleMapping.get(fields.get(i));
				if(ruleName != null && ruleName != mainRuleName) {
					Entity joinEntity = findEntity(entity, outputRule.getJoinRule(ruleName));
					if(null != joinEntity) {
//						String value = joinEntity.getPropValue();
					}
				}
			}
//			SetValues(row, entity, outputRule.getMainRuleFieldAlias());
//			for (JoinRule joinRule : outputRule.getJoinRules()) {
//				SetValues(row, entity, joinRule);
//			}
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

	private Map<String, String> getFieldToRuleMapping(OutputRule outputRule) {
		Map<String, String> results = new HashMap<>();
		List<String> fields = outputRule.getFields();
		String mainRuleName = outputRule.getMainRule();
		List<JoinRule> joinRules = outputRule.getJoinRules();
		
		//add props from joinRules
		if(joinRules != null) {
			joinRules.forEach(joinRule-> {
				String ruleName = joinRule.getJoinRuleName();
				EntityRule rule = config.getRule(ruleName);
				rule.getPropRules().keySet().forEach(k-> {
					results.put(k, ruleName);
				});
				joinRule.getFieldAlias().forEach((f,alias)-> {
					results.put(alias, ruleName);
				});
			});
		}
		// add props from mainRule
		EntityRule rule = config.getRule(mainRuleName);
		rule.getPropRules().keySet().forEach(k-> {
			results.put(k, mainRuleName);
		});
		outputRule.getMainRuleFieldAlias().forEach((f,alias)-> {
			results.put(alias, mainRuleName);
		});
		config.getCommonPropRules().keySet().forEach(k-> {
			results.put(k, mainRuleName);
		});
		// remove non-output fields
		for (String field : fields) {
			if(!results.containsKey(field)) {
				results.remove(field);
			}
		}
		return results;
	}
	
	private void SetValues(DataRow row, Entity entity, JoinRule joinRule) {
		
	}

	private void SetValues(DataRow row, Entity entity, Map<String, String> fieldAlias) {
		
	}

	private List<Field> getFields(OutputRule outputRule) {
		return outputRule.getFields().stream().map(Field::new).collect(Collectors.toList());
	}
}
