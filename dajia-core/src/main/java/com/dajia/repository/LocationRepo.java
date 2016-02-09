package com.dajia.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.Location;

public interface LocationRepo extends CrudRepository<Location, Long> {

	public List<Location> findByLocationTypeOrderByLocationKey(String locationType);

	public List<Location> findByParentKeyOrderByLocationKey(Long parentKey);

	public Location findByLocationKey(Long locationKey);
}