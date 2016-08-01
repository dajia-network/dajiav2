CREATE DATABASE IF NOT EXISTS dajia DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

DROP TABLE IF EXISTS dajia.user;
CREATE TABLE IF NOT EXISTS dajia.user (
	user_id BIGINT(25) NOT NULL AUTO_INCREMENT,
	mobile VARCHAR(20) NULL,
	password VARCHAR(200) NULL,
	username VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
	email VARCHAR(100) NULL,
    sex VARCHAR(50) NULL,
    country VARCHAR(100) NULL,
    province VARCHAR(100) NULL,
    city VARCHAR(100) NULL,
    head_img_url VARCHAR(500) NULL,
    is_admin VARCHAR(5) NOT NULL DEFAULT 'N',
	oauth_type VARCHAR(50) NULL,
	oauth_user_id VARCHAR(50) NULL,
	last_visit_date TIMESTAMP NULL,
	last_visit_ip VARCHAR(200) NULL,
	created_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	modified_date TIMESTAMP NULL,
    is_active VARCHAR(5) NOT NULL DEFAULT 'Y',
	PRIMARY KEY(user_id),
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
    img_url_home VARCHAR(1000) NULL,
    img_url_list VARCHAR(1000) NULL,
    sold INT,
    total_sold INT,
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

DROP TABLE IF EXISTS dajia.product_item;
CREATE TABLE IF NOT EXISTS dajia.product_item (
	product_item_id BIGINT(25) NOT NULL AUTO_INCREMENT,
	product_id BIGINT(25) NOT NULL DEFAULT 0,
    sold INT,
    stock INT,
    buy_quota INT,
    product_status INT,
    fix_top INT,
    original_price NUMERIC(10,2),
    current_price NUMERIC(10,2),
    post_fee NUMERIC(10,2),
	start_date TIMESTAMP NULL,
	expired_date TIMESTAMP NULL,
	created_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	modified_date TIMESTAMP NULL,
    is_active VARCHAR(5) NOT NULL DEFAULT 'Y',
	PRIMARY KEY(product_item_id)
);

DROP TABLE IF EXISTS dajia.price;
CREATE TABLE IF NOT EXISTS dajia.price (
	price_id BIGINT(25) NOT NULL AUTO_INCREMENT,
    product_item_id BIGINT(25) NOT NULL,
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
    tracking_id VARCHAR(50) NULL,
	product_id BIGINT(25) NULL,
	product_item_id BIGINT(25) NULL,
    product_desc VARCHAR(800) NULL,
    product_shared VARCHAR(5) NULL DEFAULT 'N',
	user_contact_id BIGINT(25) NULL,
    user_id BIGINT(25) NOT NULL,
    ref_user_id BIGINT(25) NULL,
    ref_order_id BIGINT(25) NULL,
    payment_id VARCHAR(50) NULL,
    quantity INT,
    unit_price NUMERIC(10,2),
    total_price NUMERIC(10,2),
    post_fee NUMERIC(10,2),
    order_status INT,
    order_date TIMESTAMP NULL,
    deliver_date TIMESTAMP NULL,
    close_date TIMESTAMP NULL,
    pay_type INT,
    logistic_agent VARCHAR(100) NULL,
    logistic_tracking_id VARCHAR(200) NULL,
    contact_name VARCHAR(100) NOT NULL,
    contact_mobile VARCHAR(20) NOT NULL,
    address VARCHAR(800),
    comments VARCHAR(4000) NULL,
    user_comments VARCHAR(4000) NULL,
    admin_comments VARCHAR(4000) NULL,
    pingxx_charge VARCHAR(4000) NULL,
	created_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	modified_date TIMESTAMP NULL,
    is_active VARCHAR(5) NOT NULL DEFAULT 'Y',
	PRIMARY KEY(order_id)
);

DROP TABLE IF EXISTS dajia.user_order_item;
CREATE TABLE IF NOT EXISTS dajia.user_order_item (
	order_item_id BIGINT(25) NOT NULL AUTO_INCREMENT,
	order_id BIGINT(25) NOT NULL,
    tracking_id VARCHAR(50) NULL,
	product_id BIGINT(25) NOT NULL,
	product_item_id BIGINT(25) NOT NULL,
    product_shared VARCHAR(5) NULL DEFAULT 'N',
    user_id BIGINT(25) NOT NULL,
    quantity INT,
    unit_price NUMERIC(10,2),
	created_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	modified_date TIMESTAMP NULL,
    is_active VARCHAR(5) NOT NULL DEFAULT 'Y',
	PRIMARY KEY(order_item_id)
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
	min_post_fee int(5) NOT NULL,
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

DROP TABLE IF EXISTS dajia.user_reward;
CREATE TABLE IF NOT EXISTS dajia.user_reward (
	reward_id BIGINT(25) NOT NULL AUTO_INCREMENT,
    ref_user_id BIGINT(25) NOT NULL,
    ref_order_id BIGINT(25) NULL,
    product_id BIGINT(25) NOT NULL,
    product_item_id BIGINT(25) NOT NULL,
    order_id BIGINT(25) NOT NULL,
    order_user_id BIGINT(25) NOT NULL,
    reward_ratio int(11),
    reward_status INT,
	reward_date TIMESTAMP NULL,
	expired_date TIMESTAMP NULL,
	created_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	modified_date TIMESTAMP NULL,
    is_active VARCHAR(5) NOT NULL DEFAULT 'Y',
	PRIMARY KEY(reward_id)
);

DROP TABLE IF EXISTS dajia.user_refund;
CREATE TABLE IF NOT EXISTS dajia.user_refund (
	refund_id BIGINT(25) NOT NULL AUTO_INCREMENT,
    user_id BIGINT(25) NOT NULL,
    product_id BIGINT(25) NOT NULL,
    product_item_id BIGINT(25) NOT NULL,
    order_id BIGINT(25) NOT NULL,
    refund_value NUMERIC(10,2),
    refund_type INT,
	refund_date TIMESTAMP NULL,
	created_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	modified_date TIMESTAMP NULL,
    is_active VARCHAR(5) NOT NULL DEFAULT 'Y',
	PRIMARY KEY(refund_id)
);

DROP TABLE IF EXISTS dajia.user_visit_log;
CREATE TABLE IF NOT EXISTS dajia.user_visit_log (
	visit_log_id BIGINT(25) NOT NULL AUTO_INCREMENT,
    user_id BIGINT(25) NULL,
    ref_user_id BIGINT(25) NULL,
    product_id BIGINT(25) NULL,
    product_item_id BIGINT(25) NULL,
	visit_url varchar(2000) NOT NULL,
	ref_url varchar(2000) NULL,
	visit_ip VARCHAR(20) NULL,
    log_type INT,
	created_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	modified_date TIMESTAMP NULL,
    is_active VARCHAR(5) NOT NULL DEFAULT 'Y',
	PRIMARY KEY(visit_log_id)
);

DROP TABLE IF EXISTS dajia.user_cart;
CREATE TABLE IF NOT EXISTS dajia.user_cart (
	cart_id BIGINT(25) NOT NULL AUTO_INCREMENT,
    user_id BIGINT(25) NOT NULL,
    product_id BIGINT(25) NOT NULL,
    product_item_id BIGINT(25) NULL,
    quantity INT,
	created_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	modified_date TIMESTAMP NULL,
    is_active VARCHAR(5) NOT NULL DEFAULT 'Y',
	PRIMARY KEY(cart_id)
);