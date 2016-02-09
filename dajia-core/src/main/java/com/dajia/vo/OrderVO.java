package com.dajia.vo;

import java.math.BigDecimal;

import com.dajia.domain.UserContact;

public class OrderVO {

	public Integer quantity;

	public Long productId;

	public BigDecimal unitPrice;

	public BigDecimal totalPrice;

	public UserContact userContact;
}
