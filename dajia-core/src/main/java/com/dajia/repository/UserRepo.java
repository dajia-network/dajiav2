package com.dajia.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.User;

public interface UserRepo extends CrudRepository<User, Long> {

	public User findByUserId(Long userId);

	public User findByMobile(String mobile);

	public Page<User> findByIsActiveOrderByCreatedDateDesc(String isActive, Pageable pageable);
}