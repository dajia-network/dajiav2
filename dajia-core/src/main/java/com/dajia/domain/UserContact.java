package com.dajia.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_contact")
public class UserContact extends BaseModel {

	@Column(name = "contact_info_id")
	@Id
	@GeneratedValue
	public Long contactId;

	@Column(name = "user_id", nullable = false)
	public Long userId;

	@Column(name = "contact_name", nullable = false)
	public String contactName;

	@Column(name = "contact_mobile", nullable = false)
	public String contactMobile;

	@Column(name = "mobile", nullable = false)
	public String mobile;

	@Column(name = "province")
	public String province;

	@Column(name = "city")
	public String city;

	@Column(name = "district")
	public String district;

	@Column(name = "zipcode")
	public String zipcode;

	@Column(name = "is_default")
	public String isDefault;

	@Column(name = "address_1")
	public String address1;

	@Column(name = "address_2")
	public String address2;

}