package com.dajia.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dajia.domain.Product;
import com.dajia.domain.User;
import com.dajia.domain.UserOrder;
import com.dajia.domain.UserReward;
import com.dajia.repository.ProductRepo;
import com.dajia.repository.UserOrderRepo;
import com.dajia.repository.UserRepo;
import com.dajia.repository.UserRewardRepo;
import com.dajia.util.CommonUtils;
import com.dajia.vo.LoginUserVO;
import com.pingplusplus.exception.PingppException;

@Service
public class RewardService {
	Logger logger = LoggerFactory.getLogger(RewardService.class);

	@Autowired
	private UserRewardRepo rewardRepo;

	@Autowired
	private UserOrderRepo orderRepo;

	@Autowired
	private ProductRepo productRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private ApiService apiService;

	public Map<Long, LoginUserVO> getRefUsers(Long orderId) {
		UserOrder order = orderRepo.findOne(orderId);
		Map<Long, LoginUserVO> refUserMap = new HashMap<Long, LoginUserVO>();
		List<UserReward> rewardList = rewardRepo.findByUserIdAndProductIdAndRewardStatus(order.userId, order.productId,
				CommonUtils.RewardStatus.PENDING.getKey());
		for (UserReward userReward : rewardList) {
			if (null != userReward.orderUserId) {
				User user = userRepo.findByUserId(userReward.orderUserId);
				LoginUserVO userVO = new LoginUserVO();
				userVO.userName = user.userName;
				userVO.headImgUrl = user.headImgUrl;
				refUserMap.put(user.userId, userVO);
			}
		}
		return refUserMap;
	}

	public BigDecimal calculateRewards(Long userId, Product product) {
		BigDecimal rewardValue = new BigDecimal(0);
		List<UserReward> rewardList = rewardRepo.findByUserIdAndProductIdAndRewardStatus(userId, product.productId,
				CommonUtils.RewardStatus.PENDING.getKey());
		if (null != rewardList && !rewardList.isEmpty()) {
			for (UserReward userReward : rewardList) {
				rewardValue = rewardValue.add(product.currentPrice.multiply(new BigDecimal(
						userReward.rewardRatio * 0.01)));
			}
		}
		return rewardValue;
	}

	public List<UserReward> getPendingPayRewards() {
		return rewardRepo.findByRewardDateAfterAndRewardStatusAndIsActive(new Date(),
				CommonUtils.RewardStatus.PENDING.getKey(), CommonUtils.ActiveStatus.YES.toString());
	}

	public void payRewards() {
		List<UserReward> rewards = this.getPendingPayRewards();
		Map<String, List<UserReward>> userProductMap = new HashMap<String, List<UserReward>>();
		for (UserReward userReward : rewards) {
			String key = userReward.userId + "-" + userReward.productId;
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
		Iterator<Map.Entry<String, List<UserReward>>> iter = userProductMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, List<UserReward>> entry = (Map.Entry<String, List<UserReward>>) iter.next();
			String key = entry.getKey();
			String keyArray[] = key.split("-");
			if (keyArray.length == 2) {
				Long userId = Long.valueOf(keyArray[0]);
				Long productId = Long.valueOf(keyArray[1]);
				List<UserReward> rwList = entry.getValue();
				Integer ratioSum = 0;
				for (UserReward rw : rwList) {
					ratioSum = ratioSum + rw.rewardRatio;
					if (ratioSum > 100) {
						ratioSum = 100;
					}
				}
				BigDecimal rewardValue = this.calculateSingleReward(productId, ratioSum);
				UserOrder userOrder = orderRepo.findByUserIdAndProductIdAndOrderStatusAndIsActive(userId, productId,
						CommonUtils.OrderStatus.DELEVRIED.getKey(), CommonUtils.ActiveStatus.YES.toString());
				if (null != userOrder && null != userOrder.paymentId && !userOrder.paymentId.isEmpty()) {
					try {
						apiService.applyRefund(userOrder.paymentId, rewardValue, CommonUtils.refund_type_reward);
						logger.info("order " + userOrder.trackingId + " reward applied for "
								+ rewardValue.doubleValue());
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
			} else {
				logger.error("Check reward error, key: " + key);
			}
		}
	}

	private BigDecimal calculateSingleReward(Long productId, Integer rewardRatio) {
		BigDecimal rewardValue = new BigDecimal(0);
		Product product = productRepo.findOne(productId);
		rewardValue = rewardValue.add(product.currentPrice.multiply(new BigDecimal(rewardRatio * 0.01)));
		return rewardValue;
	}
}
