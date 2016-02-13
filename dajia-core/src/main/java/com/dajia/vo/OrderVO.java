package com.dajia.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.dajia.domain.Product;
import com.dajia.domain.UserContact;

public class OrderVO {

	public Long orderId;

	public Integer quantity;

	public Long productId;

	public BigDecimal unitPrice;

	public BigDecimal totalPrice;

	public String orderStatus4Show;

	public Date orderDate;

	public UserContact userContact;

	public Product product;
}
