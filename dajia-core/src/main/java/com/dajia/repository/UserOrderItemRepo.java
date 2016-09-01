package com.dajia.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.UserOrderItem;

public interface UserOrderItemRepo extends CrudRepository<UserOrderItem, Long> {
	public List<UserOrderItem> findByProductItemIdAndIsActive(Long productItemId, String isActive);

	public List<UserOrderItem> findByProductItemIdAndUserIdAndAndIsActiveOrderByOrderItemId(Long productItemId,
			Long userId, String isActive);

}