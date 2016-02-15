package com.dajia.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.UserFavourite;

public interface UserFavouriteRepo extends CrudRepository<UserFavourite, Long> {

	public List<UserFavourite> findByUserId(Long userId);

	public UserFavourite findByUserIdProductId(Long userId, Long productId);
}