package com.dajia.service;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajia.domain.Product;
import com.dajia.domain.User;
import com.dajia.domain.UserOrder;
import com.dajia.repository.ProductRepo;
import com.dajia.repository.UserContactRepo;
import com.dajia.repository.UserOrderRepo;
import com.dajia.repository.UserRepo;
import com.dajia.util.CommonUtils;
import com.dajia.util.CommonUtils.ActiveStatus;
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
	private UserRepo userRepo;

	@Autowired
	private UserContactRepo userContactRepo;

	@Autowired
	private ProductService productService;

	@Autowired
	private RewardService rewardService;

	@Transactional
	public UserOrder generateRobotOrder(Long productId, Integer quantity) {
		Product product = productRepo.findOne(productId);
		UserOrder order = new UserOrder();
		order.orderStatus = OrderStatus.PENDING_PAY.getKey();
		order.orderDate = new Date();
		order.quantity = quantity;
		order.unitPrice = product.currentPrice;
		order.totalPrice = product.currentPrice.multiply(new BigDecimal(quantity));
		order.productId = productId;

		order.userId = 0L;
		order.paymentId = 0L;
		order.payType = 0;
		order.contactName = "";
		order.contactMobile = "";
		order.address = "";
		orderRepo.save(order);
		productService.productSold(order);
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
		ov.userId = order.userId;
		ov.trackingId = order.trackingId;
		ov.productId = order.productId;
		ov.quantity = order.quantity;
		ov.orderDate = order.orderDate;
		ov.unitPrice = order.unitPrice;
		ov.totalPrice = order.totalPrice;
		ov.logisticAgent = order.logisticAgent;
		ov.logisticTrackingId = order.logisticTrackingId;
		ov.contactName = order.contactName;
		ov.contactMobile = order.contactMobile;
		ov.address = order.address;
		ov.comments = order.comments;
		ov.userComments = order.userComments;
		ov.orderStatus = order.orderStatus;
		ov.orderStatus4Show = this.getOrderStatusStr(order.orderStatus);
		return ov;
	}

	public Page<UserOrder> loadOrdersByPage(Integer pageNum, String filter) {
		Pageable pageable = new PageRequest(pageNum - 1, CommonUtils.page_item_perpage);
		Page<UserOrder> orders = null;
		if (null == filter) {
			filter = "real";
		}
		if (filter.equals("all")) {
			orders = orderRepo.findByIsActiveOrderByOrderDateDesc(ActiveStatus.YES.toString(), pageable);
		} else if (filter.equals("real")) {
			orders = orderRepo
					.findByUserIdNotAndIsActiveOrderByOrderDateDesc(0L, ActiveStatus.YES.toString(), pageable);
		}
		for (UserOrder order : orders) {
			order.orderStatus4Show = getOrderStatusStr(order.orderStatus);
			Product p = productService.loadProductDetail(order.productId);
			if (null != p) {
				order.productInfo4Show = p.name;
			}
			User u = userRepo.findByUserId(order.userId);
			if (null != u) {
				order.userInfo4Show = u.userName;
			}
		}
		return orders;
	}

	public void fillOrderVO(OrderVO ov, UserOrder order) {
		ov.product = productService.loadProductDetail(order.productId);
		if (null != ov.product) {
			ov.product.priceOff = ov.product.originalPrice.add(ov.product.currentPrice.negate());
		}
		User user = userRepo.findByUserId(order.userId);
		if (null != user) {
			ov.userName = user.userName;
		}
		ov.refUsers = rewardService.getRefUsers(ov.orderId).values();
	}
}
