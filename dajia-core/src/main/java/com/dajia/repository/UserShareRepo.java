package com.dajia.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.UserShare;

public interface UserShareRepo extends CrudRepository<UserShare, Long> {

	public List<UserShare> findByUserIdAndVisitUserIdAndProductItemIdAndShareType(Long userId, Long visitUserId,
			Long productItemId, Integer shareType);

	public List<UserShare> findByOrderIdAndProductItemIdAndShareTypeOrderByShareIdDesc(Long orderId, Long productItemId, Integer shareType);
}