package com.dajia.task;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dajia.service.ProductService;

@Component
public class ScheduledTasks {
	Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Autowired
	private ProductService productService;

	@Scheduled(cron = "0 */5 *  * * * ")
	public void productUpdateByCron() {
		Date currentDate = new Date();
		logger.info("Product expiration check job starts at: " + dateFormat.format(currentDate));
		productService.updateProductExpireStatus(currentDate);
	}
}
