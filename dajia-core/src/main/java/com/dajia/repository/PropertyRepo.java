package com.dajia.repository;

import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.Property;

public interface PropertyRepo extends CrudRepository<Property, Long> {

	public Property findByPropertyKey(String propertyKey);
}