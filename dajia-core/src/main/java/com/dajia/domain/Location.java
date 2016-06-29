package com.dajia.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "location")
public class Location extends BaseModel {

	@Column(name = "id")
	@Id
	@GeneratedValue
	public Long id;

	@Column(name = "location_key", nullable = false)
	public Long locationKey;

	@Column(name = "location_value", nullable = false)
	public String locationValue;

	@Column(name = "min_post_fee")
	public Integer minPostFee;

	@Column(name = "parent_key", nullable = false)
	public Long parentKey;

	@Column(name = "location_type", nullable = false)
	public String locationType;

}