package com.dajia.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.UserReward;

public interface UserRewardRepo extends CrudRepository<UserReward, Long> {
	public List<UserReward> findByRefUserIdAndProductItemIdAndRewardStatus(Long refUserId, Long productItemId,
			Integer rewardStatus);

	public List<UserReward> findByRefOrderIdAndProductItemIdAndRewardStatus(Long refOrderId, Long productItemId,
			Integer rewardStatus);

	public List<UserReward> findTop5ByRefOrderIdAndRewardStatusOrderByCreatedDateDesc(Long refOrderId,
			Integer rewardStatus);

	public List<UserReward> findByOrderUserIdAndProductItemIdAndRewardStatus(Long orderUserId, Long productItemId,
			Integer rewardStatus);

	public List<UserReward> findByRewardDateBeforeAndRewardStatusAndIsActive(Date rewardDate, Integer rewardStatus,
			String isActive);
}