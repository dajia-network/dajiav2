package com.dajia.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dajia.domain.User;
import com.dajia.domain.UserOrder;
import com.dajia.repository.UserRepo;
import com.dajia.util.ApiWechatUtils;
import com.dajia.util.CommonUtils;
import com.dajia.util.CommonUtils.ActiveStatus;
import com.dajia.util.CommonUtils.YesNoStatus;
import com.dajia.util.EncodingUtil;
import com.dajia.util.UserUtils;
import com.dajia.vo.LoginUserVO;
import com.dajia.vo.SalesVO;

@Service
public class UserService {
	Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private OrderService orderService;

	public String checkMobile(String mobile) {
		String returnVal = CommonUtils.return_val_failed;
		if (null == userRepo.findByMobile(mobile)) {
			returnVal = CommonUtils.return_val_success;
		}
		return returnVal;
	}

	public User userSignup(User user, HttpServletRequest request) {
		user.password = EncodingUtil.encode("SHA1", user.password);
		user.userName = UserUtils.generateUserName(user.mobile);
		user.isAdmin = "N";
		user.isSales = "N";
		user.lastVisitIP = CommonUtils.getRequestIP(request);
		user.lastVisitDate = new Date();
		userRepo.save(user);
		return user;
	}

	public User oauthLogin(String oauthType, String oauthUserId, Map<String, String> userInfoMap, String state,
			HttpServletRequest request) {
		User user = userRepo.findByOauthUserIdAndOauthType(oauthUserId, oauthType);
		if (null == user) {
			user = new User();
			user.oauthType = ApiWechatUtils.wechat_oauth_type;
			user.oauthUserId = oauthUserId;
			user.isAdmin = "N";
			user.isSales = "N";
			if (null != state && !state.equalsIgnoreCase(CommonUtils.state_string)) {
				String[] stateArray = state.split("_");
				if (stateArray.length > 1) {
					String refUserId = stateArray[0];
					user.refUserId = Long.valueOf(refUserId);
				}
			}
		}
		user.lastVisitIP = CommonUtils.getRequestIP(request);
		user.lastVisitDate = new Date();
		ApiWechatUtils.updateWechatUserInfo(user, userInfoMap);
		userRepo.save(user);
		return user;
	}

	public User oauthLogin(String oauthType, String oauthUserId, HttpServletRequest request) {
		User user = userRepo.findByOauthUserIdAndOauthType(oauthUserId, oauthType);
		if (null != user) {
			user.lastVisitIP = CommonUtils.getRequestIP(request);
			user.lastVisitDate = new Date();
			userRepo.save(user);
		}
		return user;
	}

	public User userLogin(String mobile, String password, HttpServletRequest request, boolean authIgnore) {
		User user = userRepo.findByMobile(mobile);
		password = EncodingUtil.encode("SHA1", password);
		if ((null == user || null == user.password || !user.password.equals(password)) && !authIgnore) {
			return null;
		} else {
			user.lastVisitIP = CommonUtils.getRequestIP(request);
			user.lastVisitDate = new Date();
			userRepo.save(user);
		}

		return user;
	}

	public String userLogout(Long userId, HttpServletRequest request) {
		String returnVal = CommonUtils.return_val_failed;
		if (null != userRepo.findByUserId(userId)) {
			request.getSession().setAttribute(UserUtils.session_user, null);
			returnVal = CommonUtils.return_val_success;
		}
		return returnVal;
	}

	public Page<User> loadUsersByPage(Integer pageNum) {
		Pageable pageable = new PageRequest(pageNum - 1, CommonUtils.page_item_perpage);
		Page<User> users = userRepo.findByIsActiveOrderByCreatedDateDesc(ActiveStatus.YES.toString(), pageable);
		return users;
	}

	public Page<User> loadSalesUsersByPage(Integer pageNum) {
		Pageable pageable = new PageRequest(pageNum - 1, CommonUtils.page_item_perpage);
		Page<User> users = userRepo.findByIsSalesAndIsActiveOrderByCreatedDateDesc(YesNoStatus.YES.toString(),
				ActiveStatus.YES.toString(), pageable);
		return users;
	}

	public Page<User> loadUsersByKeywordByPage(String keyword, Integer pageNum) {
		Pageable pageable = new PageRequest(pageNum - 1, CommonUtils.page_item_perpage);
		Page<User> users = userRepo.findByUserNameContainingAndIsActiveOrderByCreatedDateDesc(keyword,
				ActiveStatus.YES.toString(), pageable);
		return users;
	}

	public String bindMobile(Long userId, String mobile) {
		String returnVal = CommonUtils.return_val_failed;
		User user = userRepo.findByUserId(userId);
		if (null != user) {
			user.mobile = mobile;
			userRepo.save(user);
			returnVal = CommonUtils.return_val_success;
		}
		return returnVal;
	}

	public LoginUserVO getUserVO(Long userId) {
		User user = userRepo.findByUserId(userId);
		return UserUtils.getUserVO(user);
	}

	public void modifyUser(Long userId, LoginUserVO userVO) {
		User user = userRepo.findByUserId(userId);
		user.isAdmin = userVO.isAdmin;
		user.isSales = userVO.isSales;
		userRepo.save(user);
	}

	public SalesVO generateSalesVO(User user) {
		SalesVO sales = UserUtils.getSalesVO(user);
		sales.refAmountWTD = new BigDecimal(0);

		Calendar monthStart = Calendar.getInstance();
		monthStart.set(Calendar.DAY_OF_WEEK, 1);
		monthStart.set(Calendar.HOUR_OF_DAY, 0);
		monthStart.set(Calendar.MINUTE, 0);
		monthStart.set(Calendar.SECOND, 0);
		Date startDate = monthStart.getTime();
		List<UserOrder> orderList = orderService.getOrderListBySales(user.userId, startDate, new Date());
		for (UserOrder order : orderList) {
			sales.refAmountWTD = sales.refAmountWTD.add(order.totalPrice);
		}
		sales.refOrderNumWTD = orderList.size();
		List<User> users = userRepo.findByRefUserIdAndCreatedDateBetweenAndIsActive(user.userId, startDate, new Date(),
				CommonUtils.ActiveStatus.YES.toString());
		sales.refUserNumWTD = users.size();

		sales.bonusAmountWTD = sales.refAmountWTD.multiply(new BigDecimal(0.2));
		return sales;
	}
}
