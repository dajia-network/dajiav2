INSERT INTO dajia.product (
	short_name,
	name,
    brief,
    description,
    spec,
    order_num,
    max_order,
    original_price,
    current_price,
    target_price,
    start_date,
    expired_date
	) 
VALUES(
	'迪奥魅惑唇膏玩色狂想系列',
    '迪奥魅惑唇膏玩色狂想系列',
    '一款全新风格，一个颠覆性时尚解码：全新Dior迪奥魅惑唇膏玩色狂想系列，采用晶炫酷黑包装，尽显谜漾深邃、晶透纯粹及闪耀光芒。',
    '...',
    '...',
    65,
    100,
    480,
    247,
    150,
    '2015-12-01 00:00:01',
    '2015-12-07 00:00:01'
    );

INSERT INTO dajia.product (
	short_name,
	name,
    brief,
    description,
    spec,
    order_num,
    max_order,
    original_price,
    current_price,
    target_price,
    start_date,
    expired_date
	) 
VALUES(
	'倩碧润肤乳-啫喱配方',
    '倩碧润肤乳-啫喱配方',
    '皮肤科医生研发无油保湿配方，与肌肤自然滋润成分如出一辙。',
    '...',
    '...',
    28,
    100,
    295,
    205,
    120,
    '2015-12-01 00:00:01',
    '2015-12-07 00:00:01'
    );

INSERT INTO dajia.product (
	short_name,
	name,
    brief,
    description,
    spec,
    order_num,
    max_order,
    original_price,
    current_price,
    target_price,
    start_date,
    expired_date
	) 
VALUES(
	'香奈儿邂逅活力淡香水50ml',
    '香奈儿邂逅活力淡香水50ml',	'这款全新的活力淡香水给人以动力，犹如幸运之神赐予的强大能量，不是乍现的灵光，而是无尽活力的源泉，怡人的葡萄柚-血橙复合香调跟随脉搏一起跳动，激发无穷的蓬勃的活力。',
    '...',
    '...',
    19,
    100,
    550,
    482,
    240,
    '2015-12-01 00:00:01',
    '2015-12-07 00:00:01'
    );
    
    
INSERT INTO dajia.product_img (
	product_id,
    sort,
    img_type,
    location,
    path
	) 
VALUES(
	1,
    0,
    1,
    '/Users/Puffy/Works/dajia_upload/dajia-sample-1.jpg',
    '/ionic/upload/productImg/dajia-sample-1.jpg'
    );
    
INSERT INTO dajia.product_img (
	product_id,
    sort,
    img_type,
    location,
    path
	) 
VALUES(
	2,
    0,
    1,
    '/Users/Puffy/Works/dajia_upload/dajia-sample-2.jpg',
    '/ionic/upload/productImg/dajia-sample-2.jpg'
    );
    
INSERT INTO dajia.product_img (
	product_id,
    sort,
    img_type,
    location,
    path
	) 
VALUES(
	3,
    0,
    1,
    '/Users/Puffy/Works/dajia_upload/dajia-sample-3.jpg',
    '/ionic/upload/productImg/dajia-sample-3.jpg'
    );
    
INSERT INTO dajia.product_img (
	product_id,
    sort,
    img_type,
    location,
    path
	) 
VALUES(
	1,
    0,
    2,
    '/Users/Puffy/Works/dajia_upload/dajia-company-1.jpg',
    '/ionic/upload/productImg/dajia-company-1.jpg'
    );
    
INSERT INTO dajia.product_img (
	product_id,
    sort,
    img_type,
    location,
    path
	) 
VALUES(
	2,
    0,
    2,
    '/Users/Puffy/Works/dajia_upload/dajia-company-2.jpg',
    '/ionic/upload/productImg/dajia-company-2.jpg'
    );
    
INSERT INTO dajia.product_img (
	product_id,
    sort,
    img_type,
    location,
    path
	) 
VALUES(
	3,
    0,
    2,
    '/Users/Puffy/Works/dajia_upload/dajia-company-3.jpg',
    '/ionic/upload/productImg/dajia-company-3.jpg'
    );
    
    
INSERT INTO dajia.property (
	property_key,
    property_value
	) 
VALUES(
    'appkey',
    '642537'
    );
    
INSERT INTO dajia.property (
	property_key,
    property_value
	) 
VALUES(
    'secret',
    '71a748a0c72eb8626eecacf340477846'
    );
    
INSERT INTO dajia.property (
	property_key,
    property_value
	) 
VALUES(
    'token',
    null
    );