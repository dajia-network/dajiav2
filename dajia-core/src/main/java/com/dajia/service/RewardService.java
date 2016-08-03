package com.dajia.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dajia.domain.ProductItem;
import com.dajia.domain.User;
import com.dajia.domain.UserOrder;
import com.dajia.domain.UserOrderItem;
import com.dajia.domain.UserReward;
import com.dajia.repository.ProductItemRepo;
import com.dajia.repository.UserOrderRepo;
import com.dajia.repository.UserRepo;
import com.dajia.repository.UserRewardRepo;
import com.dajia.util.CommonUtils;
import com.dajia.vo.LoginUserVO;
import com.dajia.vo.ProductVO;
import com.pingplusplus.exception.PingppException;

@Service
public class RewardService {
	Logger logger = LoggerFactory.getLogger(RewardService.class);

	@Autowired
	private UserRewardRepo rewardRepo;

	@Autowired
	private UserOrderRepo orderRepo;

	@Autowired
	private ProductItemRepo productItemRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private ApiService apiService;

	@Autowired
	private OrderService orderService;

	public Map<Long, LoginUserVO> getRewardSrcUsers(Long orderId, Long productItemId) {
		Map<Long, LoginUserVO> rdUserMap = new HashMap<Long, LoginUserVO>();
		List<Integer> rewardStatusList = new ArrayList<Integer>();
		rewardStatusList.add(CommonUtils.RewardStatus.PENDING.getKey());
		rewardStatusList.add(CommonUtils.RewardStatus.COMPLETED.getKey());
		List<UserReward> rewardList = rewardRepo.findByRefOrderIdAndProductItemIdAndRewardStatusIn(orderId,
				productItemId, rewardStatusList);
		for (UserReward userReward : rewardList) {
			if (null != userReward.orderUserId) {
				User user = userRepo.findByUserId(userReward.orderUserId);
				LoginUserVO userVO = new LoginUserVO();
				userVO.userName = user.userName;
				userVO.headImgUrl = user.headImgUrl;
				rdUserMap.put(user.userId, userVO);
			}
		}
		return rdUserMap;
	}

	public void createReward(UserOrder order, UserOrderItem oi, ProductItem productItem) {
		List<UserReward> rewardList = rewardRepo.findByOrderUserIdAndProductItemIdAndRewardStatus(order.userId,
				productItem.productItemId, CommonUtils.RewardStatus.PENDING.getKey());
		if (null == rewardList || rewardList.isEmpty()) {
			UserReward ur = new UserReward();
			ur.orderId = order.orderId;
			if (null != oi) {
				ur.productId = oi.productId;
				ur.productItemId = oi.productItemId;
			} else {
				ur.productId = order.productId;
				ur.productItemId = order.productItemId;
			}
			ur.refUserId = order.refUserId;
			ur.refOrderId = order.refOrderId;
			ur.orderUserId = order.userId;
			ur.rewardRatio = 10; // ignore quantity
			ur.expiredDate = productItem.expiredDate;
			if (null == ur.refOrderId || ur.refOrderId.longValue() == 0L) {
				UserOrder rewardOrder = orderService.findOneOrderByProductItemIdAndUserId(ur.productItemId,
						ur.refUserId);
				if (null != rewardOrder) {
					ur.refOrderId = rewardOrder.orderId;
					ur.rewardStatus = CommonUtils.RewardStatus.PENDING.getKey();
				} else {
					ur.rewardStatus = CommonUtils.RewardStatus.INVALID.getKey();
				}
			} else {
				UserOrder rewardOrder = orderRepo.findOne(ur.refOrderId);
				if (null != rewardOrder.productItemId) {
					if (rewardOrder.productItemId.longValue() == productItem.productItemId.longValue()) {
						ur.rewardStatus = CommonUtils.RewardStatus.PENDING.getKey();
					} else {
						return;
					}
				} else {
					for (UserOrderItem rewardOi : rewardOrder.orderItems) {
						if (rewardOi.productItemId.longValue() == productItem.productItemId.longValue()) {
							ur.rewardStatus = CommonUtils.RewardStatus.PENDING.getKey();
						}
					}
					if (null == ur.rewardStatus || ur.rewardStatus != CommonUtils.RewardStatus.PENDING.getKey()) {
						return;
					}
				}
			}
			Calendar c = Calendar.getInstance();
			c.setTime(ur.expiredDate);
			c.add(Calendar.DATE, CommonUtils.reward_delay_days);
			ur.rewardDate = c.getTime();
			rewardRepo.save(ur);
		}
	}

	public BigDecimal calculateRewards(Long orderId, ProductVO productVO) {
		BigDecimal rewardValue = new BigDecimal(0);
		List<Integer> rewardStatusList = new ArrayList<Integer>();
		rewardStatusList.add(CommonUtils.RewardStatus.PENDING.getKey());
		rewardStatusList.add(CommonUtils.RewardStatus.COMPLETED.getKey());
		List<UserReward> rewardList = rewardRepo.findByRefOrderIdAndProductItemIdAndRewardStatusIn(orderId,
				productVO.productItemId, rewardStatusList);
		if (null != rewardList && !rewardList.isEmpty()) {
			for (UserReward userReward : rewardList) {
				rewardValue = rewardValue.add(productVO.currentPrice.multiply(new BigDecimal(
						userReward.rewardRatio * 0.01)));
			}
		}
		return rewardValue;
	}

	public List<UserReward> getPendingPayRewards() {
		return rewardRepo.findByRewardDateBeforeAndRewardStatusAndIsActive(new Date(),
				CommonUtils.RewardStatus.PENDING.getKey(), CommonUtils.ActiveStatus.YES.toString());
	}

	public void payRewards() {
		List<UserReward> rewards = this.getPendingPayRewards();
		Map<Long, List<UserReward>> userProductMap = new HashMap<Long, List<UserReward>>();
		for (UserReward userReward : rewards) {
			Long key = userReward.refOrderId;
			List<UserReward> rwList = new ArrayList<UserReward>();
			if (userProductMap.containsKey(key)) {
				rwList = userProductMap.get(key);
				rwList.add(userReward);
				userProductMap.put(key, rwList);
			} else {
				rwList.add(userReward);
				userProductMap.put(key, rwList);
			}
		}
		Iterator<Map.Entry<Long, List<UserReward>>> iter = userProductMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Long, List<UserReward>> entry = (Map.Entry<Long, List<UserReward>>) iter.next();
			Long orderId = entry.getKey();
			List<UserReward> rwList = entry.getValue();
			Integer ratioSum = 0;
			for (UserReward rw : rwList) {
				ratioSum = ratioSum + rw.rewardRatio;
				if (ratioSum > 100) {
					ratioSum = 100;
				}
			}
			Long productItemId = rwList.get(0).productItemId;
			BigDecimal rewardValue = this.calculateSingleReward(productItemId, ratioSum);
			UserOrder userOrder = orderRepo.findByOrderIdAndOrderStatusAndIsActive(orderId,
					CommonUtils.OrderStatus.DELEVRIED.getKey(), CommonUtils.ActiveStatus.YES.toString());
			if (null != userOrder && null != userOrder.paymentId && !userOrder.paymentId.isEmpty()) {
				try {
					apiService.applyRefund(userOrder.paymentId, rewardValue, CommonUtils.refund_type_reward);
					logger.info("order " + userOrder.trackingId + " reward applied for " + rewardValue.doubleValue());
				} catch (PingppException e) {
					logger.error(e.getMessage(), e);
					for (UserReward rw : rwList) {
						rw.rewardStatus = CommonUtils.RewardStatus.ERROR.getKey();
						rewardRepo.save(rw);
					}
				}
				// mark reward finish logic - performance to be improved
				for (UserReward rw : rwList) {
					rw.rewardStatus = CommonUtils.RewardStatus.COMPLETED.getKey();
					rewardRepo.save(rw);
				}
			}
		}
	}

	private BigDecimal calculateSingleReward(Long productItemId, Integer rewardRatio) {
		BigDecimal rewardValue = new BigDecimal(0);
		ProductItem productItem = productItemRepo.findOne(productItemId);
		rewardValue = rewardValue.add(productItem.currentPrice.multiply(new BigDecimal(rewardRatio * 0.01)));
		return rewardValue;
	}
}
