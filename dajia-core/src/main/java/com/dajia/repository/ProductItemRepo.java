package com.dajia.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.ProductItem;

public interface ProductItemRepo extends CrudRepository<ProductItem, Long> {

	public List<ProductItem> findByProductStatusAndIsActiveOrderByExpiredDateAsc(Integer productStatus, String isActive);

	public Page<ProductItem> findByProductStatusAndIsActiveOrderByExpiredDateAsc(Integer productStatus,
			String isActive, Pageable pageable);

	public Page<ProductItem> findByIsActiveOrderByStartDateDesc(String isActive, Pageable pageable);

	public Page<ProductItem> findByProductStatusInAndStartDateBeforeAndIsActiveOrderByProductStatusAscExpiredDateAsc(
			List<Integer> productStatusList, Date startDate, String isActive, Pageable pageable);
}