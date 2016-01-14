package com.dajia.util;

import java.lang.reflect.Field;

import com.dajia.domain.Price;
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
		if (null != req.buyQuota) {
			persist.buyQuota = req.buyQuota;
		}
		if (null != req.startDate) {
			persist.startDate = req.startDate;
		}
		if (null != req.expiredDate) {
			persist.expiredDate = req.expiredDate;
		}
		if (null != req.originalPrice) {
			persist.originalPrice = req.originalPrice;
		}
		if (null != req.originalPrice) {
			persist.currentPrice = req.originalPrice;
		}
		if (null != req.prices && req.prices.size() > 0) {
			if (null != persist.prices) {
				for (Price price : req.prices) {
					price.product = persist;
				}
				persist.prices.clear();
				persist.prices.addAll(req.prices);
			}
		}
	}

	public static long getLongValue(Long input) {
		return null != input ? input.longValue() : 0L;
	}
}