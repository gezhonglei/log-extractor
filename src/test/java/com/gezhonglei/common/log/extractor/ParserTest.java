package com.gezhonglei.common.log.extractor;

import java.io.IOException;
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
import com.gezhonglei.common.log.extractor.config.PropRule;
import com.gezhonglei.common.log.extractor.config.JoinRule;
import com.gezhonglei.common.log.extractor.config.OutputRule;
import com.gezhonglei.common.log.extractor.config.ExtracteConfig;
import com.gezhonglei.common.log.extractor.config.RegexPropRule;
import com.gezhonglei.common.log.extractor.entity.DataSet;
import com.gezhonglei.common.log.extractor.entity.DataTable;
import com.gezhonglei.common.log.extractor.entity.ParseResult;
import com.gezhonglei.common.util.FileUtil;
import com.gezhonglei.common.util.JsonUtil;

public class ParserTest {
	
	private ExtracteConfig getConfig() {
		// ----- rule1 -------
		List<PropRule> props = new ArrayList<>();
		BoundaryPropRule propRule = new BoundaryPropRule();
		propRule.setName("cmd");
		propRule.setBeginText("cmd=");
		propRule.setEndText(",");
		props.add(propRule);
		propRule = new BoundaryPropRule();
		propRule.setName("id");
		propRule.setBeginText("id=");
		propRule.setLength(10);
		props.add(propRule);
		
		List<EntityRule> rules = new ArrayList<>();
		EntityRule rule = new EntityRule();
		rule.setName("sendCmd");
		rule.setMatchText("sendRequest: cmd=(con|disc)");
		rule.setUseRegex(true);
		rule.setIgnoreCase(false);
		rule.setPropRules(props);
		rules.add(rule);
		
		// ----- rule2 -------
		props = new ArrayList<>();
		propRule = new BoundaryPropRule();
		propRule.setName("id");
		propRule.setBeginText("id=");
		propRule.setLength(10);
		props.add(propRule);
		propRule = new BoundaryPropRule();
		propRule.setName("total");
		propRule.setBeginText("totalCost=");
		propRule.setEndText(",");
		props.add(propRule);
		propRule = new BoundaryPropRule();
		propRule.setName("recv");
		propRule.setBeginText("recv=");
		propRule.setEndText(",");
		props.add(propRule);
		
		rule = new EntityRule();
		rule.setName("executed");
		rule.setMatchText("execute\\((con|disc)\\):");
		rule.setUseRegex(true);
		rule.setIgnoreCase(false);
		rule.setPropRules(props);
		rules.add(rule);
		
		List<PropRule> commonPropRules = new ArrayList<>();
		RegexPropRule regexProp = new RegexPropRule();
		regexProp.setRegex("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3})\\s");
		regexProp.setGroupIndex(1);
		regexProp.setName("logtime");
		commonPropRules.add(regexProp);
		
		ExtracteConfig config = new ExtracteConfig();
		String path = ParserTest.class.getClassLoader().getResource("log").getFile();
		config.setPath(path);
		config.setOutputPath(path);
		config.setFilter("*.log");
		config.setRules(rules);
		config.setCommonPropRules(commonPropRules);
		
		// Output-Rule
		List<OutputRule> outputs = new ArrayList<>();
		OutputRule outputRule = new OutputRule("sendCmd");
		outputs.add(outputRule);
		
		outputRule = new OutputRule("executed");
		outputs.add(outputRule);
		
		outputRule = new OutputRule("sendCmd-join", "sendCmd");
		Map<String, String> mainRuleFieldAlias = new HashMap<>();
		mainRuleFieldAlias.put("logtime", "time");
		
		outputRule.setMainRuleFieldAlias(mainRuleFieldAlias);
		List<JoinRule> joinRules = new LinkedList<>();
		JoinRule e = new JoinRule();
		e.setJoinRuleName("executed");
		Map<String, String> keyMapping = new HashMap<>();
		keyMapping.put("id", "id");
		e.setKeyMapping(keyMapping);
		Map<String, String> fieldAlias = new HashMap<>();
		fieldAlias.put("total", "totalCost");
		e.setFieldAlias(fieldAlias);
		joinRules.add(e);
		outputRule.setJoinRules(joinRules);
		List<String> fields = Arrays.asList("id", "time", "cmd", "totalCost", "recv");
		outputRule.setFields(fields);
		
		outputs.add(outputRule);
		config.setOutput(outputs);
		return config;
	}
	
	public void parse(ExtracteConfig config) throws Exception {
		LogExtractor parser = new LogExtractor(config);
		ParseResult result = parser.parse();
		parser.writeResult(result);

		DataSet dataSet = new OutputHandler(result, config).handle();
		for (DataTable table : dataSet.getTables()) {
			System.out.println("----------" + table.getName() + "-----------");
			table.getFields().forEach(f-> {
				System.out.print(f.getName() + "\t");
			});
			int size = table.getFields().size();
			System.out.println();
			table.getValues().forEach(row -> {
				for (int i = 0; i < size; i++) {
					System.out.print(row.getValue(i) + "\t");
				}
				System.out.println();
			});
		}
	}

	@Test
	public void test03() throws Exception {
		ExtracteConfig config = getConfig();
		System.out.println(JsonUtil.toFormatJson(config));
	}
	
	@Test
	public void test01() throws Exception {
		parse(getConfig());
	}
	
	@Test
	public void test02() throws Exception {
		ExtracteConfig config = getConfigFromFile();
		System.out.println(JsonUtil.toFormatJson(config));
		parse(config);
	}
	
	public ExtracteConfig getConfigFromFile() {
		String file = ParserTest.class.getClassLoader().getResource("config.json").getFile();
		try {
			String json = FileUtil.getAllContent(file, "UTF-8");
			return JsonUtil.fromJson(json, ExtracteConfig.class);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
