package com.dajia.controller;

import com.dajia.domain.User;
import com.dajia.domain.UserCoupon;
import com.dajia.repository.UserRepo;
import com.dajia.service.UserCouponService;
import com.dajia.util.DajiaResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
    public UserRepo userRepo;

    final static Logger logger = LoggerFactory.getLogger(UserCouponController.class);

    // TODO DajiaResult类需要增加一个Exception 默认为null 在返回后可以打印堆栈的详细信息

    @RequestMapping("/user/coupons/{page}")
    @ResponseBody
    public DajiaResult userCoupons(HttpServletRequest request, HttpServletResponse response, @PathVariable("page") Integer page) {
        User user = getLoginUser(request, response, userRepo, false);
        DajiaResult result;
        page  = page > 0 ? (page - 1) : page;
        int size = 10;
        try {
            Page<UserCoupon> userCoupons = userCouponService.userCouponRepo.findByUserIdOrderByStatusAscGmtExpiredDesc(user.userId, new PageRequest(page, size, Sort.Direction.DESC, "gmtExpired"));
            result = DajiaResult.successReturn(COMMON_MSG_QUERY_OK, null, userCoupons);
        } catch (Exception ex) {
            result = DajiaResult.systemError("查询优惠券失败, 系统异常", null, ex);
        }
        String input = String.format("user=%s,page=%d,size=%d", user.userId, page, size);
        logger.info(String.format("%s|%s|%s", "userCoupons", result, input));
        return result;
    }

    @RequestMapping("/user/coupons/request/{couponId}")
    @ResponseBody
    public DajiaResult requestCoupon(HttpServletRequest request, HttpServletResponse response, @PathVariable("couponId") Long couponId) {

        User user = getLoginUser(request, response, userRepo, false);
        // TODO check user null ?
        // TODO check if user already has this coupon ?
        Long userId = user.userId;

        // TODO Put it to service method
        try {
            UserCoupon userCoupon =  userCouponService.userCouponRepo.findByUserIdAndCouponId(userId, couponId);
            if (null != userCoupon) {
                return DajiaResult.inputError("领券失败, 您已经领取过这张优惠券", null);
            }
        } catch (Exception ex) {
            return DajiaResult.systemError("领券失败, 系统异常, 请稍后重试", null, ex);
        }

        DajiaResult result = userCouponService.publishCoupons(couponId, Arrays.asList(new Long[] { user.userId}), String.valueOf(user.userId));
        String input = String.format("user=%s,couponId=%d", user.userId, couponId);
        logger.info(String.format("%s|%s", "requestCoupon", result, input));
        return result;
    }
}
