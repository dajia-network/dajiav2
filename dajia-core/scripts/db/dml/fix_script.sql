-- find all orders miss the refund checkpoint and add into retry list
insert into user_refund 
(user_id, product_id, product_item_id, order_id, refund_value, refund_type, refund_status, refund_date) 
select user_id, product_id, product_item_id, order_id, refund_value, 0 refund_type, 2 refund_status, now() refund_date 
from (
select res.rf+IFNULL(us.share_count, 0) refund_value, res.* from (
select ((o.unit_price-pi.current_price)*o.quantity) rf, 
o.* from user_order o, product_item pi
where o.product_item_id=pi.product_item_id and pi.product_status=3
and o.is_active='Y' and o.payment_id is not null and o.order_status in (2,3,4) and o.product_item_id is not null
and o.unit_price>pi.current_price and o.order_date>'2016-10-01'
and o.order_id not in 
(select order_id from user_refund where is_active='Y' and refund_type in (0,2))) res
left join 
(select order_id, count(1) share_count from user_share where is_active='Y' group by order_id) us
on res.order_id=us.order_id) final;

insert into user_refund 
(user_id, product_id, product_item_id, order_id, refund_value, refund_type, refund_status, refund_date) 
select user_id, null product_id, null product_item_id, order_id, sum(refund_value), 0 refund_type, 2 refund_status, now() refund_date 
from (
select res.rf+IFNULL(us.share_count, 0) refund_value, res.* from (
select ((o.unit_price-pi.current_price)*o.quantity) rf, 
o.* from (
select * from user_order_item where order_id in (
select order_id from user_order where payment_id is not null and order_status in (2,3,4) 
and product_item_id is null and order_date>'2016-10-01')
) o, product_item pi
where o.product_item_id=pi.product_item_id and pi.product_status=3
and o.is_active='Y' and o.unit_price>pi.current_price
and o.order_id not in 
(select order_id from user_refund where is_active='Y' and refund_type in (0,2))) res
left join 
(select order_id, count(1) share_count from user_share where is_active='Y' group by order_id) us
on res.order_id=us.order_id) final group by user_id, order_id;

