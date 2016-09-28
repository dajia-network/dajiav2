package com.dajia.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.UserRefund;

public interface UserRefundRepo extends CrudRepository<UserRefund, Long> {
	public List<UserRefund> findByOrderIdAndRefundTypeAndIsActive(Long orderId, Integer refundType, String isActive);

	public List<UserRefund> findByOrderIdAndRefundStatusInAndIsActive(Long orderId, List<Integer> refundStatusList,
			String isActive);

	public List<UserRefund> findByRefundStatusAndIsActive(Integer refundStatus, String isaActive);
}