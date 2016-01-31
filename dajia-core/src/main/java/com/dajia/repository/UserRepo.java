package com.dajia.repository;

import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.User;

public interface UserRepo extends CrudRepository<User, Long> {

	public User findByMobile(String mobile);
}