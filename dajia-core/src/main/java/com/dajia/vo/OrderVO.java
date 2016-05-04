package com.dajia.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.dajia.domain.Product;
import com.dajia.domain.UserContact;

public class OrderVO {

	public Long orderId;

	public String trackingId;

	public Integer quantity;

	public Integer orderStatus;

	public Integer payType;

	public Long productId;

	public Long userId;

	public String userName;

	public BigDecimal unitPrice;

	public BigDecimal totalPrice;
	
	public BigDecimal postFee;

	public String logisticAgent;

	public String logisticTrackingId;

	public String comments;

	public String userComments;

	public String orderStatus4Show;

	public Date orderDate;

	public UserContact userContact;

	public Product product;
}
