package com.dajia.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.UserOrder;

public interface UserOrderRepo extends CrudRepository<UserOrder, Long> {

	public UserOrder findByTrackingId(String trackingId);

	public UserOrder findByPaymentId(String paymentId);

	public UserOrder findByOrderIdAndOrderStatusAndIsActive(Long orderId, Integer orderStatus, String isActive);

	public UserOrder findByUserIdAndProductIdAndOrderStatusAndIsActive(Long userId, Long productId,
			Integer orderStatus, String isActive);

	public List<UserOrder> findByUserIdAndOrderStatusInOrderByOrderDateDesc(Long userId, List<Integer> orderStatusList);

	public List<UserOrder> findByUserIdOrderByOrderDateDesc(Long userId);

	public List<UserOrder> findByProductIdAndIsActiveOrderByOrderDateDesc(Long productId, String isActive);

	public Page<UserOrder> findByIsActiveOrderByOrderDateDesc(String isActive, Pageable pageable);

	public Page<UserOrder> findByUserIdNotAndIsActiveOrderByOrderDateDesc(Long userId, String isActive,
			Pageable pageable);
}