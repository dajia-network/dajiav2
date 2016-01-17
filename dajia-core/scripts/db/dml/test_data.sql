insert into location(location_key, location_value, location_type, parent_key, is_active) 
select province_id, province, 'province', 0, 'Y' from province;

insert into location(location_key, location_value, location_type, parent_key, is_active) 
select city_id, city, 'city', parent_id, 'Y' from city;

insert into location(location_key, location_value, location_type, parent_key, is_active) 
select area_id, area, 'area', parent_id, 'Y' from area;