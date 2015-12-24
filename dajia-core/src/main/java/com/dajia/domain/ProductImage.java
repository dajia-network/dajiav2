package com.dajia.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "product_img")
public class ProductImage extends BaseModel {

	@Column(name = "img_id")
	@Id
	@GeneratedValue
	public Long imgId;

	@Column(name = "product_id", nullable = false)
	public Long productId;

	@Column(name = "sort")
	public int sort;

	@Column(name = "location")
	public String location;

	@Column(name = "path")
	public String path;

	@Column(name = "img_type")
	public Integer imgType;

	// @ManyToOne
	// @JoinColumn(name="product_id")
	// private Product product;
}