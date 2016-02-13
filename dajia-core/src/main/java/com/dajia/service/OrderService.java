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
import com.dajia.util.CommonUtils.OrderStatus;
import com.dajia.vo.OrderVO;

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

	public String getOrderStatusStr(Integer key) {
		String returnStr = null;
		if (key.equals(OrderStatus.PENDING_PAY.getKey())) {
			returnStr = OrderStatus.PENDING_PAY.getValue();
		} else if (key.equals(OrderStatus.PAIED.getKey())) {
			returnStr = OrderStatus.PAIED.getValue();
		} else if (key.equals(OrderStatus.DELEVERING.getKey())) {
			returnStr = OrderStatus.DELEVERING.getValue();
		} else if (key.equals(OrderStatus.DELEVRIED.getKey())) {
			returnStr = OrderStatus.DELEVRIED.getValue();
		} else if (key.equals(OrderStatus.CLOSED.getKey())) {
			returnStr = OrderStatus.CLOSED.getValue();
		} else if (key.equals(OrderStatus.CANCELLED.getKey())) {
			returnStr = OrderStatus.CANCELLED.getValue();
		}
		return returnStr;
	}

	public OrderVO convertOrderVO(UserOrder order) {
		OrderVO ov = new OrderVO();
		ov.orderId = order.orderId;
		ov.productId = order.productId;
		ov.quantity = order.quantity;
		ov.orderDate = order.orderDate;
		ov.unitPrice = order.unitPrice;
		ov.totalPrice = order.totalPrice;
		ov.orderStatus4Show = this.getOrderStatusStr(order.orderStatus);
		return ov;
	}
}
