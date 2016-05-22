package com.dajia.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.UserReward;

public interface UserRewardRepo extends CrudRepository<UserReward, Long> {
	public List<UserReward> findByUserIdAndProductIdAndRewardStatus(Long userId, Long productId, Integer rewardStatus);

	public List<UserReward> findByRewardDateAfterAndRewardStatusAndIsActive(Date rewardDate, Integer rewardStatus,
			String isActive);
}