package com.dajia.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dajia.repository.UserRewardRepo;

@Service
public class RewardService {
	Logger logger = LoggerFactory.getLogger(RewardService.class);

	@Autowired
	private UserRewardRepo rewardRepo;

}
