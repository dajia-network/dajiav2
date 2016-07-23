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