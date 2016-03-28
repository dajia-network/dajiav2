package com.dajia.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.UserOrder;

public interface UserOrderRepo extends CrudRepository<UserOrder, Long> {

	public List<UserOrder> findByUserIdOrderByOrderDateDesc(Long userId);

	public Page<UserOrder> findByIsActiveOrderByOrderDateDesc(String isActive, Pageable pageable);
	
	public Page<UserOrder> findByUserIdNotAndIsActiveOrderByOrderDateDesc(Long userId, String isActive, Pageable pageable);
}