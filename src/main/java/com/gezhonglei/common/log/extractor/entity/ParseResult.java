package com.gezhonglei.common.log.extractor.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseResult {
	
	private Map<String, List<Entity>> props;

	public ParseResult() {
		props = new HashMap<>();
	}

	public void addEntity(String name, Entity entity) {
		List<Entity> entities = getEntities(name);
		entities.add(entity);
	}
	
	public List<Entity> getResult(String name) {
		List<Entity> entities = getEntities(name);
		return new ArrayList<>(entities);
	}
	
	private List<Entity> getEntities(String name) {
		List<Entity> entities = props.get(name);
		if(entities == null) {
			entities = new ArrayList<>();
			props.put(name, entities);
		}
		return entities;
	}
}
