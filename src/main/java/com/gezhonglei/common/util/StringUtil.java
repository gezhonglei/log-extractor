package com.gezhonglei.common.util;

public class StringUtil {
	public static boolean isEmpty(String str) {
		return null == str || "".equals(str);
	}
	
	public static boolean isNonEmpty(String str) {
		return !isEmpty(str);
	}
}
