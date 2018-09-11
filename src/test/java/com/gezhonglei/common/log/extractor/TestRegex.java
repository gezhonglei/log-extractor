package com.gezhonglei.common.log.extractor;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.gezhonglei.common.log.extractor.config.ParseMode;

public class TestRegex {
	
	@Test
	public void test01() {
		Pattern pattern = Pattern.compile("cmd=(con|disc)");

		String value = "cmd=disc";
		Matcher matcher = pattern.matcher(value);
		assertEquals(true, matcher.matches());
		assertEquals(false, matcher.find());
		
		value = " SendRequest: cmd=con, ";
		matcher = pattern.matcher(value);
		assertEquals(false, matcher.matches());
		assertEquals(true, matcher.find());
		
		int groupCount = matcher.groupCount();
		for (int i = 0; i <= groupCount; i++) {
			System.out.println(matcher.group(i));
		}
	}
	
	@Test
	public void test02() {
		System.out.println(ParseMode.Boundary);
		String name = "boundary";
//		System.out.println(ParseMode.valueOf(name));
//		System.out.println(Enum.valueOf(ParseMode.class, name));
		
	}
}
