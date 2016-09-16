package com.dajia.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.Product;

public interface ProductRepo extends CrudRepository<Product, Long> {

	public Product findByRefId(String refId);

	public List<Product> findByProductIdInAndIsActive(List<Long> productIds, String isActive);

	public List<Product> findByNameContainingAndIsActiveOrderByCreatedDateDesc(String keyword, String isActive);

}