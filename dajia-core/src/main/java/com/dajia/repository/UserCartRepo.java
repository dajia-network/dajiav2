package com.dajia.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.UserCart;

public interface UserCartRepo extends CrudRepository<UserCart, Long> {

	public List<UserCart> findByUserIdOrderByCreatedDateDesc(Long userId);

	public UserCart findByUserIdAndProductId(Long userId, Long productId);
}