package com.dajia.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.UserRefund;

public interface UserRefundRepo extends CrudRepository<UserRefund, Long> {
	public List<UserRefund> findByOrderIdAndRefundTypeAndIsActive(Long orderId, Integer refundType, String isActive);

	public List<UserRefund> findByOrderIdAndIsActive(Long orderId, String isActive);
}