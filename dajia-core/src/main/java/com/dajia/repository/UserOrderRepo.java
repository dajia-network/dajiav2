package com.dajia.repository;

import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.UserOrder;

public interface UserOrderRepo extends CrudRepository<UserOrder, Long> {

}