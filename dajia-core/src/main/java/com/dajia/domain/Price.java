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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties(value = { "productItem" })
@Table(name = "price")
public class Price extends BaseModel {

	@Column(name = "price_id")
	@Id
	@GeneratedValue
	public Long priceId;

	@Column(name = "sort")
	public int sort;

	@Column(name = "sold")
	public Long sold;

	@Column(name = "target_price")
	public BigDecimal targetPrice;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_item_id", referencedColumnName = "product_item_id")
	public ProductItem productItem;
}