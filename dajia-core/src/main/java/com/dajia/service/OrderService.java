package com.dajia.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajia.domain.ProductItem;
import com.dajia.domain.User;
import com.dajia.domain.UserOrder;
import com.dajia.domain.UserRefund;
import com.dajia.domain.UserReward;
import com.dajia.repository.ProductItemRepo;
import com.dajia.repository.UserContactRepo;
import com.dajia.repository.UserOrderRepo;
import com.dajia.repository.UserRefundRepo;
import com.dajia.repository.UserRepo;
import com.dajia.repository.UserRewardRepo;
import com.dajia.util.CommonUtils;
import com.dajia.util.CommonUtils.ActiveStatus;
import com.dajia.util.CommonUtils.OrderStatus;
import com.dajia.vo.LoginUserVO;
import com.dajia.vo.OrderVO;
import com.dajia.vo.ProductVO;
import com.dajia.vo.ProgressVO;
import com.pingplusplus.exception.PingppException;

@Service
public class OrderService {
	Logger logger = LoggerFactory.getLogger(OrderService.class);

	@Autowired
	private UserOrderRepo orderRepo;

	@Autowired
	private ProductItemRepo productItemRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private UserContactRepo userContactRepo;

	@Autowired
	private UserRefundRepo refundRepo;

	@Autowired
	private UserRewardRepo rewardRepo;

	@Autowired
	private ProductService productService;

	@Autowired
	private RewardService rewardService;

	@Autowired
	private ApiService apiService;

	@Transactional
	public UserOrder generateRobotOrder(Long productId, Integer quantity) {
		ProductVO product = productService.loadProductDetail(productId);
		UserOrder order = new UserOrder();
		order.orderStatus = OrderStatus.PENDING_PAY.getKey();
		order.orderDate = new Date();
		order.quantity = quantity;
		order.unitPrice = product.currentPrice;
		order.totalPrice = product.currentPrice.multiply(new BigDecimal(quantity));
		order.productId = product.productId;
		order.productItemId = product.productItemId;

		order.userId = 0L;
		order.payType = 0;
		order.contactName = "";
		order.contactMobile = "";
		order.address = "";
		orderRepo.save(order);
		productService.productSold(order);
		return order;
	}

	public OrderVO convertOrderVO(UserOrder order) {
		OrderVO ov = new OrderVO();
		ov.orderId = order.orderId;
		ov.userId = order.userId;
		ov.trackingId = order.trackingId;
		ov.productId = order.productId;
		ov.productItemId = order.productItemId;
		ov.quantity = order.quantity;
		ov.orderDate = order.orderDate;
		ov.unitPrice = order.unitPrice;
		ov.totalPrice = order.totalPrice;
		ov.postFee = order.postFee;
		ov.logisticAgent = order.logisticAgent;
		ov.logisticTrackingId = order.logisticTrackingId;
		ov.contactName = order.contactName;
		ov.contactMobile = order.contactMobile;
		ov.address = order.address;
		ov.comments = order.comments;
		ov.userComments = order.userComments;
		ov.adminComments = order.adminComments;
		ov.orderStatus = order.orderStatus;
		ov.orderStatus4Show = CommonUtils.getOrderStatusStr(order.orderStatus);
		ov.logisticAgent4Show = CommonUtils.getLogisticAgentStr(order.logisticAgent);
		ov.payType4Show = CommonUtils.getPayTypeStr(order.payType);
		return ov;
	}

	public Page<UserOrder> loadOrdersByUserIdByPage(Long userId, List<Integer> orderStatusList, Integer pageNum) {
		Pageable pageable = new PageRequest(pageNum - 1, CommonUtils.page_item_perpage_5);
		Page<UserOrder> orders = orderRepo.findByUserIdAndOrderStatusInOrderByOrderDateDesc(userId, orderStatusList,
				pageable);
		return orders;
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
		return orders;
	}

	public void fillOrderVO(OrderVO ov, UserOrder order) {
		ov.productVO = productService.loadProductDetail(ov.productId);
		User user = userRepo.findByUserId(order.userId);
		if (null != user) {
			ov.userName = user.userName;
		}
		if (null != ov.productVO) {
			ov.productInfo4Show = ov.productVO.name;
			ov.rewardValue = rewardService.calculateRewards(ov.orderId, ov.productVO);
			ov.refundValue = calculateRefundValue(ov.productVO.currentPrice, order);
		}
	}

	private BigDecimal calculateRefundValue(BigDecimal currentPrice, UserOrder userOrder) {
		if (null == currentPrice || null == userOrder) {
			return null;
		}
		return userOrder.unitPrice.add(currentPrice.negate()).multiply(new BigDecimal(userOrder.quantity));
	}

	public void orderRefund(ProductItem productItem) {
		List<UserOrder> orderList = orderRepo.findByProductItemIdAndIsActiveOrderByOrderDateDesc(
				productItem.productItemId, CommonUtils.ActiveStatus.YES.toString());
		if (null != orderList) {
			for (UserOrder userOrder : orderList) {
				if (null != userOrder.paymentId && !userOrder.paymentId.isEmpty()) {
					List<UserRefund> refunds = refundRepo.findByOrderIdAndRefundTypeAndIsActive(userOrder.orderId,
							CommonUtils.RefundType.REFUND.getKey(), CommonUtils.ActiveStatus.YES.toString());
					// one order one refund only
					if (refunds.isEmpty()) {
						BigDecimal refundValue = calculateRefundValue(productItem.currentPrice, userOrder);
						if (refundValue.compareTo(new BigDecimal(0)) <= 0) {
							try {
								apiService
										.applyRefund(userOrder.paymentId, refundValue, CommonUtils.refund_type_refund);
								logger.info("order " + userOrder.trackingId + " refund applied for "
										+ refundValue.doubleValue());
							} catch (PingppException e) {
								logger.error(e.getMessage(), e);
							}
						}
					}
				}
			}
		}
	}

	public OrderVO getOrderDetailByTrackingId(String trackingId) {
		UserOrder order = orderRepo.findByTrackingId(trackingId);
		if (null == order) {
			return null;
		}
		OrderVO ov = this.convertOrderVO(order);
		this.fillOrderVO(ov, order);
		return ov;
	}

	public OrderVO getOrderDetailByTrackingId4Progress(String trackingId) {
		OrderVO ov = this.getOrderDetailByTrackingId(trackingId);
		Map<Long, LoginUserVO> rewardSrcUserMap = rewardService.getRewardSrcUsers(ov.orderId);
		ov.rewardSrcUsers = rewardSrcUserMap.values();
		ProductItem productItem = productItemRepo.findOne(ov.productItemId);

		List<ProgressVO> progressList = new ArrayList<ProgressVO>();

		List<Integer> orderStatusList = new ArrayList<Integer>();
		orderStatusList.add(CommonUtils.OrderStatus.PAIED.getKey());
		orderStatusList.add(CommonUtils.OrderStatus.DELEVERING.getKey());
		orderStatusList.add(CommonUtils.OrderStatus.DELEVRIED.getKey());
		List<UserOrder> orderList = orderRepo.findTop5ByProductItemIdAndOrderStatusInAndIsActiveOrderByOrderIdDesc(
				ov.productItemId, orderStatusList, CommonUtils.ActiveStatus.YES.toString());
		BigDecimal comparePrice = productItem.currentPrice;
		for (UserOrder userOrder : orderList) {
			ProgressVO pv = new ProgressVO();
			pv.progressType = CommonUtils.refund_type_refund;
			pv.orderId = ov.orderId;
			pv.productId = ov.productId;
			pv.productItemId = ov.productItemId;
			pv.orderDate = userOrder.orderDate;
			pv.orderQuantity = userOrder.quantity;
			pv.priceOff = userOrder.unitPrice.add(comparePrice.negate());
			comparePrice = userOrder.unitPrice;
			progressList.add(pv);
		}
		List<UserReward> rewardList = rewardRepo.findTop5ByRefOrderIdAndRewardStatusOrderByCreatedDateDesc(ov.orderId,
				CommonUtils.RewardStatus.PENDING.getKey());
		for (UserReward userReward : rewardList) {
			ProgressVO pv = new ProgressVO();
			pv.progressType = CommonUtils.refund_type_reward;
			pv.orderId = ov.orderId;
			pv.productItemId = ov.productItemId;
			pv.orderDate = userReward.createdDate;
			pv.orderUserName = rewardSrcUserMap.get(userReward.orderUserId).userName;
			progressList.add(pv);
		}
		Collections.sort(progressList, Collections.reverseOrder());
		ov.progressList = progressList;
		return ov;
	}

	public String generateOrderInfoStr(UserOrder order) {
		StringBuilder sb = new StringBuilder();
		sb.append("oid:");
		sb.append(order.trackingId);
		sb.append("|uid:");
		sb.append(order.userId);
		sb.append("|ptid:");
		sb.append(order.productItemId);
		sb.append("|cname:");
		sb.append(order.contactName);
		sb.append("|cmobile:");
		sb.append(order.contactMobile);
		return sb.toString();
	}
}
