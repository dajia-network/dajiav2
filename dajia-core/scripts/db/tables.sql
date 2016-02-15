CREATE DATABASE IF NOT EXISTS dajia DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

DROP TABLE IF EXISTS dajia.user;
CREATE TABLE IF NOT EXISTS dajia.user (
	user_id BIGINT(25) NOT NULL AUTO_INCREMENT,
	username VARCHAR(100) NULL,
	email VARCHAR(255) NULL,
	mobile VARCHAR(20) NOT NULL,
	password VARCHAR(200) NOT NULL,
	wechat VARCHAR(100) NULL,
	last_visit_date TIMESTAMP NULL,
	last_visit_ip VARCHAR(20) NULL,
	created_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	modified_date TIMESTAMP NULL,
    is_active VARCHAR(5) NOT NULL DEFAULT 'Y',
	PRIMARY KEY(user_id),
    UNIQUE KEY (username),
    UNIQUE KEY (email),
    UNIQUE KEY (mobile)
);
  
DROP TABLE IF EXISTS dajia.user_contact;
CREATE TABLE IF NOT EXISTS dajia.user_contact (
	user_contact_id BIGINT(25) NOT NULL AUTO_INCREMENT,
    user_id BIGINT(25) NOT NULL,
    contact_name VARCHAR(100) NOT NULL,
    contact_mobile VARCHAR(20) NOT NULL,
    province BIGINT(25),
    city BIGINT(25),
    district BIGINT(25),
    zipcode VARCHAR(20),
    is_default VARCHAR(5),
    address_1 VARCHAR(400),
    address_2 VARCHAR(400),
	created_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	modified_date TIMESTAMP NULL,
    is_active VARCHAR(5) NOT NULL DEFAULT 'Y',
	PRIMARY KEY(user_contact_id)
);

DROP TABLE IF EXISTS dajia.product;
CREATE TABLE IF NOT EXISTS dajia.product (
	product_id BIGINT(25) NOT NULL AUTO_INCREMENT,
    ref_id VARCHAR(100) NULL,
    short_name VARCHAR(100) NULL,
    name VARCHAR(500) NOT NULL,
    brief VARCHAR(4000) NULL,
    description TEXT NULL,
    spec TEXT NULL,
    img_url VARCHAR(1000) NULL,
    img_thumb_url VARCHAR(1000) NULL,
    sold INT,
    stock INT,
    buy_quota INT,
    product_status INT,
    original_price NUMERIC(10,2),
    current_price NUMERIC(10,2),
    post_fee NUMERIC(10,2),
	start_date TIMESTAMP NULL,
	expired_date TIMESTAMP NULL,
	created_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	modified_date TIMESTAMP NULL,
    is_active VARCHAR(5) NOT NULL DEFAULT 'Y',
	PRIMARY KEY(product_id)
);

DROP TABLE IF EXISTS dajia.price;
CREATE TABLE IF NOT EXISTS dajia.price (
	price_id BIGINT(25) NOT NULL AUTO_INCREMENT,
    product_id BIGINT(25) NOT NULL,
    sort INT NOT NULL,
    sold INT,
    target_price NUMERIC(10,2),
	created_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	modified_date TIMESTAMP NULL,
    is_active VARCHAR(5) NOT NULL DEFAULT 'Y',
	PRIMARY KEY(price_id)
);

DROP TABLE IF EXISTS dajia.product_img;
CREATE TABLE IF NOT EXISTS dajia.product_img (
	img_id BIGINT(25) NOT NULL AUTO_INCREMENT,
	product_id BIGINT(25) NOT NULL,
    sort INT NULL,
    url VARCHAR(1000) NULL,
    thumb_url VARCHAR(1000) NULL,
    med_url VARCHAR(1000) NULL,
    img_type INT NULL,
	created_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	modified_date TIMESTAMP NULL,
    is_active VARCHAR(5) NOT NULL DEFAULT 'Y',
	PRIMARY KEY(img_id)
);

DROP TABLE IF EXISTS dajia.user_order;
CREATE TABLE IF NOT EXISTS dajia.user_order (
	order_id BIGINT(25) NOT NULL AUTO_INCREMENT,
	product_id BIGINT(25) NOT NULL,
	user_contact_id BIGINT(25) NOT NULL,
    user_id BIGINT(25) NOT NULL,
    payment_id BIGINT(25) NOT NULL,
    quantity INT,
    unit_price NUMERIC(10,2),
    total_price NUMERIC(10,2),
    order_status INT,
    order_date TIMESTAMP NULL,
    deliver_date TIMESTAMP NULL,
    close_date TIMESTAMP NULL,
    pay_type INT,
	created_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	modified_date TIMESTAMP NULL,
    is_active VARCHAR(5) NOT NULL DEFAULT 'Y',
	PRIMARY KEY(order_id)
);

DROP TABLE IF EXISTS dajia.property;
CREATE TABLE IF NOT EXISTS dajia.property (
	property_id BIGINT(25) NOT NULL AUTO_INCREMENT,
	property_key VARCHAR(100) NOT NULL,
    property_value VARCHAR(1000),
    comments VARCHAR(1000),
	created_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	modified_date TIMESTAMP NULL,
    is_active VARCHAR(5) NOT NULL DEFAULT 'Y',
	PRIMARY KEY(property_id),
    UNIQUE KEY (property_key)
);

DROP TABLE IF EXISTS dajia.location;
CREATE TABLE IF NOT EXISTS dajia.location (
	id BIGINT(25) NOT NULL AUTO_INCREMENT,
	location_key int(11) NOT NULL,
	location_value varchar(20) NOT NULL,
	location_type varchar(20) NOT NULL,
	parent_key int(11) NOT NULL,
	created_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	modified_date TIMESTAMP NULL,
    is_active VARCHAR(5) NOT NULL DEFAULT 'Y',
	PRIMARY KEY  (id),
    UNIQUE KEY (location_key)
);

DROP TABLE IF EXISTS dajia.user_favourite;
CREATE TABLE IF NOT EXISTS dajia.user_favourite (
	favourite_id BIGINT(25) NOT NULL AUTO_INCREMENT,
    user_id BIGINT(25) NOT NULL,
    product_id BIGINT(25) NOT NULL,
	created_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	modified_date TIMESTAMP NULL,
    is_active VARCHAR(5) NOT NULL DEFAULT 'Y',
	PRIMARY KEY(favourite_id)
);
