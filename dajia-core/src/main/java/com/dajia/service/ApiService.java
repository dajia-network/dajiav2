package com.dajia.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dajia.repository.PropertyRepo;
import com.dajia.util.ApiUtils;

@Service
public class ApiService {
	Logger logger = LoggerFactory.getLogger(ApiService.class);

	@Autowired
	private PropertyRepo propertyRepo;

	public String getApiToken() {
		String token = "";
		String appkey = (propertyRepo.findByPropertyKey(ApiUtils.appkey)).propertyValue;
		System.out.println(appkey);
		return token;
	}
}
