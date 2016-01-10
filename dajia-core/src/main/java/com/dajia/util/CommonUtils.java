package com.dajia.util;

import java.lang.reflect.Field;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.dajia.domain.Product;

public class CommonUtils {
	public static final String is_active_y = "Y";
	public static final String is_active_n = "N";

	public static void copyProperties(Object src, Object target) throws IllegalArgumentException,
			IllegalAccessException {
		Field[] fields = src.getClass().getFields();
		for (Field field : fields) {
			if (null != field.get(src)) {
				field.set(target, field.get(src));
			}
		}
	}

	public static void updateProductWithReq(Product persist, Product req) {
		if (null != req.brief) {
			persist.brief = req.brief;
		}
		if (null != req.stock) {
			persist.stock = req.stock;
		}
		if (null != req.startDate) {
			persist.startDate = req.startDate;
		}
		if (null != req.expiredDate) {
			persist.expiredDate = req.expiredDate;
		}
	}
}