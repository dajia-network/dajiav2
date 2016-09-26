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
) o, user u where u.user_id=o.user_id order by o.c desc

select u.user_id, u.username, res.total_price from user u, (
select user_id, sum(total_price) total_price from user_order 
where payment_id is not null and order_status in (2,3,4) group by user_id
) res where res.user_id=u.user_id order by res.total_price desc

select p.product_id, p.short_name, res2.quantity from product p, (
select res.product_id, sum(res.quantity) quantity from (
select product_id, sum(quantity) quantity from user_order 
where payment_id is not null and order_status in (2,3,4) group by product_id
union
select oi.product_id, sum(oi.quantity) quantity from user_order_item oi, user_order o  
where o.payment_id is not null and o.order_status in (2,3,4) and oi.order_id=o.order_id 
group by oi.product_id
) res group by res.product_id) res2 where res2.product_id=p.product_id order by res2.quantity desc

select sum(quantity) quantity from user_order 
where payment_id is not null and order_status in (2,3,4)
and order_date between '2016-09-01' AND '2016-10-01'
union
select sum(oi.quantity) quantity from user_order_item oi, user_order o  
where o.payment_id is not null and o.order_status in (2,3,4) and oi.order_id=o.order_id 
and o.order_date between '2016-09-01' AND '2016-10-01'

select count(*) from user where created_date!=last_visit_date

select sum(total_price) from user_order where payment_id is not null and order_status in (2,3,4)

select count(1) from user_share
union
select count(distinct(visit_user_id)) from user_share

select * from user_refund where order_id in (
select order_id from user_refund where refund_type=1 
group by product_item_id, order_id
having count(order_id)>1
) and refund_type=1 order by order_id


select * from (
select * from user_order where is_active='Y' 
and payment_id is not null and order_status in (2,3,4) and product_item_id is not null
and order_id not in 
(select order_id from user_refund where is_active='Y' and refund_type=0 and refund_status=1)
) res where res.created_date between '2016-08-01' and '2016-09-20'


insert into user_refund 
(user_id, product_id, product_item_id, order_id, refund_value, refund_type, refund_status, refund_date) 
select user_id, product_id, product_item_id, order_id, refund_value, 0 refund_type, 2 refund_status, now() refund_date 
from (
select ((o.unit_price-pi.current_price)*o.quantity) refund_value,
o.* from user_order o, product_item pi
where o.product_item_id=pi.product_item_id
and o.is_active='Y' 
and o.payment_id is not null and o.order_status in (2,3,4) and o.product_item_id is not null
and o.order_id not in 
(select order_id from user_refund where is_active='Y' and refund_type=0 and refund_status!=0)
) res where res.refund_value>0 and res.created_date between '2016-08-01' and '2016-09-26'
and res.order_id not in (select order_id from user_refund where refund_status=2);