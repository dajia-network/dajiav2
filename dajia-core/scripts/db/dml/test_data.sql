insert into dajia.location(location_key, location_value, location_type, parent_key, is_active) 
select province_id, province, 'province', 0, 'Y' from dajia.province;

insert into dajia.location(location_key, location_value, location_type, parent_key, is_active) 
select city_id, city, 'city', parent_id, 'Y' from dajia.city;

insert into dajia.location(location_key, location_value, location_type, parent_key, is_active) 
select area_id, area, 'area', parent_id, 'Y' from dajia.area;