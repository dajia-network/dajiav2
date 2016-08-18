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


select count(*) from user where created_date!=last_visit_date

select sum(total_price) from user_order where payment_id is not null and order_status in (2,3,4)