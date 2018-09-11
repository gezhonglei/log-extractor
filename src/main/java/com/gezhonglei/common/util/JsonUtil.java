package com.gezhonglei.common.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * json字符串与实例对象之间的转换<br>
 * <b>注意：</b><br/>
 * Object => json:相应字段要有get方法；否则，无get方法的字段不会被序列成json对象属性。<br/>
 * json => Object:类要有空的构造函数，对应的字段要有set方法；否则，出错。
 * @author gezhonglei
 *
 */
public class JsonUtil {

	public static String toJson(Object obj) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		StringWriter sw = new StringWriter();
		JsonGenerator jsonGenerator = new JsonFactory().createJsonGenerator(sw);
		mapper.writeValue(jsonGenerator, obj);
		jsonGenerator.close();
		String json = sw.toString();
		sw.close();
		return json;
	}

	@SuppressWarnings("unchecked")
	public static <T> T fromJson(String json, Class<T> clazz) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Object obj = mapper.readValue(json, clazz);
		return (T)obj;
	}
	
	public static String toFormatJson(Object object) {
		String json = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
		}  catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}

	public static String format(String jsonStr) {
		return format(jsonStr, "\t");
	}
	
	public static String format(String jsonStr, String space) {
		if (null == jsonStr || "".equals(jsonStr))
			return "";
		if(space == null) {
			space = "\t";
		}
		
		StringBuilder sb = new StringBuilder();
		char last = '\0';
		char current = '\0';
		int indent = 0;
		boolean quota = false;
		for (int i = 0; i < jsonStr.length(); i++) {
			last = current;
			current = jsonStr.charAt(i);
			switch (current) {
			case ':':
				if(!quota) {
					sb.append(current).append(' ');
				}
				break;
			case '{':
			case '[':
				sb.append(current);
				if(!quota) {
					indent++;
					//sb.append('\n');
					//addIndentBlank(sb, space, indent);
				}
				break;
			case '}':
			case ']':
				if(!quota) {
					if((current == '}' && last == '{') || (current == ']' && last == '[')) {
						indent--;
						sb.append(current);
						break;
					}
					indent--;
					sb.append('\n');
					addIndentBlank(sb, space, indent);
				}
				sb.append(current);
				break;
			case ',':
				sb.append(current);
				if (!quota && last != '\\') {
					sb.append('\n');
					addIndentBlank(sb, space, indent);
				}
				break;
			default:
				if(!quota && (last == '{' || last == '[')) {
					sb.append('\n');
					addIndentBlank(sb, space, indent);
				} 
				if('"' == current) {
					quota = !quota;
				}
				sb.append(current);
			}
		}
		return sb.toString();
	}
	
	private static void addIndentBlank(StringBuilder sb, String space, int indent) {
		for (int i = 0; i < indent; i++) {
			sb.append(space);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception{
		List<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");
		String json = JsonUtil.toJson(list);
		System.out.println(json);
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("name", "zhonglei, ge");
		map.put("num", 12);
		//map.put("float", 12.01f);
		//map.put("list", list);
		//map.put("custom", new custom("ge",001));
		json = JsonUtil.toJson(map);
		System.out.println(JsonUtil.format(json, "  "));
		
		map = new HashMap<String,Object>();
		map = (Map<String, Object>) JsonUtil.fromJson(json, Map.class);
		System.out.println("Object:" + map);
	}
}
