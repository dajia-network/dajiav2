package com.dajia.util;

import java.util.Map;

import com.dajia.domain.User;

public class ApiWechatUtils {
	public static final String wechat_api_token = "dajia";
	public static final String wechat_get_token_url = "https://api.weixin.qq.com/sns/oauth2/access_token";
	public static final String wechat_get_userinfo_url = "https://api.weixin.qq.com/sns/userinfo";
	public static final String wechat_app_key = "appkey_wechat";
	public static final String wechat_secret = "secret_wechat";
	public static final String wechat_oauth_type = "Wechat";
	public static final String wechat_oauth_url = "https://open.weixin.qq.com/connect/oauth2/authorize";
	public static final String wechat_callback_url = "http%3A%2F%2F51daja.com%2Fwechatoauth";

	public static void updateWechatUserInfo(User user, Map<String, String> userInfoMap) {
		user.userName = userInfoMap.get("nickname");
		user.headImgUrl = userInfoMap.get("headimgurl");
		user.sex = String.valueOf(userInfoMap.get("sex"));
		user.country = userInfoMap.get("country");
		user.province = userInfoMap.get("province");
		user.city = userInfoMap.get("city");
	}

	public static String getOauthUrl(String appId) {
		String url = wechat_oauth_url + "?appid=" + appId + "&redirect_uri=" + wechat_callback_url
				+ "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
		return url;
	}
}