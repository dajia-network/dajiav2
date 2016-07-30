package com.dajia.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.dajia.vo.ProductVO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties(value = { "userOrder" })
@Table(name = "user_order_item")
public class UserOrderItem extends BaseModel {

	@Column(name = "order_item_id")
	@Id
	@GeneratedValue
	public Long orderItemId;

	@Column(name = "tracking_id")
	public String trackingId;

	@Column(name = "product_id", nullable = false)
	public Long productId;

	@Column(name = "product_item_id", nullable = false)
	public Long productItemId;

	@Column(name = "user_id", nullable = false)
	public Long userId;

	@Column(name = "product_shared")
	public String productShared;

	@Column(name = "quantity")
	public Integer quantity;

	@Column(name = "unit_price")
	public BigDecimal unitPrice;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "order_id", referencedColumnName = "order_id")
	public UserOrder userOrder;

	@Transient
	public ProductVO productVO;
}