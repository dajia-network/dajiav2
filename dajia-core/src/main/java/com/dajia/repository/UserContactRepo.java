package com.dajia.repository;

import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.UserContact;

public interface UserContactRepo extends CrudRepository<UserContact, Long> {

}