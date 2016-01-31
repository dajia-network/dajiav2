package com.dajia.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

//import com.avaje.ebean.*;

@Entity
@Table(name = "user")
public class User extends BaseModel {

	@Column(name = "user_id")
	@Id
	@GeneratedValue
	public Long userId;

	@Column(name = "username", nullable = false)
	public String userName;

	@Column(name = "email")
	public String email;

	@Column(name = "mobile", nullable = false)
	public String mobile;

	@Column(name = "password", nullable = false)
	public String password;

	@Column(name = "wechat")
	public String wechat;

	@Column(name = "last_visit_date")
	public Date lastVisitDate;

	@Column(name = "last_visit_ip")
	public String lastVisitIP;

}