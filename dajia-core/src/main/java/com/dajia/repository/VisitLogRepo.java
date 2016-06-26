package com.dajia.repository;

import org.springframework.data.repository.CrudRepository;

import com.dajia.domain.VisitLog;

public interface VisitLogRepo extends CrudRepository<VisitLog, Long> {
}