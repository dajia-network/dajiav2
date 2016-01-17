package com.dajia.util;

import java.lang.reflect.Field;

import com.dajia.domain.Price;
import com.dajia.domain.Product;
import com.dajia.domain.ProductImage;

public class CommonUtils {

	public static Long beijing_city_key = 110100L;
	public static Long shanghai_city_key = 310100L;

	public static void copyProperties(Object src, Object target) throws IllegalArgumentException,
			IllegalAccessException {
		Field[] fields = src.getClass().getFields();
		for (Field field : fields) {
			if (null != field.get(src)) {
				field.set(target, field.get(src));
			}
		}
	}

	public static void copyProductProperties(Product src, Product target) {
		target.name = src.name;
		target.description = src.description;
		target.postFee = src.postFee;
		target.imgUrl = src.imgUrl;
		target.imgThumbUrl = src.imgThumbUrl;
		if (null != src.productImages && src.productImages.size() > 0) {
			for (ProductImage pi : src.productImages) {
				pi.product = target;
			}
			target.productImages.clear();
			target.productImages.addAll(src.productImages);
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

	public enum ActiveStatus {
		YES("Y"), NO("N");
		private String key;

		private ActiveStatus(String key) {
			this.key = key;
		}

		@Override
		public String toString() {
			return String.valueOf(this.key);
		}
	}

	public enum LocationType {
		PROVINCE("province"), CITY("city"), AREA("area");
		private String key;

		private LocationType(String key) {
			this.key = key;
		}

		@Override
		public String toString() {
			return String.valueOf(this.key);
		}
	}

	public enum OrderStatus {
		PENDING_PAY(1, "待付款"), PAIED(2, "已付款"), DELEVERING(3, "已发货"), DELEVRIED(4, "已签收"), CLOSED(5, "已完成"), CANCELLED(
				6, "已取消");
		private Integer key;
		private String value;

		private OrderStatus(Integer key, String value) {
			this.key = key;
			this.value = value;
		}

		public Integer getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

	}
}