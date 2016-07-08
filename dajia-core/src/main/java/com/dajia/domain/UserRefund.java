package com.dajia.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "user_refund")
public class UserRefund extends BaseModel {

	@Column(name = "refund_id")
	@Id
	@GeneratedValue
	public Long refundId;

	@Column(name = "user_id")
	public Long userId;

	@Column(name = "product_id")
	public Long productId;
	
	@Column(name = "product_item_id")
	public Long productItemId;

	@Column(name = "order_id")
	public Long orderId;

	@Column(name = "refund_date")
	public Date refundDate;

	@Column(name = "refund_value")
	public BigDecimal refundValue;

	@Column(name = "refund_type")
	public Integer refundType;

	@Transient
	public String refundType4Show;

}