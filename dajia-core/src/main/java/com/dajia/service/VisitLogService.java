package com.dajia.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dajia.domain.VisitLog;
import com.dajia.repository.VisitLogRepo;

@Service
public class VisitLogService {
	Logger logger = LoggerFactory.getLogger(VisitLogService.class);

	@Autowired
	private VisitLogRepo visitLogRepo;

	public void addShareLog(Long userId, Long refUserId, String productId, Integer logType, String visitUrl) {
		VisitLog visitLog = new VisitLog();
		visitLog.logType = logType;
		visitLog.productId = productId;
		visitLog.userId = userId;
		visitLog.refUserId = refUserId;
		visitLog.visitUrl = visitUrl;
		visitLogRepo.save(visitLog);
	}
}
