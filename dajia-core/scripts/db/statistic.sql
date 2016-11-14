select u.user_id, u.username, ref.count from user u,
(
select r.ref_user_id, count(1) count from (
select distinct ref_user_id, user_id from user_visit_log 
where ref_user_id not in (1,2,11,12,13,20) and user_id not in (1,2,11,12,13,20) and user_id!=ref_user_id 
) r
group by r.ref_user_id
) ref where u.user_id=ref.ref_user_id 
 order by ref.count desc;
 
 
select u.user_id, u.username, ref.c from user u,
(
select ref_user_id, count(1) c from user_visit_log 
where ref_user_id not in (1,2,11,12,13,20) and user_id not in (1,2,11,12,13,20) and user_id!=ref_user_id 
group by ref_user_id
) ref where u.user_id=ref.ref_user_id 
 order by ref.c desc;
 
select * from user_order where payment_id is not null and order_status in (2,3,4) and order_id not in (
select order_id from user_refund where refund_type=0
) and product_item_id in (
select product_item_id from product_item where product_status=3
);

select o.user_id, u.username, o.c from (
select user_id, count(1) c from user_order where payment_id is not null and order_status in (2,3,4) group by user_id
) o, user u where u.user_id=o.user_id order by o.c desc;

select u.user_id, u.username, res.total_price from user u, (
select user_id, sum(total_price) total_price from user_order 
where payment_id is not null and order_status in (2,3,4) group by user_id
) res where res.user_id=u.user_id order by res.total_price desc;

select p.product_id, p.short_name, res2.quantity from product p, (
select res.product_id, sum(res.quantity) quantity from (
select product_id, sum(quantity) quantity from user_order 
where payment_id is not null and order_status in (2,3,4) group by product_id
union
select oi.product_id, sum(oi.quantity) quantity from user_order_item oi, user_order o  
where o.payment_id is not null and o.order_status in (2,3,4) and oi.order_id=o.order_id 
group by oi.product_id
) res group by res.product_id) res2 where res2.product_id=p.product_id order by res2.quantity desc;

select sum(quantity) quantity from user_order 
where payment_id is not null and order_status in (2,3,4)
and order_date between '2016-11-01' AND '2016-12-01'
union
select sum(oi.quantity) quantity from user_order_item oi, user_order o  
where o.payment_id is not null and o.order_status in (2,3,4) and oi.order_id=o.order_id 
and o.order_date between '2016-11-01' AND '2016-12-01';

select count(*) from user where created_date!=last_visit_date;

select sum(total_price) from user_order where payment_id is not null and order_status in (2,3,4);

select count(1) from user_share
union
select count(distinct(visit_user_id)) from user_share;


select o.order_id, oi.order_item_id, o.product_desc, o.quantity, p.name, oi.quantity,
o.contact_name, o.contact_mobile, o.address, o.user_id, o.user_comments, o.user_coupon_ids  
from user_order o left join user_order_item oi on o.order_id=oi.order_id 
left join product p on oi.product_id=p.product_id 
where o.payment_id is not null and o.order_status=2 and o.order_date>'2016-11-10';


select sum(res.refund_value) from (
select user_id, product_id, product_item_id, order_id, refund_value, 0 refund_type, 2 refund_status, now() refund_date 
from (
select res.rf+IFNULL(us.share_count, 0) refund_value, res.* from (
select ((o.unit_price-pi.current_price)*o.quantity) rf, 
o.* from user_order o, product_item pi
where o.product_item_id=pi.product_item_id and pi.product_status=2
and o.is_active='Y' and o.payment_id is not null and o.order_status in (2,3,4) and o.product_item_id is not null
and o.unit_price>pi.current_price and o.order_date>'2016-11-01'
and o.order_id not in 
(select order_id from user_refund where is_active='Y' and refund_type in (0,2))) res
left join 
(select order_id, count(1) share_count from user_share where is_active='Y' group by order_id) us
on res.order_id=us.order_id) final
union
select user_id, null product_id, null product_item_id, order_id, sum(refund_value), 0 refund_type, 2 refund_status, now() refund_date 
from (
select res.rf+IFNULL(us.share_count, 0) refund_value, res.* from (
select ((o.unit_price-pi.current_price)*o.quantity) rf, 
o.* from (
select * from user_order_item where order_id in (
select order_id from user_order where payment_id is not null and order_status in (2,3,4) 
and product_item_id is null and order_date>'2016-11-01')
) o, product_item pi
where o.product_item_id=pi.product_item_id and pi.product_status=3
and o.is_active='Y' and o.unit_price>pi.current_price
and o.order_id not in 
(select order_id from user_refund where is_active='Y' and refund_type in (0,2))) res
left join 
(select order_id, count(1) share_count from user_share where is_active='Y' group by order_id) us
on res.order_id=us.order_id) final group by user_id, order_id) res;


select order_id, tracking_id, total_price, actual_pay, order_date, contact_name, pay_type 
from user_order where order_id in (select order_id from user_refund where refund_status=3)
order by order_id desc;