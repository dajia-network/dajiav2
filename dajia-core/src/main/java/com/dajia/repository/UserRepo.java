package com.dajia.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.User;

public interface UserRepo extends CrudRepository<User, Long> {

	public User findByUserId(Long userId);

	public User findByMobile(String mobile);

	public User findByOauthUserIdAndOauthType(String oauthUserId, String oauthType);

	public Page<User> findByIsActiveOrderByCreatedDateDesc(String isActive, Pageable pageable);

	public Page<User> findByIsSalesAndIsActiveOrderByCreatedDateDesc(String isSales, String isActive, Pageable pageable);

	public Page<User> findByUserNameContainingAndIsActiveOrderByCreatedDateDesc(String userName, String isActive,
			Pageable pageable);

	public List<User> findByRefUserIdAndCreatedDateBetweenAndIsActive(Long refUserId, Date startDate, Date endDate,
			String isActive);
}