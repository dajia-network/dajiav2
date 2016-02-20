package com.dajia.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.UserFavourite;

public interface UserFavouriteRepo extends CrudRepository<UserFavourite, Long> {

	public List<UserFavourite> findByUserIdOrderByCreatedDateDesc(Long userId);

	public UserFavourite findByUserIdAndProductId(Long userId, Long productId);
}