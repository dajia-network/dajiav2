package com.dajia.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dajia.domain.Property;
import com.dajia.repository.PropertyRepo;
import com.dajia.util.ApiKdtUtils;
import com.dajia.util.ApiWdUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdt.api.KdtApiClient;

@Service
public class ApiService {
	Logger logger = LoggerFactory.getLogger(ApiService.class);

	@Autowired
	private PropertyRepo propertyRepo;

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
}
