package com.dajia.service;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajia.domain.Product;
import com.dajia.domain.UserOrder;
import com.dajia.repository.ProductRepo;
import com.dajia.repository.UserOrderRepo;
import com.dajia.util.CommonUtils;

@Service
public class OrderService {
	Logger logger = LoggerFactory.getLogger(OrderService.class);

	@Autowired
	private ProductRepo productRepo;

	@Autowired
	private UserOrderRepo orderRepo;

	@Autowired
	private ProductService productService;

	@Transactional
	public UserOrder generateRobotOrder(Long productId, Integer quantity) {
		Product product = productRepo.findOne(productId);
		UserOrder order = new UserOrder();
		order.orderStatus = CommonUtils.OrderStatus.PAIED.getKey();
		order.orderDate = new Date();
		order.quantity = quantity;
		order.unitPrice = product.currentPrice;
		order.totalPrice = product.currentPrice.multiply(new BigDecimal(quantity));
		order.productId = productId;

		order.userId = 0L;
		order.contactId = 0L;
		order.paymentId = 0L;
		order.payType = 0;
		orderRepo.save(order);
		productService.productSold(productId, quantity);
		return order;
	}
}
