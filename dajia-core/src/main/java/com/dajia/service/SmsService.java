package com.dajia.service;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Service;

import com.dajia.repository.PropertyRepo;
import com.dajia.util.ApiAlibabaUtils;
import com.dajia.util.CommonUtils;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

@Service
public class SmsService {
	Logger logger = LoggerFactory.getLogger(SmsService.class);

	@Autowired
	PropertyRepo propertyRepo;

	@Autowired
	EhCacheCacheManager ehcacheManager;

	public String sendSignupMessage(String mobile, boolean allowSend) {
		String signupCode = CommonUtils.genRandomNum(4);
		// put signup_code into cache;
		if (null == ehcacheManager.getCacheManager().getCache(CommonUtils.cache_name_signup_code)) {
			ehcacheManager.getCacheManager().addCache(CommonUtils.cache_name_signup_code);
		}
		Cache cache = ehcacheManager.getCacheManager().getCache(CommonUtils.cache_name_signup_code);
		cache.put(new Element(mobile, signupCode));

		return sendSmsMessage(mobile, ApiAlibabaUtils.sms_free_sign_name_signup, ApiAlibabaUtils.sms_template_signup,
				signupCode, allowSend);
	}

	public String sendSigninMessage(String mobile, boolean allowSend) {
		String signinCode = CommonUtils.genRandomNum(4);
		// put signup_code into cache;
		if (null == ehcacheManager.getCacheManager().getCache(CommonUtils.cache_name_signin_code)) {
			ehcacheManager.getCacheManager().addCache(CommonUtils.cache_name_signin_code);
		}
		Cache cache = ehcacheManager.getCacheManager().getCache(CommonUtils.cache_name_signin_code);
		cache.put(new Element(mobile, signinCode));

		return sendSmsMessage(mobile, ApiAlibabaUtils.sms_free_sign_name_signin, ApiAlibabaUtils.sms_template_signin,
				signinCode, allowSend);
	}

	public String sendBindingMessage(String mobile, boolean allowSend) {
		String bindingCode = CommonUtils.genRandomNum(4);
		// put signup_code into cache;
		if (null == ehcacheManager.getCacheManager().getCache(CommonUtils.cache_name_binding_code)) {
			ehcacheManager.getCacheManager().addCache(CommonUtils.cache_name_binding_code);
		}
		Cache cache = ehcacheManager.getCacheManager().getCache(CommonUtils.cache_name_binding_code);
		cache.put(new Element(mobile, bindingCode));

		return sendSmsMessage(mobile, ApiAlibabaUtils.sms_free_sign_name_binding, ApiAlibabaUtils.sms_template_binding,
				bindingCode, allowSend);
	}

	private String sendSmsMessage(String mobile, String signName, String templateCode, String codeStr, boolean allowSend) {
		String returnStatus = CommonUtils.return_val_failed;
		String url = ApiAlibabaUtils.sms_server_url;
		String appkey = propertyRepo.findByPropertyKey(ApiAlibabaUtils.sms_app_key).propertyValue;
		String secret = propertyRepo.findByPropertyKey(ApiAlibabaUtils.sms_app_secret).propertyValue;

		TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		req.setRecNum(mobile);
		req.setSmsType(ApiAlibabaUtils.sms_type);
		req.setSmsFreeSignName(signName);
		req.setSmsParam("{\"code\":\"" + codeStr + "\",\"product\":\"打价网\"}");
		req.setSmsTemplateCode(templateCode);
		logger.info("Sending msg to mobile: " + mobile + " / Code: " + codeStr);
		try {
			if (allowSend) {
				AlibabaAliqinFcSmsNumSendResponse response = client.execute(req);
				String returnJsonStr = response.getBody();
				if (returnJsonStr.indexOf("\"err_code\":\"0\"") >= 0) {
					returnStatus = CommonUtils.return_val_success;
				}
			} else {
				returnStatus = CommonUtils.return_val_success;
			}
		} catch (ApiException e) {
			logger.error(e.getMessage(), e);
		}
		return returnStatus;
	}
}