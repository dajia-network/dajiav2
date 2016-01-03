package com.dajia.util;

import java.lang.reflect.Field;

public class CommonUtils {
	public static final String is_active_y = "Y";
	public static final String is_active_n = "N";

	public static void copyProperties(Object src, Object target) throws IllegalArgumentException,
			IllegalAccessException {
		// BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
		Field[] fields = src.getClass().getFields();
		for (Field field : fields) {
			if (null != field.get(src)) {
				field.set(target, field.get(src));
			}
		}
	}
}