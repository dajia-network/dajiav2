package com.dajia.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dajia.domain.Property;
import com.dajia.repository.PropertyRepo;
import com.dajia.util.ApiKdtUtils;
import com.dajia.util.ApiWdUtils;
import com.dajia.util.ApiWechatUtils;
import com.dajia.util.CommonUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdt.api.KdtApiClient;

@Service
public class ApiService {
	Logger logger = LoggerFactory.getLogger(ApiService.class);

	@Autowired
	private PropertyRepo propertyRepo;

	@Autowired
	EhCacheCacheManager ehcacheManager;

	public String loadApiWdToken() throws JsonParseException, JsonMappingException, IOException {
		String token = (propertyRepo.findByPropertyKey(ApiWdUtils.token)).propertyValue;
		boolean tokenValid = false;
		if (null != token && token.length() > 0) {
			String testTokenUrl = ApiWdUtils.testTokenUrl();
			String publicStr = ApiWdUtils.testTokenPublicStr(token);
			logger.info("testTokenUrl: " + testTokenUrl);
			RestTemplate restTemplate = new RestTemplate();
			String retrunJsonStr = restTemplate.getForObject(testTokenUrl, String.class, publicStr);
			logger.info("retrunJsonStr: " + retrunJsonStr);
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> map = new HashMap<String, Object>();
			map = mapper.readValue(retrunJsonStr, HashMap.class);
			Integer returnCode = Integer.valueOf(((Map) map.get("status")).get("status_code").toString());
			if (null != returnCode && returnCode == ApiWdUtils.code_success) {
				tokenValid = true;
			}

		}
		if (!tokenValid) {
			logger.info("access token is invalid...");
			String appkey = (propertyRepo.findByPropertyKey(ApiWdUtils.appkey)).propertyValue;
			String secret = (propertyRepo.findByPropertyKey(ApiWdUtils.secret)).propertyValue;
			String generateTokenUrl = ApiWdUtils.generateTokenUrl(appkey, secret);
			logger.info("generateTokenUrl: " + generateTokenUrl);
			RestTemplate restTemplate = new RestTemplate();
			String retrunJsonStr = restTemplate.getForObject(generateTokenUrl, String.class);
			logger.info("retrunJsonStr: " + retrunJsonStr);
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> map = new HashMap<String, Object>();
			map = mapper.readValue(retrunJsonStr, HashMap.class);
			Integer returnCode = Integer.valueOf(((Map) map.get("status")).get("status_code").toString());
			if (returnCode == 0) {
				String newToken = ((Map) map.get("result")).get("access_token").toString();
				logger.info("newToken: " + newToken);
				Property property = propertyRepo.findByPropertyKey(ApiWdUtils.token);
				property.propertyValue = newToken;
				propertyRepo.save(property);
				token = newToken;
			}
		}
		return token;
	}

	public String sendGet2Kdt(String method, HashMap<String, String> params) {
		String returnStr = "";
		KdtApiClient kdtApiClient;
		HttpResponse response;

		try {
			String appkey = (propertyRepo.findByPropertyKey(ApiKdtUtils.appkey)).propertyValue;
			String secret = (propertyRepo.findByPropertyKey(ApiKdtUtils.secret)).propertyValue;
			kdtApiClient = new KdtApiClient(appkey, secret);
			response = kdtApiClient.get(method, params);
			System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
					"UTF-8"));
			StringBuffer resultSb = new StringBuffer();
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				resultSb.append(line);
			}
			returnStr = resultSb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStr;
	}

	public Map<String, String> loadWechatUserInfo(String code) throws JsonParseException, JsonMappingException,
			IOException {
		String appkey = propertyRepo.findByPropertyKey(ApiWechatUtils.wechat_app_key).propertyValue;
		String secret = propertyRepo.findByPropertyKey(ApiWechatUtils.wechat_secret).propertyValue;
		String requestTokenUrl = ApiWechatUtils.wechat_get_token_url + "?appid=" + appkey + "&secret=" + secret
				+ "&code=" + code + "&grant_type=authorization_code";
		logger.info("request token url: " + requestTokenUrl);
		RestTemplate restTemplate = new RestTemplate();
		String retrunJsonStr = restTemplate.getForObject(requestTokenUrl, String.class);
		logger.info("request token result: " + retrunJsonStr);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		map = mapper.readValue(retrunJsonStr, HashMap.class);
		String accessToken = "";
		String openId = "";
		if (null != map && map.containsKey("access_token") && map.containsKey("openid")) {
			accessToken = map.get("access_token").toString();
			openId = map.get("openid").toString();
			if (!accessToken.isEmpty() && !openId.isEmpty()) {
				String requestUserInfoUrl = ApiWechatUtils.wechat_get_userinfo_url + "?access_token=" + accessToken
						+ "&openid=" + openId + "&lang=zh_CN";
				logger.info("request userInfo url: " + requestUserInfoUrl);
				retrunJsonStr = restTemplate.getForObject(requestUserInfoUrl, String.class);
				map = mapper.readValue(retrunJsonStr, HashMap.class);
				logger.info("request userInfo result: " + retrunJsonStr);
				return map;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public String getWechatOauthUrl() {
		String appkey = propertyRepo.findByPropertyKey(ApiWechatUtils.wechat_app_key).propertyValue;
		return ApiWechatUtils.getOauthUrl(appkey);
	}

	public String getWechatAccessToken() throws JsonParseException, JsonMappingException, IOException {
		String accessToken = null;
		if (null == ehcacheManager.getCacheManager().getCache(CommonUtils.global_cache_key)) {
			ehcacheManager.getCacheManager().addCache(CommonUtils.global_cache_key);
		}
		Cache cache = ehcacheManager.getCacheManager().getCache(CommonUtils.global_cache_key);
		if (null != cache.get(ApiWechatUtils.wechat_access_token_key)) {
			accessToken = cache.get(ApiWechatUtils.wechat_access_token_key).getObjectValue().toString();
		} else {
			String appkey = propertyRepo.findByPropertyKey(ApiWechatUtils.wechat_app_key).propertyValue;
			String secret = propertyRepo.findByPropertyKey(ApiWechatUtils.wechat_secret).propertyValue;
			String requestAccessTokenUrl = ApiWechatUtils.wechat_get_access_token_url + "&appid=" + appkey + "&secret="
					+ secret;
			logger.info("request access token url: " + requestAccessTokenUrl);
			RestTemplate restTemplate = new RestTemplate();
			String retrunJsonStr = restTemplate.getForObject(requestAccessTokenUrl, String.class);
			logger.info("request access token result: " + retrunJsonStr);
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> map = new HashMap<String, String>();
			map = mapper.readValue(retrunJsonStr, HashMap.class);
			accessToken = map.get(ApiWechatUtils.wechat_access_token_key);
			cache.put(new Element(ApiWechatUtils.wechat_access_token_key, accessToken));
		}
		return accessToken;
	}

	public String getWechatJsapiTicket() throws JsonParseException, JsonMappingException, IOException {
		String ticket = null;
		if (null == ehcacheManager.getCacheManager().getCache(CommonUtils.global_cache_key)) {
			ehcacheManager.getCacheManager().addCache(CommonUtils.global_cache_key);
		}
		Cache cache = ehcacheManager.getCacheManager().getCache(CommonUtils.global_cache_key);
		if (null != cache.get(ApiWechatUtils.wechat_jsapi_key)) {
			ticket = cache.get(ApiWechatUtils.wechat_jsapi_key).getObjectValue().toString();
		} else {
			String accessToken = getWechatAccessToken();
			String requestTicketUrl = ApiWechatUtils.wechat_get_jsapi_ticket_url + "?access_token=" + accessToken
					+ "&type=jsapi";
			logger.info("request ticket url: " + requestTicketUrl);
			RestTemplate restTemplate = new RestTemplate();
			String retrunJsonStr = restTemplate.getForObject(requestTicketUrl, String.class);
			logger.info("request ticket result: " + retrunJsonStr);
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> map = new HashMap<String, String>();
			map = mapper.readValue(retrunJsonStr, HashMap.class);
			ticket = map.get(ApiWechatUtils.wechat_jsapi_key);
			cache.put(new Element(ApiWechatUtils.wechat_jsapi_key, ticket));
		}
		return ticket;
	}

	public String getWechatSignature(String timestamp, String nonceStr, String url) {
		String ticket = null;
		try {
			ticket = getWechatJsapiTicket();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String str = "jsapi_ticket=" + ticket + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + url;
		logger.info("wechat signature str: " + str);
		return DigestUtils.sha1Hex(str);
	}
}
