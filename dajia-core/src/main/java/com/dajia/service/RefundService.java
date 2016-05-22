package com.dajia.service;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dajia.domain.UserOrder;
import com.dajia.domain.UserRefund;
import com.dajia.repository.ProductRepo;
import com.dajia.repository.UserOrderRepo;
import com.dajia.repository.UserRefundRepo;

@Service
public class RefundService {
	Logger logger = LoggerFactory.getLogger(RefundService.class);

	@Autowired
	private UserRefundRepo refundRepo;

	@Autowired
	private UserOrderRepo orderRepo;

	@Autowired
	private ProductRepo productRepo;

	public void createRefund(String chargeId, BigDecimal refundValue, Integer refundType) {
		UserOrder order = orderRepo.findByPaymentId(chargeId);
		UserRefund refund = new UserRefund();
		refund.productId = order.productId;
		refund.orderId = order.orderId;
		refund.userId = order.userId;
		refund.refundValue = refundValue;
		refund.refundType = refundType;
		refund.refundDate = new Date();
		refundRepo.save(refund);
	}
}
