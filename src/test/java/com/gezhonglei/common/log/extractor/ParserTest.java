package com.gezhonglei.common.log.extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.gezhonglei.common.log.extractor.LogExtractor;
import com.gezhonglei.common.log.extractor.config.BoundaryPropRule;
import com.gezhonglei.common.log.extractor.config.EntityRule;
import com.gezhonglei.common.log.extractor.config.IPropRule;
import com.gezhonglei.common.log.extractor.config.JoinRule;
import com.gezhonglei.common.log.extractor.config.OutputRule;
import com.gezhonglei.common.log.extractor.config.ExtracteConfig;
import com.gezhonglei.common.log.extractor.config.RegexPropRule;
import com.gezhonglei.common.log.extractor.entity.Entity;
import com.gezhonglei.common.log.extractor.entity.ParseResult;
import com.gezhonglei.common.util.JsonUtil;

public class ParserTest {
	
	private ExtracteConfig getConfig() {
		// ----- rule1 -------
		Map<String, IPropRule> props = new HashMap<>();
		BoundaryPropRule propRule = new BoundaryPropRule();
		propRule.setName("cmd");
		propRule.setBeginText("cmd=");
		propRule.setEndText(",");
		props.put("cmd", propRule);
		propRule = new BoundaryPropRule();
		propRule.setName("id");
		propRule.setBeginText("id=");
		propRule.setLength(10);
		props.put("id", propRule);
		
		List<EntityRule> rules = new ArrayList<>();
		EntityRule rule = new EntityRule();
		rule.setName("sendCmd");
		rule.setMatchText("sendRequest: cmd=(con|disc)");
		rule.setUseRegex(true);
		rule.setIgnoreCase(false);
		rule.setPropRules(props);
		rules.add(rule);
		
		// ----- rule2 -------
		props = new HashMap<>();
		propRule = new BoundaryPropRule();
		propRule.setName("id");
		propRule.setBeginText("id=");
		propRule.setLength(10);
		props.put("id", propRule);
		propRule = new BoundaryPropRule();
		propRule.setName("total");
		propRule.setBeginText("totalCost=");
		propRule.setEndText(",");
		props.put(propRule.getName(), propRule);
		propRule = new BoundaryPropRule();
		propRule.setName("recv");
		propRule.setBeginText("recv=");
		propRule.setEndText(",");
		props.put(propRule.getName(), propRule);
		
		rule = new EntityRule();
		rule.setName("executed");
		rule.setMatchText("execute\\((con|disc)\\):");
		rule.setUseRegex(true);
		rule.setIgnoreCase(false);
		rule.setPropRules(props);
		rules.add(rule);
		
		Map<String, IPropRule> commonPropRules = new HashMap<>();
		RegexPropRule regexProp = new RegexPropRule();
		regexProp.setRegex("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3})\\s");
		regexProp.setGroupIndex(1);
		regexProp.setName("logtime");
		commonPropRules.put("logtime", regexProp);
		
		ExtracteConfig config = new ExtracteConfig();
		String path = ParserTest.class.getClassLoader().getResource("log").getFile();
		config.setPath(path);
		config.setFilter("*.log");
		config.setRules(rules);
		config.setCommonPropRules(commonPropRules);
		
		// Output-Rule
		List<OutputRule> outputs = new ArrayList<>();
		OutputRule outputRule = new OutputRule("sendCmd");
		outputs.add(outputRule);
		
		outputRule = new OutputRule("excuted");
		outputs.add(outputRule);
		
		outputRule = new OutputRule("sendCmd-join", "sendCmd");
		Map<String, String> mainRuleFieldAlias = new HashMap<>();
		mainRuleFieldAlias.put("logtime", "time");
		mainRuleFieldAlias.put("total", "totalCost");
		outputRule.setMainRuleFieldAlias(mainRuleFieldAlias);
		List<JoinRule> joinRules = new LinkedList<>();
		JoinRule e = new JoinRule();
		e.setJoinRuleName("excuted");
		Map<String, String> keyMapping = new HashMap<>();
		keyMapping.put("id", "id");
		e.setKeyMapping(keyMapping);
		joinRules.add(e);
		outputRule.setJoinRules(joinRules);
		List<String> fields = Arrays.asList("id", "time", "cmd", "totalCost", "recv");
		outputRule.setFields(fields);
		
		outputs.add(outputRule);
		config.setOutput(outputs);
		return config;
	}
	
	@Test
	public void testParse() throws Exception {
		LogExtractor parser = new LogExtractor(getConfig());
		ParseResult result = parser.parse();
		List<Entity> entities = result.getResult("sendCmd");
		String json = JsonUtil.toJson(entities);
		System.out.println(JsonUtil.format(json, "    "));
		
		List<Entity> entities2 = result.getResult("executed");
		json = JsonUtil.toJson(entities2);
		System.out.println(JsonUtil.format(json, "    "));
		
		List<Map<String, Object>> results = new ArrayList<>();
		for (Entity entity : entities) {
			Map<String, Object> row = new HashMap<>();
			row.putAll(entity.getProps());
			row.putAll(entity.getCommonProps());
			
			String key = (String) entity.getProps().get("id");
			for (Entity e2 : entities2) {
				if(key.equals(e2.getProps().get("id"))) {
					row.putAll(e2.getProps());
					e2.getProps().forEach((k,v)-> {
						row.put("ref-" + k, v);
					});
					e2.getCommonProps().forEach((k,v)->{
						row.put("common-" + k, v);
					});
					break;
				}
			}
			results.add(row);
		}
		
		results.get(0).forEach((k,v)-> {
			System.out.print(k + "\t");
		});
		System.out.println();
		results.forEach(r -> {
			r.forEach((k,v)-> {
				System.out.print(v + "\t");
			});
			System.out.println();
		});
	}
}
