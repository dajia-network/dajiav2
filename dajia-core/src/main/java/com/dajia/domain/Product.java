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
import javax.persistence.OneToMany;
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
	
	@Column(name = "total_sold")
	public Long totalSold;

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
	public boolean isFav;

	@Transient
	public String status4Show;

	@Column(name = "img_url")
	public String imgUrl;

	@Column(name = "img_thumb_url")
	public String imgThumbUrl;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "product", fetch = FetchType.LAZY)
	public List<ProductImage> productImages;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "product", fetch = FetchType.LAZY)
	public List<Price> prices;

	@Deprecated
	@Transient
	public String vendorImg;

	@Deprecated
	@Transient
	public List<String> productImagesExt;

	@Deprecated
	@Transient
	public List<String> productImagesThumbExt;

}