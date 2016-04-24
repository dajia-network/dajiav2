package com.dajia.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.Product;

public interface ProductRepo extends CrudRepository<Product, Long> {

	public Product findByRefId(String refId);

	public List<Product> findByProductStatusAndIsActiveOrderByExpiredDateAsc(Integer productStatus, String isActive);

	public Page<Product> findByIsActiveOrderByExpiredDateAsc(String isActive, Pageable pageable);

	public Page<Product> findByIsActiveOrderByStartDateDesc(String isActive, Pageable pageable);

	public List<Product> findByProductIdInAndIsActive(List<Long> productIds, String isActive);
}