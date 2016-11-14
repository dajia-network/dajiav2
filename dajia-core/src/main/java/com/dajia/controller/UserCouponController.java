package com.dajia.controller;

import com.dajia.domain.User;
import com.dajia.domain.UserCoupon;
import com.dajia.repository.UserCouponRepo;
import com.dajia.repository.UserRepo;
import com.dajia.service.ProductService;
import com.dajia.service.UserCouponService;
import com.dajia.util.DajiaResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Arrays;

import static com.dajia.util.ResultConstants.COMMON_MSG_QUERY_OK;

/**
 * 优惠券controller
 *
 * Created by huhaonan on 2016/10/22.
 */

@RestController
public class UserCouponController extends BaseController {

	@Autowired
	public UserCouponService userCouponService;

	@Autowired
	public UserCouponRepo userCouponRepo;

	@Autowired
	public ProductService productService;

	@Autowired
	public UserRepo userRepo;

	final static Logger logger = LoggerFactory.getLogger(UserCouponController.class);

	// TODO DajiaResult类需要增加一个Exception 默认为null 在返回后可以打印堆栈的详细信息

	@RequestMapping("/user/coupons/available")
	@ResponseBody
	public DajiaResult getAvailableCoupons(HttpServletRequest request, HttpServletResponse response) {
		User user = getLoginUser(request, response, userRepo, false);
		int size = 10;
		DajiaResult result;
		try {
			result = userCouponService.getAvailableConponsWhenBuy(user.userId);
		} catch (Exception ex) {
			result = DajiaResult.systemError("查询优惠券失败, 系统异常", null, ex);
		}
		logger.info(String.format("%s|%s|%s", "userCoupons", result, user.userId));
		return result;
	}

	@RequestMapping("/user/coupons/{page}")
	@ResponseBody
	public DajiaResult userCoupons(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("page") Integer page) {
		User user = getLoginUser(request, response, userRepo, false);
		page = page > 0 ? (page - 1) : page;
		int size = 10;
		DajiaResult result;
		Long now = System.currentTimeMillis();
		try {
			Page<UserCoupon> userCoupons = userCouponRepo
					.findByUserIdAndGmtExpiredAfterOrderByStatusAscGmtExpiredDesc(user.userId, now, new PageRequest(
							page, size, Sort.Direction.DESC, "gmtExpired"));
			result = DajiaResult.successReturn(COMMON_MSG_QUERY_OK, null, userCoupons);
		} catch (Exception ex) {
			result = DajiaResult.systemError("查询优惠券失败, 系统异常", null, ex);
		}
		String input = String.format("user=%s,page=%d,size=%d", user.userId, page, size);
		logger.info(String.format("%s|%s|%s", "userCoupons", result, input));
		return result;
	}

	@RequestMapping(value = "/user/coupons/request/{couponId}/{amt}", method = RequestMethod.GET)
	@ResponseBody
	public DajiaResult requestCoupon(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("couponId") Long couponId, @PathVariable("amt") Integer amount) {
		User user = getLoginUser(request, response, userRepo, false);
		Long userId = user.userId;

		/** 检查是否已经发放过优惠券 **/
		DajiaResult canRequestResult = userCouponService.canRequest(userId, couponId);
		Boolean can = null != canRequestResult.data;
		if (!can) {
			return canRequestResult;
		}

		DajiaResult result = userCouponService.publishCoupons(couponId, amount, Arrays.asList(user.userId),
				String.valueOf(user.userId));
		String input = String.format("user=%s,couponId=%d", user.userId, couponId);
		logger.info(String.format("succees request coupons|%s|%s", result, input));
		return result;
	}

	/**
	 * 某个用户能不能领取某张优惠券
	 *
	 * @param request
	 * @param response
	 * @param couponId
	 * @return
	 */
	@RequestMapping(value = "/user/coupons/can/{couponId}", method = RequestMethod.GET)
	@ResponseBody
	public DajiaResult canRequest(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("couponId") Long couponId) {
		User user = getLoginUser(request, response, userRepo, false);
		Long userId = user.userId;
		DajiaResult result = userCouponService.canRequest(userId, couponId);
		String input = String.format("user=%s,couponId=%d", userId, couponId);
		logger.info(String.format("%s|%s|%s", "canRequestCoupon", result, input));
		return result;
	}

}
