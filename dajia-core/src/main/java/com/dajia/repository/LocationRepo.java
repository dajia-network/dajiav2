package com.dajia.repository;

import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.Location;

public interface LocationRepo extends CrudRepository<Location, Long> {

	public Location findByLocationType(String locationType);

	public Location findByParentKey(Long parentKey);
}