package com.gezhonglei.common.log.extractor.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.gezhonglei.common.log.extractor.entity.Entity;

public class EntityIndex {
	private String name;
	private List<String> props;
	private Map<String, List<Entity>> indexToEntities;
	private JoinRule joinRule;

	public EntityIndex(String name, JoinRule joinRule, List<String> indexProps, List<Entity> entities) {
		this.name = name;
		this.joinRule = joinRule;
		this.props = indexProps;
		init(entities);
	}
	
	private void init(List<Entity> entities) {
		indexToEntities = new HashMap<>();
		entities.forEach(e-> {
			List<String> values = new LinkedList<>();
			props.forEach(prop -> {
				values.add(Optional.ofNullable(e.getPropValue(prop)).orElse("").toString());
			});
			String key = getIndexStr(values);
			List<Entity> list = indexToEntities.get(key);
			if(list == null) {
				list = new LinkedList<>();
				indexToEntities.put(key, list);
			}
			list.add(e);
		});
	}
	
	private String getIndexStr(List<String> indexValues) {
		// TODO: 构建索引简单实现，要考虑重复
		return String.join("$", indexValues);
	}
	
	public List<Entity> getEntityList(List<String> indexPropValues) {
		String key = getIndexStr(indexPropValues);
		return indexToEntities.get(key);
	}

	public String getName() {
		return name;
	}
	public List<String> getProps() {
		return props;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setProps(List<String> props) {
		this.props = props;
	}

	public JoinRule getJoinRule() {
		return joinRule;
	}

	public void setJoinRule(JoinRule joinRule) {
		this.joinRule = joinRule;
	}

	public List<String> getRefValue(Entity entity) {
		List<String> results = new ArrayList<>();
		joinRule.getKeyMapping().values().forEach(p-> {
			results.add(Optional.ofNullable(entity.getPropValue(p)).orElse("").toString());
		});
		return results;
	}
}
