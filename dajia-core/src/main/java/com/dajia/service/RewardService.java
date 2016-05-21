package com.dajia.service;

import java.math.BigDecimal;
import java.util.HashMap;
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

	public BigDecimal calculateRewardValue(Long userId, Product product) {
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
}
