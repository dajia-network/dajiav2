package com.dajia.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.dajia.Application;
import com.dajia.domain.ProductItem;
import com.dajia.domain.UserOrder;

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

	@Autowired
	private RewardService rewardService;

	// @Test
	public void testApiService() throws Exception {
		apiService.loadApiWdToken();
	}

	public void testProductService() {
		productService.syncProductsAll();
	}

	@Test
	public void testSmsService() {
		smsService.sendSignupMessage("13900000000", false);
	}

	// @Test
	public void testRewardService() {
		UserOrder order = new UserOrder();
		order.userId = 1L;
		order.orderId = 705L;
		order.refUserId = 3L;
		order.productItemId = 68L;
		ProductItem productItem = new ProductItem();
		productItem.productItemId = 68L;
		rewardService.createReward(order, productItem);
	}
}
