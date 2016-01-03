package com.dajia.service;

import java.math.BigDecimal;

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

	// @Test
	public void testApiService() throws Exception {
		apiService.loadApiToken();
	}

	@Test
	public void testProductService() {
		productService.syncProductsAll();
		// productService.updateProductPrice(1L, new BigDecimal("0.05"));
	}
}
