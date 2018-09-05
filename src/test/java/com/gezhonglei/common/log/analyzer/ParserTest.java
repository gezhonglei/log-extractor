package com.gezhonglei.common.log.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.gezhonglei.common.log.extractor.LogParser;
import com.gezhonglei.common.log.extractor.config.BoundaryPropRule;
import com.gezhonglei.common.log.extractor.config.EntityRule;
import com.gezhonglei.common.log.extractor.config.IPropRule;
import com.gezhonglei.common.log.extractor.config.ParserConfig;
import com.gezhonglei.common.log.extractor.config.RegexPropRule;
import com.gezhonglei.common.log.extractor.entity.Entity;
import com.gezhonglei.common.log.extractor.entity.ParseResult;
import com.gezhonglei.common.util.JsonUtil;

public class ParserTest {
	
	private ParserConfig getConfig() {
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
		
		ParserConfig config = new ParserConfig();
		String path = ParserTest.class.getClassLoader().getResource("log").getFile();
		config.setPath(path);
		config.setFilter("*.log");
		config.setRules(rules);
		config.setCommonPropRules(commonPropRules);
		return config;
	}
	
	@Test
	public void testParse() throws Exception {
		LogParser parser = new LogParser(getConfig());
		ParseResult result = parser.parse();
		List<Entity> entities = result.getResult("sendCmd");
		String json = JsonUtil.toJson(entities);
		System.out.println(JsonUtil.format(json, "    "));
		
		List<Entity> entities2 = result.getResult("executed");
		json = JsonUtil.toJson(entities2);
		System.out.println(JsonUtil.format(json, "    "));
		
		for (Entity entity : entities) {
			Map<String, Object> map = entity.getProps();
			String key = (String) map.get("id");
			for (Entity e2 : entities2) {
				
			}
		}
	}
}
