package com.dajia.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dajia.domain.User;
import com.dajia.domain.UserContact;
import com.dajia.domain.UserOrder;
import com.dajia.repository.UserOrderRepo;
import com.dajia.repository.UserRepo;
import com.dajia.service.ApiService;
import com.dajia.service.OrderService;
import com.dajia.service.ProductService;
import com.dajia.service.UserContactService;
import com.dajia.util.CommonUtils;
import com.dajia.util.CommonUtils.OrderStatus;
import com.dajia.vo.OrderVO;
import com.pingplusplus.exception.PingppException;
import com.pingplusplus.model.Charge;
import com.pingplusplus.model.Event;
import com.pingplusplus.model.Webhooks;

@RestController
public class OrderController extends BaseController {
	Logger logger = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private UserOrderRepo orderRepo;

	@Autowired
	private ProductService productService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private UserContactService userContactService;

	@Autowired
	private ApiService apiService;

	@RequestMapping(value = "/user/submitOrder", method = RequestMethod.POST)
	public Charge submitOrder(HttpServletRequest request, HttpServletResponse response, @RequestBody OrderVO orderVO) {
		User user = this.getLoginUser(request, response, userRepo, true);
		UserContact uc = orderVO.userContact;
		if (null != uc) {
			uc = userContactService.updateUserContact(uc, user);
		}

		UserOrder order = new UserOrder();
		order.unitPrice = orderVO.unitPrice;
		order.totalPrice = orderVO.totalPrice;
		order.quantity = orderVO.quantity;
		order.payType = orderVO.payType;
		order.productId = orderVO.productId;
		order.orderDate = new Date();
		order.orderStatus = OrderStatus.PENDING_PAY.getKey();
		order.userId = user.userId;
		order.contactId = uc.contactId;
		order.paymentId = 0L;
		order.trackingId = CommonUtils.genTrackingId(user.userId);
		orderRepo.save(order);

		Charge charge = null;
		try {
			charge = apiService.getPingppCharge(order, CommonUtils.getPayTypeStr(order.payType), user.oauthUserId);
			order.pingxxCharge = charge.toString();
			orderRepo.save(order);
		} catch (PingppException e) {
			logger.error(e.getMessage(), e);
		}

		return charge;
	}

	@RequestMapping(value = "/webhooks", method = RequestMethod.POST)
	public void webhooks(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF8");
		// 获取头部所有信息
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			logger.info(key + " " + value);
		}
		// 获得 http body 内容
		BufferedReader reader = request.getReader();
		StringBuffer buffer = new StringBuffer();
		String string;
		while ((string = reader.readLine()) != null) {
			buffer.append(string);
		}
		reader.close();
		String eventString = buffer.toString();
		// 解析异步通知数据
		Event event = Webhooks.eventParse(eventString);
		if ("charge.succeeded".equals(event.getType())) {
			Object obj = Webhooks.getObject(eventString);
			if (obj instanceof Charge) {
				logger.info("webhooks 发送了 Charge");
				Charge charge = (Charge) obj;
				String trackingId = charge.getOrderNo();
				logger.info("付款状态：" + charge.getPaid() + " 订单号：" + trackingId);
				UserOrder order = orderRepo.findByTrackingId(trackingId);
				productService.productSold(order);
			}
			response.setStatus(200);
		} else if ("refund.succeeded".equals(event.getType())) {
			response.setStatus(200);
		} else {
			response.setStatus(500);
		}
	}

	@Deprecated
	@RequestMapping(value = "/user/createCharge", method = RequestMethod.POST)
	public Map<String, String> createCharge(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, String> map) {
		String trackingId = map.get("order_no");
		String channel = map.get("channel");
		String openId = "";
		UserOrder order = orderRepo.findByTrackingId(trackingId);
		if (null != order) {
			try {
				Charge charge = apiService.getPingppCharge(order, channel, openId);
				order.pingxxCharge = charge.toString();
				orderRepo.save(order);
			} catch (PingppException e) {
				logger.error(e.getMessage(), e);
			}
			return map;
		}
		return null;
	}

	@RequestMapping("/user/progress")
	public List<OrderVO> myProgress(HttpServletRequest request, HttpServletResponse response) {
		User user = this.getLoginUser(request, response, userRepo, true);
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
		orderService.fillOrderVO(ov, order);
		return ov;
	}
}
