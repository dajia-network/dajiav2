package com.dajia.task;

import com.dajia.service.OrderService;
import com.dajia.service.ProductService;
import com.dajia.service.RewardService;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ScheduledTasks {
	Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

	private static final FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd_HH:mm:ss");

	@Autowired
	private ProductService productService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private RewardService rewardService;

	@Scheduled(cron = "0 */10 *  * * * ")
	public void productUpdateByCron() {
		Date currentDate = new Date();
		logger.info("Product expiration check job starts at: " + dateFormat.format(currentDate));
		productService.updateProductExpireStatus(currentDate);
	}

	// @Scheduled(cron = "0 0 12 * * ?")
	public void orderRefundByCron() {
		Date currentDate = new Date();
		logger.info("Order refund job starts at: " + dateFormat.format(currentDate));
		orderService.orderRefundCheck();
	}

	@Scheduled(cron = "0 */20 *  * * * ")
	public void checkRewardByCron() {
		String currentTime = dateFormat.format(new Date());
		rewardService.payRewards(currentTime);
	}
}
