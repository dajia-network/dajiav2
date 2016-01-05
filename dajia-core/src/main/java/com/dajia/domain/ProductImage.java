package com.dajia.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties(value = { "product" })
@Table(name = "product_img")
public class ProductImage extends BaseModel {

	@Column(name = "img_id")
	@Id
	@GeneratedValue
	public Long imgId;

	@Column(name = "sort")
	public int sort;

	@Column(name = "url")
	public String url;

	@Column(name = "thumb_url")
	public String thumbUrl;

	@Column(name = "med_url")
	public String medUrl;

	@Column(name = "img_type")
	public Integer imgType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", referencedColumnName = "product_id")
	public Product product;
}