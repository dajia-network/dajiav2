package com.dajia.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.Product;

public interface ProductRepo extends CrudRepository<Product, Long> {

	public Product findByRefId(String refId);

	public List<Product> findByIsActiveOrderByExpiredDateAsc(String isActive);
}