package com.dajia.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dajia.domain.User;
import com.dajia.domain.UserContact;
import com.dajia.domain.UserOrder;
import com.dajia.repository.UserContactRepo;
import com.dajia.repository.UserOrderRepo;
import com.dajia.repository.UserRepo;
import com.dajia.service.OrderService;
import com.dajia.service.ProductService;
import com.dajia.service.UserContactService;
import com.dajia.util.CommonUtils;
import com.dajia.vo.OrderVO;

@RestController
public class OrderController extends BaseController {
	Logger logger = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private UserOrderRepo orderRepo;

	@Autowired
	private UserContactRepo userContactRepo;

	@Autowired
	private ProductService productService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private UserContactService userContactService;

	@RequestMapping("/robotorder/{pid}")
	public @ResponseBody UserOrder robotOrder(@PathVariable("pid") Long pid) {
		UserOrder order = orderService.generateRobotOrder(pid, 1);
		return order;
	}

	@RequestMapping(value = "/user/submitOrder", method = RequestMethod.POST)
	public UserOrder submitOrder(HttpServletRequest request, HttpServletResponse response, @RequestBody OrderVO orderVO) {
		User user = this.getLoginUser(request, response, userRepo);
		UserContact uc = orderVO.userContact;
		if (null != uc) {
			uc = userContactService.updateUserContact(uc, user);
		}

		UserOrder order = new UserOrder();
		order.unitPrice = orderVO.unitPrice;
		order.totalPrice = orderVO.totalPrice;
		order.quantity = orderVO.quantity;
		order.productId = orderVO.productId;
		order.orderDate = new Date();
		order.orderStatus = CommonUtils.OrderStatus.PAIED.getKey();
		order.userId = user.userId;
		order.contactId = uc.contactId;
		order.paymentId = 0L;
		orderRepo.save(order);

		// need to be moved to payment logic
		productService.productSold(order.productId, order.quantity);

		return order;
	}

	@RequestMapping("/user/progress")
	public List<OrderVO> myProgress(HttpServletRequest request, HttpServletResponse response) {
		User user = this.getLoginUser(request, response, userRepo);
		List<UserOrder> orders = orderRepo.findByUserIdOrderByOrderDateDesc(user.userId);
		List<OrderVO> progressList = new ArrayList<OrderVO>();
		for (UserOrder order : orders) {
			OrderVO ov = orderService.convertOrderVO(order);
			ov.product = productService.loadProductDetail(order.productId);
			if (null != ov.product) {
				ov.product.priceOff = ov.product.originalPrice.add(ov.product.currentPrice.negate());
			}
			progressList.add(ov);
		}
		return progressList;
	}

	@RequestMapping("/user/order/{oid}")
	public OrderVO orderDetail(@PathVariable("oid") Long oid) {
		UserOrder order = orderRepo.findOne(oid);
		if (null == order) {
			return null;
		}
		OrderVO ov = orderService.convertOrderVO(order);
		ov.product = productService.loadProductDetail(order.productId);
		if (null != ov.product) {
			ov.product.priceOff = ov.product.originalPrice.add(ov.product.currentPrice.negate());
		}
		ov.userContact = userContactRepo.findOne(order.contactId);
		return ov;
	}
}
