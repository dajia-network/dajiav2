package com.dajia.util;

public class ApiUtils {
	public static final String schema = "http";
	public static final String schema_https = "https";
	public static final String domain = "api.vdian.com";
	public static final String path_api = "/api";
	public static final String path_token = "/token";
	public static final String appkey = "appkey";
	public static final String secret = "secret";
	public static final String token = "token";

	public static final int code_success = 0;
	public static final int code_token_error = 10016;

	public static String allProductsUrl() {
		String path = schema + "://" + domain + path_api + "?param={paramStr}&public={publicStr}";
		return path;
	}

	public static String allProductsParamStr() {
		String paramStr = "{\"page_num\":1,\"page_size\":20,\"orderby\":1}";
		return paramStr;
	}

	public static String allProductsPublicStr(String token) {
		String publicStr = "{\"method\":\"vdian.item.list.get\",\"access_token\":\"" + token
				+ "\",\"version\":\"1.0\",\"format\":\"json\"}";
		return publicStr;
	}

	public static String productUrl() {
		String path = schema + "://" + domain + path_api + "?param={paramStr}&public={publicStr}";
		return path;
	}

	public static String productParamStr(String refId) {
		String paramStr = "{\"itemid\":\"" + refId + "\"}";
		return paramStr;
	}

	public static String productPublicStr(String token) {
		String publicStr = "{\"method\":\"vdian.item.get\",\"access_token\":\"" + token
				+ "\",\"version\":\"1.0\",\"format\":\"json\"}";
		return publicStr;
	}

	public static String testTokenUrl() {
		return schema + "://" + domain + path_api + "?param={}&public={publicStr}";
	}

	public static String testTokenPublicStr(String token) {
		String publicStr = "{\"method\":\"vdian.shop.cate.get\",\"access_token\":\"" + token
				+ "\",\"version\":\"1.0\",\"format\":\"json\"}";
		return publicStr;
	}

	public static String generateTokenUrl(String appkey, String secret) {
		String path = schema_https + "://" + domain + path_token + "?grant_type=client_credential&appkey=" + appkey
				+ "&secret=" + secret;
		return path;
	}
}