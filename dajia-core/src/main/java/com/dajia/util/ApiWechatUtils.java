package com.dajia.util;

import java.util.Map;

import com.dajia.domain.User;

public class ApiWechatUtils {
	public static final String wechat_api_token = "dajia";
	public static final String wechat_get_token_url = "https://api.weixin.qq.com/sns/oauth2/access_token";
	public static final String wechat_get_userinfo_url = "https://api.weixin.qq.com/sns/userinfo";
	public static final String wechat_get_access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";
	public static final String wechat_get_jsapi_ticket_url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";
	public static final String wechat_app_key = "appkey_wechat";
	public static final String wechat_secret = "secret_wechat";
	public static final String wechat_oauth_type = "Wechat";
	public static final String wechat_oauth_url = "https://open.weixin.qq.com/connect/oauth2/authorize";
	public static final String wechat_callback_url = "http%3A%2F%2F51daja.com%2Fwechatoauth";
	public static final String wechat_access_token_key = "access_token";
	public static final String wechat_jsapi_key = "ticket";

	public static void updateWechatUserInfo(User user, Map<String, String> userInfoMap) {
		user.userName = CommonUtils.stringCharsetConvert(userInfoMap.get("nickname"), "ISO-8859-1");
		user.headImgUrl = userInfoMap.get("headimgurl");
		user.sex = String.valueOf(userInfoMap.get("sex"));
		user.country = CommonUtils.stringCharsetConvert(userInfoMap.get("country"), "ISO-8859-1");
		user.province = CommonUtils.stringCharsetConvert(userInfoMap.get("province"), "ISO-8859-1");
		user.city = CommonUtils.stringCharsetConvert(userInfoMap.get("city"), "ISO-8859-1");
	}

	public static String getOauthUrl(String appId, String refUserId, String productId, String orderId) {
		String url = "";
		if (null != refUserId && !refUserId.isEmpty() && !refUserId.equalsIgnoreCase(CommonUtils.null_string)) {
			url = wechat_oauth_url + "?appid=" + appId + "&redirect_uri=" + wechat_callback_url
					+ "&response_type=code&scope=snsapi_userinfo&state=" + refUserId + "_" + productId + "_" + orderId
					+ "#wechat_redirect";
		} else {
			url = wechat_oauth_url + "?appid=" + appId + "&redirect_uri=" + wechat_callback_url
					+ "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
		}
		return url;
	}
}