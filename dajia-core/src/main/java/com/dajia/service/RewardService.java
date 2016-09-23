package com.dajia.service;

import com.dajia.domain.*;
import com.dajia.repository.ProductItemRepo;
import com.dajia.repository.UserOrderRepo;
import com.dajia.repository.UserRepo;
import com.dajia.repository.UserRewardRepo;
import com.dajia.util.CommonUtils;
import com.dajia.vo.LoginUserVO;
import com.dajia.vo.ProductVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

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
			Calendar c = Calendar.getInstance();
			c.setTime(ur.expiredDate);
			c.add(Calendar.DATE, CommonUtils.reward_delay_days);
			ur.rewardDate = c.getTime();
			if (null == ur.refOrderId || ur.refOrderId.longValue() == 0L) {
				UserOrder rewardOrder = orderService.findOneOrderByProductItemIdAndUserId(ur.productItemId,
						ur.refUserId);
				if (null != rewardOrder) {
					ur.refOrderId = rewardOrder.orderId;
					ur.rewardStatus = CommonUtils.RewardStatus.PENDING.getKey();
				} else {
					User rewardUser = userRepo.findOne(ur.refUserId);
					if (null != rewardUser
							&& rewardUser.isSales.equalsIgnoreCase(CommonUtils.YesNoStatus.YES.toString())) {
						ur.rewardStatus = CommonUtils.RewardStatus.SALES.getKey();
						ur.rewardDate = new Date();
					} else {
						ur.rewardStatus = CommonUtils.RewardStatus.INVALID.getKey();
					}
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
		try {
			return rewardRepo.findByRewardDateBeforeAndRewardStatusAndIsActive(new Date(),
					CommonUtils.RewardStatus.PENDING.getKey(), CommonUtils.ActiveStatus.YES.toString());
		} catch (Exception ex) {
			logger.error("getPendingPayRewards failed at {}", System.currentTimeMillis(), ex);
			return null;
		}
	}

	/**
	 * 发起reward退款
	 *
	 * @param jobToken
	 */
	public void payRewards(String jobToken) {
		logger.info("payRewards job {} starts at {}", jobToken, System.currentTimeMillis());

		List<UserReward> rewards = this.getPendingPayRewards();

		if(null == rewards) {
			logger.error("payRewards job {}, failed because getPendingPayRewards returns null at {}", jobToken, System.currentTimeMillis());
			return;
		}

		if(rewards.isEmpty()) {
			logger.info("payRewards job {}, exit because getPendingPayRewards is empty at {}", jobToken, System.currentTimeMillis());
			return;
		}

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
			Map.Entry<Long, List<UserReward>> entry = iter.next();
			Long orderId = entry.getKey();
			List<UserReward> rwList = entry.getValue();

			if(CollectionUtils.isEmpty(rwList)) {
				logger.error("payRewards job {}, skipped for orderId " + orderId + ", no user reward for this order", jobToken);
				continue;
			}

			Integer ratioSum = 0;
			for (UserReward rw : rwList) {
				ratioSum = ratioSum + rw.rewardRatio;
				if (ratioSum > 100) {
					ratioSum = 100;
				}
			}

			Long productItemId = rwList.get(0).productItemId;
			BigDecimal rewardValue = this.calculateSingleReward(productItemId, ratioSum);

			int k = rewardValue.compareTo(BigDecimal.ZERO);
			if(k <= 0) {
				logger.warn("payRewards job {}, reward value <= 0, order id is " + orderId + ", rewards are " + rwList, jobToken);
			}

			UserOrder userOrder = orderRepo.findByOrderIdAndIsActive(orderId, CommonUtils.ActiveStatus.YES.toString());

			if (null == userOrder) {
				logger.error("payRewards job {}, failed for orderId " + orderId + ", order not found", jobToken);
				continue;
			}

			if (!CommonUtils.OrderStatus.DELEVRIED.getKey().equals(userOrder.orderStatus)) {
				logger.warn("payRewards job {}, ignore for undelivered orderId {}", jobToken, orderId);
				continue;
			}

			if (StringUtils.isEmpty(userOrder.paymentId)) {
				logger.error("payRewards job {}, failed for orderId " + orderId + ", paymentId is empty", jobToken);
				continue;
			}

			try {
				apiService.applyRefund(userOrder.paymentId, rewardValue, CommonUtils.refund_type_reward);
				batchUpdateRewardStatus(rwList, CommonUtils.RewardStatus.COMPLETED, jobToken);

				logger.info("payRewards job {} success for order " + orderId + ", trackingId=" + userOrder.trackingId + " value=" + rewardValue.doubleValue(), jobToken);

			} catch (Exception ex) {
				logger.error("payRewards job {}, exception for order " + orderId + ", " + ex.getMessage() , jobToken, ex);
				batchUpdateRewardStatus(rwList, CommonUtils.RewardStatus.ERROR, jobToken);
			}

		}

		logger.info("payRewards job {} finished at {}", jobToken, System.currentTimeMillis());
	}

	/**
	 * 批量更新reward的状态
	 *
	 * TODO 性能问题
	 *
	 * @param userRewards
	 * @param rewardStatus
	 * @param jobToken
	 *
	 * @return
	 */
	private boolean batchUpdateRewardStatus(List<UserReward> userRewards, CommonUtils.RewardStatus rewardStatus, String jobToken) {
		if(null == rewardStatus) {
			logger.error("batch update rewards status failed, status is null");
			return false;
		}

		if(null == userRewards) {
			logger.error("batch update rewards status failed, userRewards is null");
			return false;
		}

		Integer key = rewardStatus.getKey();
		String desc = rewardStatus.getValue();
		for (UserReward reward : userRewards) {
			if(null != reward) {
				reward.rewardStatus = key;
				rewardRepo.save(reward);
				logger.info("payRewards job {}, reward " + reward.rewardId + " status updated to " + desc, jobToken);
			}
		}
		return true;
	}

	private BigDecimal calculateSingleReward(Long productItemId, Integer rewardRatio) {
		BigDecimal rewardValue = new BigDecimal(0);
		ProductItem productItem = productItemRepo.findOne(productItemId);
		rewardValue = rewardValue.add(productItem.currentPrice.multiply(new BigDecimal(rewardRatio * 0.01)));
		return rewardValue;
	}

}
