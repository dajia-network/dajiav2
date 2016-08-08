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

select p.product_id, p.short_name, o.c from product p, (
select product_id, count(1) c from user_order where payment_id is not null and order_status in (2,3,4)
group by product_id) o where o.product_id=p.product_id order by o.c desc