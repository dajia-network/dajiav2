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
@Table(name = "user_order")
public class UserOrder extends BaseModel {

	@Column(name = "order_id")
	@Id
	@GeneratedValue
	public Long orderId;

	@Column(name = "tracking_id")
	public String trackingId;

	@Column(name = "product_id", nullable = false)
	public Long productId;

	@Column(name = "user_contact_id", nullable = false)
	public Long contactId;

	@Column(name = "user_id", nullable = false)
	public Long userId;

	@Column(name = "payment_id", nullable = false)
	public Long paymentId;

	@Column(name = "quantity")
	public Integer quantity;

	@Column(name = "order_status")
	public Integer orderStatus;

	@Column(name = "pay_type")
	public Integer payType;

	@Column(name = "unit_price")
	public BigDecimal unitPrice;

	@Column(name = "total_price")
	public BigDecimal totalPrice;

	@Column(name = "order_date")
	public Date orderDate;

	@Column(name = "deliver_date")
	public Date deliverDate;

	@Column(name = "close_date")
	public Date closeDate;

	@Column(name = "logistic_agent")
	public String logisticAgent;

	@Column(name = "logistic_tracking_id")
	public String logisticTrackingId;

	@Column(name = "comments")
	public String comments;

	@Column(name = "user_comments")
	public String userComments;

	@Transient
	public String orderStatus4Show;

	@Transient
	public String userInfo4Show;

	@Transient
	public String productInfo4Show;
}