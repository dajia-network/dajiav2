package com.dajia.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.dajia.vo.ProductVO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
// @JsonIgnoreProperties(value = { "product" })
@Table(name = "product_item")
public class ProductItem extends BaseModel {

	@Column(name = "product_item_id")
	@Id
	@GeneratedValue
	public Long productItemId;

	@Column(name = "sold")
	public Long sold;

	@Column(name = "stock")
	public Long stock;

	@Column(name = "buy_quota")
	public Integer buyQuota;

	@Column(name = "product_status")
	public Integer productStatus;

	@Column(name = "original_price")
	public BigDecimal originalPrice;

	@Column(name = "current_price")
	public BigDecimal currentPrice;

	@Column(name = "post_fee")
	public BigDecimal postFee;

	@Column(name = "start_date")
	public Date startDate;

	@Column(name = "expired_date")
	public Date expiredDate;

	@Transient
	public BigDecimal targetPrice;

	@Transient
	public BigDecimal priceOff;

	@Transient
	public long soldNeeded;

	@Transient
	public long progressValue;

	@Transient
	public BigDecimal nextOff;

	@Transient
	public String status4Show;

	@Transient
	public Long realSold;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "productItem", fetch = FetchType.LAZY)
	public List<Price> prices;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "product_id", referencedColumnName = "product_id")
	public Product product;

}