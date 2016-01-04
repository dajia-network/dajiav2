package com.dajia.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.dajia.domain.Product;

public class ApiKdtUtils {
	public static final String schema = "http";
	public static final String schema_https = "https";
	public static final String domain = "open.koudaitong.com";
	public static final String path_api = "/api/entry";
	public static final String appkey = "appkey_kdt";
	public static final String secret = "secret_kdt";
	
	public static final String method_get_item = "kdt.item.get";
	public static final String method_get_onsale_items = "kdt.items.onsale.get";
	
	public static Product productMapper(Map itemMap) {
		Product product = new Product();
		product.refId = (String) itemMap.get("itemid");
		product.name = (String) itemMap.get("item_name");
		product.description = (String) itemMap.get("item_desc");
		product.stock = ((Integer) itemMap.get("stock")).longValue();
		product.sold = ((Integer) itemMap.get("sold")).longValue();
		product.currentPrice = new BigDecimal((String) itemMap.get("price"));
		product.productImagesExt = (List<String>) itemMap.get("imgs");
		product.productImagesThumbExt = (List<String>) itemMap.get("thumb_imgs");
		return product;
	}
}