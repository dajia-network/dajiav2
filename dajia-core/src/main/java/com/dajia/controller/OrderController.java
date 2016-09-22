package com.dajia.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dajia.domain.ProductItem;
import com.dajia.domain.User;
import com.dajia.domain.UserContact;
import com.dajia.domain.UserOrder;
import com.dajia.domain.UserOrderItem;
import com.dajia.domain.UserShare;
import com.dajia.repository.ProductItemRepo;
import com.dajia.repository.UserOrderRepo;
import com.dajia.repository.UserRepo;
import com.dajia.repository.UserShareRepo;
import com.dajia.service.ApiService;
import com.dajia.service.CartService;
import com.dajia.service.OrderService;
import com.dajia.service.ProductService;
import com.dajia.service.RefundService;
import com.dajia.service.RewardService;
import com.dajia.service.UserContactService;
import com.dajia.util.CommonUtils;
import com.dajia.util.CommonUtils.OrderStatus;
import com.dajia.vo.CartItemVO;
import com.dajia.vo.OrderVO;
import com.dajia.vo.PaginationVO;
import com.pingplusplus.exception.PingppException;
import com.pingplusplus.model.Charge;
import com.pingplusplus.model.Event;
import com.pingplusplus.model.Refund;
import com.pingplusplus.model.Webhooks;

@RestController
public class OrderController extends BaseController {

	/** Ping++ 回调函数的返回码 **/
	public static final int Ping_Plus_Code_Error = 500;
	public static final int Ping_Plus_Code_Success = 200;
	/** Ping++ 回调函数的事件类型: 退款成功事件 **/
	public static final String Ping_Plus_Event_Type_Refund_Succeed = "refund.succeeded";
	/** Ping++ 回调函数的事件类型: 付款成功事件 **/
	public static final String Ping_Plus_Event_Type_Charge_Succeed = "charge.succeeded";

	Logger logger = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private UserOrderRepo orderRepo;

	@Autowired
	private UserShareRepo userShareRepo;

	@Autowired
	private ProductItemRepo productItemRepo;

	@Autowired
	private ProductService productService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private UserContactService userContactService;

	@Autowired
	private ApiService apiService;

	@Autowired
	private RefundService refundService;

	@Autowired
	private RewardService rewardService;

	@Autowired
	private CartService cartService;

	@RequestMapping(value = "/user/submitOrder", method = RequestMethod.POST)
	public Charge submitOrder(HttpServletRequest request, HttpServletResponse response, @RequestBody OrderVO orderVO) {
		User user = this.getLoginUser(request, response, userRepo, true);
		UserContact uc = orderVO.userContact;
		if (null != uc) {
			uc = userContactService.updateUserContact(uc, user);
		}
		if (!productService.validateStock(orderVO)) {
			return null;
		}

		UserOrder order = new UserOrder();
		order.unitPrice = orderVO.unitPrice;
		order.totalPrice = orderVO.totalPrice;
		order.postFee = orderVO.postFee;
		order.quantity = orderVO.quantity;
		order.payType = orderVO.payType;
		order.productId = orderVO.productId;
		order.productItemId = orderVO.productItemId;
		order.productDesc = orderVO.productDesc;
		order.productShared = CommonUtils.ProductShared.NO.toString();
		order.userComments = orderVO.userComments;
		if (null != orderVO.refUserId && orderVO.refUserId.longValue() != user.userId.longValue()) {
			order.refUserId = orderVO.refUserId;
			order.refOrderId = orderVO.refOrderId;
		}
		order.orderDate = new Date();
		order.orderStatus = OrderStatus.PENDING_PAY.getKey();
		order.userId = user.userId;
		order.contactName = uc.contactName;
		order.contactMobile = uc.contactMobile;
		order.address = uc.province.locationValue + " " + uc.city.locationValue + " " + uc.district.locationValue + " "
				+ uc.address1;
		order.trackingId = CommonUtils.genTrackingId(user.userId);
		if (null != orderVO.cartItems) {
			List<UserOrderItem> orderItems = new ArrayList<UserOrderItem>();
			for (CartItemVO cartItem : orderVO.cartItems) {
				UserOrderItem oi = new UserOrderItem();
				oi.userOrder = order;
				oi.trackingId = order.trackingId;
				oi.userId = order.userId;
				oi.productId = cartItem.productId;
				oi.productItemId = cartItem.productItemId;
				oi.productShared = CommonUtils.ProductShared.NO.toString();
				oi.unitPrice = cartItem.currentPrice;
				oi.quantity = cartItem.quantity;
				orderItems.add(oi);
			}
			order.orderItems = orderItems;
		}
		if (!orderService.orderValidate(order)) {
			return null;
		}
		orderRepo.save(order);

		if (null != orderVO.cartItems) {
			for (CartItemVO cartItem : orderVO.cartItems) {
				cartService.removeFromCart(user.userId, cartItem.productId);
			}
		}

		Charge charge = null;
		try {
			charge = apiService.getPingppCharge(order, user, CommonUtils.getPayTypeStr(order.payType));
			order.pingxxCharge = charge.toString();
			order.paymentId = charge.getId();
			orderRepo.save(order);
		} catch (PingppException e) {
			logger.error(e.getMessage(), e);
		}
		return charge;
	}

	@RequestMapping(value = "/user/getCharge", method = RequestMethod.POST)
	public Charge getCharge(HttpServletRequest request, HttpServletResponse response, @RequestBody OrderVO orderVO) {
		User user = this.getLoginUser(request, response, userRepo, true);
		if (null == user) {
			return null;
		}
		UserOrder order = orderRepo.findOne(orderVO.orderId);
		if (null == order) {
			return null;
		}
		if (null != order.productItemId) {
			ProductItem pi = productItemRepo.findOne(order.productItemId);
			if (pi.productStatus != CommonUtils.ProductStatus.VALID.getKey() || pi.stock <= 0) {
				return null;
			}
		} else {
			for (UserOrderItem oi : order.orderItems) {
				ProductItem pi = productItemRepo.findOne(oi.productItemId);
				if (pi.productStatus != CommonUtils.ProductStatus.VALID.getKey() || pi.stock <= 0) {
					return null;
				}
			}
		}
		order.payType = orderVO.payType;
		orderRepo.save(order);

		Charge charge = null;
		try {
			charge = apiService.getPingppCharge(order.paymentId, user, CommonUtils.getPayTypeStr(order.payType));
		} catch (PingppException e) {
			logger.error(e.getMessage(), e);
		}
		return charge;
	}

	@RequestMapping(value = "/webhooks", method = RequestMethod.POST)
	public void webhooks(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// TODO 读取http header和body的公共方法

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

		if (Ping_Plus_Event_Type_Charge_Succeed.equals(event.getType())) {
			Object obj = Webhooks.getObject(eventString);
			if (obj instanceof Charge) {
				logger.info("webhooks 发送了 Charge");
				Charge charge = (Charge) obj;
				String trackingId = charge.getOrderNo();
				logger.info("付款状态：" + charge.getPaid() + " 订单号：" + trackingId);
				UserOrder order = orderRepo.findByTrackingId(trackingId);
				// order.paymentId = charge.getId();
				productService.productSold(order);
			}
			response.setStatus(Ping_Plus_Code_Success);

		} else if (Ping_Plus_Event_Type_Refund_Succeed.equals(event.getType())) {
			Object obj = Webhooks.getObject(eventString);

			if(null == obj) {
				logger.error("RefundCallback,N,webhooks.getObject is null,eventString={}", eventString);
				response.setStatus(Ping_Plus_Code_Error);
				return;
			}

			if(!(obj instanceof Refund)) {
				logger.error("RefundCallback,N,webhooks.getObject is not typeof Refund,eventString={}", eventString);
				response.setStatus(Ping_Plus_Code_Error);
				return;
			}

			Refund refund = (Refund) obj;
			String chargeId = refund.getCharge();
			Integer amount = refund.getAmount();
			String desc = refund.getDescription();

			String refundLog = String.format("refundId=%s, desc=%s, status=%s, chargeId=%s, value=%d",
					refund.getId(), desc, refund.getStatus(), chargeId, amount);

			/**
			 * 收到退款消息 保存一条退款记录到数据库 记录退款类型
			 */
			try {
				if (desc.equalsIgnoreCase(CommonUtils.refund_type_refund)) {
					refundService.createRefund(chargeId, new BigDecimal(new Double(amount) / 100),
							CommonUtils.RefundType.REFUND.getKey());
				} else if (desc.equalsIgnoreCase(CommonUtils.refund_type_reward)) {
					refundService.createRefund(chargeId, new BigDecimal(new Double(amount) / 100),
							CommonUtils.RefundType.REWARD.getKey());
				} else {
					refundService.createRefund(chargeId, new BigDecimal(new Double(amount) / 100),
							CommonUtils.RefundType.MANNUAL.getKey());
				}

				logger.info("RefundCallback,Y,{}", refundLog);
				response.setStatus(Ping_Plus_Code_Success);

			} catch (Exception ex) {
				logger.error("RefundCallback,N,save refund failed, {}", refundLog, ex);
				response.setStatus(Ping_Plus_Code_Error);
			}

		} else {
			logger.error("RefundCallback,N,unknown event type, eventString={}", eventString);
			response.setStatus(Ping_Plus_Code_Error);
		}
	}

	@RequestMapping("/user/progresses/{page}")
	public PaginationVO<OrderVO> myProgress(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("page") Integer pageNum) {
		User user = this.getLoginUser(request, response, userRepo, true);
		List<Integer> orderStatusList = new ArrayList<Integer>();
		orderStatusList.add(CommonUtils.OrderStatus.PAIED.getKey());
		orderStatusList.add(CommonUtils.OrderStatus.DELEVERING.getKey());
		orderStatusList.add(CommonUtils.OrderStatus.DELEVRIED.getKey());
		Page<UserOrder> orders = orderService.loadOrdersByUserIdByPage(user.userId, orderStatusList, pageNum);
		List<OrderVO> progressList = new ArrayList<OrderVO>();
		for (UserOrder order : orders) {
			OrderVO ov = orderService.convertOrderVO(order);
			if (null != order.productItemId) {
				ov.productVO = productService.loadProductDetailByItemId(order.productItemId);
				progressList.add(ov);
			} else {
				if (null != order.orderItems) {
					for (UserOrderItem orderItem : order.orderItems) {
						OrderVO orderItemVO = orderService.convertOrderVO(order);
						orderItemVO.orderItemId = orderItem.orderItemId;
						orderItemVO.unitPrice = orderItem.unitPrice;
						orderItemVO.quantity = orderItem.quantity;
						orderItemVO.productVO = productService.loadProductDetailByItemId(orderItem.productItemId);
						progressList.add(orderItemVO);
					}
				}
			}
			List<UserShare> userShares = userShareRepo.findByOrderIdAndProductItemIdAndShareTypeOrderByShareIdDesc(
					ov.orderId, ov.productItemId, CommonUtils.ShareType.BUY_SHARE.getKey());
			ov.userShares = userShares;
		}
		PaginationVO<OrderVO> page = CommonUtils.generatePaginationVO(orders, pageNum);
		page.results = progressList;
		return page;
	}

	@RequestMapping("/user/myorders/{page}")
	public PaginationVO<OrderVO> myOrders(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("page") Integer pageNum) {
		User user = this.getLoginUser(request, response, userRepo, true);
		List<Integer> orderStatusList = new ArrayList<Integer>();
		orderStatusList.add(CommonUtils.OrderStatus.PENDING_PAY.getKey());
		orderStatusList.add(CommonUtils.OrderStatus.PAIED.getKey());
		orderStatusList.add(CommonUtils.OrderStatus.DELEVERING.getKey());
		orderStatusList.add(CommonUtils.OrderStatus.DELEVRIED.getKey());
		orderStatusList.add(CommonUtils.OrderStatus.CLOSED.getKey());
		orderStatusList.add(CommonUtils.OrderStatus.CANCELLED.getKey());
		Page<UserOrder> orders = orderService.loadOrdersByUserIdByPage(user.userId, orderStatusList, pageNum);
		List<OrderVO> orderVoList = new ArrayList<OrderVO>();
		for (UserOrder order : orders) {
			OrderVO ov = orderService.convertOrderVO(order);
			if (null != order.productItemId) {
				ov.productVO = productService.loadProductDetailByItemId(order.productItemId);
			} else {
				ov.productVOList = productService.loadProducts4Order(order.orderItems);
			}
			orderVoList.add(ov);
		}
		PaginationVO<OrderVO> page = CommonUtils.generatePaginationVO(orders, pageNum);
		page.results = orderVoList;
		return page;
	}

	@RequestMapping("/user/order/{trackingId}")
	public OrderVO orderDetail(@PathVariable("trackingId") String trackingId) {
		OrderVO ov = orderService.getOrderDetailByTrackingId(trackingId);
		return ov;
	}

	@RequestMapping("/user/order/del/{trackingId}")
	public void delOrder(@PathVariable("trackingId") String trackingId) {
		UserOrder order = orderRepo.findByTrackingId(trackingId);
		if (null != order) {
			order.isActive = CommonUtils.ActiveStatus.NO.toString();
			orderRepo.save(order);
		}
	}

	@RequestMapping("/user/progress/{trackingId}/{orderItemId}")
	public OrderVO progressDetail(@PathVariable("trackingId") String trackingId,
			@PathVariable("orderItemId") Long orderItemId) {
		OrderVO ov = orderService.getOrderDetailByTrackingId4Progress(trackingId, orderItemId);
		return ov;
	}

	@RequestMapping(value = "/user/share")
	public void saveShareLog(HttpServletRequest request) {
		String orderId = request.getParameter("orderId");
		String productId = request.getParameter("productId");
		UserOrder order = orderRepo.findOne(Long.valueOf(orderId));
		if (null != order) {
			if (null != order.productItemId) {
				order.productShared = CommonUtils.ProductShared.YES.toString();
				orderRepo.save(order);
			} else {
				if (null != order.orderItems) {
					for (UserOrderItem oi : order.orderItems) {
						if (oi.productId.longValue() == Long.valueOf(productId)) {
							oi.productShared = CommonUtils.ProductShared.YES.toString();
						}
					}
					orderRepo.save(order);
				}
			}
		}
	}

	@RequestMapping("/product/share/{productId}/{refOrderId}")
	public OrderVO shareInfo4Product(@PathVariable("productId") Long productId,
			@PathVariable("refOrderId") Long refOrderId) {
		OrderVO ov = orderService.getRefOrderDetail(productId, refOrderId);
		return ov;
	}
}
