package com.dajia.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_favourite")
public class UserFavourite extends BaseModel {

	@Column(name = "favourite_id")
	@Id
	@GeneratedValue
	public Long favouriteId;

	@Column(name = "user_id")
	public Long userId;

	@Column(name = "product_id")
	public Long productId;
}