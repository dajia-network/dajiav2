package com.dajia.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dajia.domain.User;
import com.dajia.domain.UserOrder;
import com.dajia.domain.UserReward;
import com.dajia.repository.UserOrderRepo;
import com.dajia.repository.UserRepo;
import com.dajia.repository.UserRewardRepo;
import com.dajia.vo.LoginUserVO;

@Service
public class RewardService {
	Logger logger = LoggerFactory.getLogger(RewardService.class);

	@Autowired
	private UserRewardRepo rewardRepo;

	@Autowired
	private UserOrderRepo orderRepo;

	@Autowired
	private UserRepo userRepo;

	public Map<Long, LoginUserVO> getRefUsers(Long orderId) {
		UserOrder order = orderRepo.findOne(orderId);
		Map<Long, LoginUserVO> refUserMap = new HashMap<Long, LoginUserVO>();
		List<UserReward> rewardList = rewardRepo.findByUserIdAndProductId(order.userId, order.productId);
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
}
