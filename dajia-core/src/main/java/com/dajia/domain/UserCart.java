package com.dajia.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_cart")
public class UserCart extends BaseModel {

	@Column(name = "cart_id")
	@Id
	@GeneratedValue
	public Long cartId;

	@Column(name = "user_id")
	public Long userId;

	@Column(name = "product_id")
	public Long productId;

	@Column(name = "product_item_id")
	public Long productItemId;

	@Column(name = "quantity")
	public Integer quantity;
}