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
    is_sales VARCHAR(5) NOT NULL DEFAULT 'N',
	ref_user_id BIGINT(25) NULL,
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
    is_promoted VARCHAR(5) NULL DEFAULT 'N',
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
    product_id BIGINT(25) NULL,
    product_item_id BIGINT(25) NULL,
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
    product_id BIGINT(25) NULL,
    product_item_id BIGINT(25) NULL,
    order_id BIGINT(25) NOT NULL,
    refund_value NUMERIC(10,2),
    refund_type INT,
    refund_status INT,
    api_msg VARCHAR(2000),
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

DROP TABLE IF EXISTS dajia.user_share;
CREATE TABLE IF NOT EXISTS dajia.user_share (
	share_id BIGINT(25) NOT NULL AUTO_INCREMENT,
    user_id BIGINT(25) NOT NULL,
    order_id BIGINT(25) NULL,
    product_id BIGINT(25) NULL,
    product_item_id BIGINT(25) NULL,
    visit_user_id BIGINT(25) NULL,
	visit_username VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
    visit_head_img_url VARCHAR(500) NULL,
    share_type INT,
	created_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	modified_date TIMESTAMP NULL,
    is_active VARCHAR(5) NOT NULL DEFAULT 'Y',
	PRIMARY KEY(share_id)
);

DROP TABLE IF EXISTS dajia.coupon ;
DROP TABLE IF EXISTS dajia.user_coupon ;

CREATE TABLE if not exists dajia.coupon (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(400) NOT NULL, -- 券的名称
  `comment` varchar(4000) , -- 券的说明
  `value` int NOT NULL, -- 券的面额
  `amount` bigint NOT NULL, -- 券的发放数量
  `remain` bigint NOT NULL, -- 剩余数量
  `type` int NOT NULL, -- 券的种类 比如 1.代金券 2.满减券 3.折扣券
  `area` int NOT NULL, -- 可以使用的范围 1.直营 2.店铺 3.通用
  `source_id` bigint NOT NULL default 1, -- 来源 商家ID 默认是1 即打价网
  `status` int NOT NULL, -- 券的状态 主要指 is_active
  `rule_desc` varchar(1000) default '',  -- 使用规则的简要描述 显示在优惠券界面上
  `gmt_expired` datetime null, -- 过期时间
  `gmt_start` datetime null, -- 可以使用的最早时间
  `expired_days` int , -- 有效天数 优先级比上面2个低
  `created_by` varchar(200) NOT NULL, -- 创建人
  `modified_by` varchar(200) NOT NULL, -- 修改人
  `created_date` timestamp null default current_timestamp, -- 创建时间
  `modified_date` timestamp, -- 修改时间
  `is_active` varchar(1) not null default 'Y',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/**
* 用户拥有的代金券 查询索引 user_id, order_id, coupon_id
* 一张券只能而且必须要用于一个订单
**/
create table if not exists dajia.user_coupon (
	`id` int(11) not null auto_increment,
	`user_id` bigint not null, -- 所属用户
	`coupon_id` bigint not null, -- 券的ID
	`order_id` bigint default null, -- 订单号
	`status` int not null, -- 券的状态
	`value` int not null, -- 金额
	`type` int not null, -- 类型 1代金券 2满减券 3折扣券
	`area` int not null, -- 可以使用的范围 1.直营 2.店铺 3.通用
	`comment` varchar(4000), -- 备注信息 后台可见 前台不可见
	`rule_desc` varchar(1000), -- 使用规则的简要说明 显示在券的界面上 比如 "满199元使用"
	`gmt_expired` bigint not null, -- 过期时间
	`gmt_start` bigint not null, -- 可以使用的最早时间
	`created_by` varchar(200) NOT NULL, -- 创建人
	`modified_by` varchar(200) NOT NULL, -- 修改人
	`created_date` timestamp null default current_timestamp, -- 创建时间
	`modified_date` timestamp, -- 修改时间
	`is_active` varchar(1) not null default 'Y',
	primary key (`id`)
) engine=InnoDB default charset=utf8;

alter table dajia.user_order add column actual_pay numeric(10,2);
alter table dajia.user_order add column user_coupon_ids varchar(400);