package com.dajia.service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.dajia.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebAppConfiguration
public class ServiceTests {

	@Autowired
	private ApiService apiService;

	@Autowired
	private ProductService productService;

	@Autowired
	private SmsService smsService;

	// @Test
	public void testApiService() throws Exception {
		apiService.loadApiWdToken();
	}

	public void testProductService() {
		productService.syncProductsAll();
		productService.updateProductPrice(1L, new BigDecimal("0.05"));
	}

	@Test
	public void testSmsService() {
		smsService.sendSignupMessage("13900000000", false);
	}
}
