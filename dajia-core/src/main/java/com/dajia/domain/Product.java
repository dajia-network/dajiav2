package com.dajia.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "product")
public class Product extends BaseModel {

	@Column(name = "product_id")
	@Id
	@GeneratedValue
	public Long productId;

	@Column(name = "ref_id")
	public String refId;

	@Column(name = "short_name")
	public String shortName;

	@Column(name = "name", nullable = false)
	public String name;

	@Column(name = "brief")
	public String brief;

	@Column(name = "description")
	public String description;

	@Column(name = "spec")
	public String spec;

	@Column(name = "sold")
	public Long sold;

	@Column(name = "stock")
	public Long stock;
	
	@Column(name = "product_status")
	public Integer productStatus;

	@Column(name = "original_price")
	public BigDecimal originalPrice;

	@Column(name = "current_price")
	public BigDecimal currentPrice;

	@Column(name = "target_price")
	public BigDecimal targetPrice;

	@Column(name = "start_date")
	public Date startDate;

	@Column(name = "expired_date")
	public Date expiredDate;

	@Transient
	public BigDecimal priceOff;

	@Transient
	public String productImg;

	@Transient
	public String vendorImg;

	@Transient
	public List<ProductImage> productImages;

	@Transient
	public List<String> productImagesExt;

	@Transient
	public List<String> productImagesThumbExt;

}