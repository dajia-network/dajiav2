package com.dajia.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.Cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dajia.domain.Location;
import com.dajia.domain.User;
import com.dajia.domain.UserContact;
import com.dajia.domain.UserFavourite;
import com.dajia.repository.LocationRepo;
import com.dajia.repository.UserRepo;
import com.dajia.service.FavouriteService;
import com.dajia.service.SmsService;
import com.dajia.service.UserService;
import com.dajia.util.CommonUtils;
import com.dajia.util.CommonUtils.LocationType;
import com.dajia.util.EncodingUtil;
import com.dajia.util.UserUtils;
import com.dajia.vo.LocationVO;
import com.dajia.vo.LoginUserVO;
import com.dajia.vo.ReturnVO;

@RestController
public class UserController extends BaseController {
	Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private LocationRepo locationRepo;

	@Autowired
	private UserService userService;

	@Autowired
	private FavouriteService favouriteService;

	@Autowired
	private SmsService smsService;

	@Autowired
	EhCacheCacheManager ehcacheManager;

	@RequestMapping("/signupSms/{mobile}")
	public @ResponseBody ReturnVO signupSms(@PathVariable("mobile") String mobile) {
		String result = smsService.sendSignupMessage(mobile, true);
		ReturnVO rv = new ReturnVO();
		rv.result = result;
		return rv;
	}

	@RequestMapping("/signinSms/{mobile}")
	public @ResponseBody ReturnVO signinSms(@PathVariable("mobile") String mobile) {
		String result = smsService.sendSigninMessage(mobile, true);
		ReturnVO rv = new ReturnVO();
		rv.result = result;
		return rv;
	}

	@RequestMapping("/signupCheck/{mobile}")
	public @ResponseBody ReturnVO signupCheck(@PathVariable("mobile") String mobile) {
		String result = userService.checkMobile(mobile);
		ReturnVO rv = new ReturnVO();
		rv.result = result;
		return rv;
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public @ResponseBody LoginUserVO userLogin(@RequestBody LoginUserVO loginUser, HttpServletRequest request,
			HttpServletResponse response) {
		User user = userService.userLogin(loginUser.mobile, loginUser.password, request, false);
		// if (null == user) {
		// response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		// }
		loginUser = UserUtils.addLoginSession(loginUser, user, request);
		return loginUser;
	}

	@RequestMapping(value = "/smslogin", method = RequestMethod.POST)
	public @ResponseBody LoginUserVO userSmsLogin(@RequestBody LoginUserVO loginUser, HttpServletRequest request,
			HttpServletResponse response) {
		if (null != ehcacheManager.getCacheManager().getCache(CommonUtils.cache_name_signin_code)) {
			Cache cache = ehcacheManager.getCacheManager().getCache(CommonUtils.cache_name_signin_code);
			String signinCode = cache.get(loginUser.mobile).getObjectValue().toString();
			logger.info(signinCode);
			if (null == signinCode || !signinCode.equals(loginUser.signinCode)) {
				return null;
			}
			loginUser.loginIP = request.getRemoteAddr();
			loginUser.loginDate = new Date();

			User user = userService.userLogin(loginUser.mobile, loginUser.password, request, true);
			loginUser = UserUtils.addLoginSession(loginUser, user, request);

			return loginUser;
		} else {
			return null;
		}
	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public @ResponseBody ReturnVO userLogout(@RequestBody String userMobile, HttpServletRequest request) {
		String result = userService.userLogout(userMobile, request);
		ReturnVO rv = new ReturnVO();
		rv.result = result;
		return rv;
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public @ResponseBody LoginUserVO userSignup(@RequestBody LoginUserVO loginUser, HttpServletRequest request) {
		// check sms signup_code
		if (null != ehcacheManager.getCacheManager().getCache(CommonUtils.cache_name_signup_code)) {
			Cache cache = ehcacheManager.getCacheManager().getCache(CommonUtils.cache_name_signup_code);
			String signupCode = cache.get(loginUser.mobile).getObjectValue().toString();
			logger.info(signupCode);
			if (null == signupCode || !signupCode.equals(loginUser.signupCode)) {
				return null;
			}
			loginUser.loginIP = request.getRemoteAddr();
			loginUser.loginDate = new Date();

			User user = new User();
			UserUtils.copyUserProperties(loginUser, user);
			userService.userSignup(user, request);

			loginUser = UserUtils.addLoginSession(loginUser, user, request);
			return loginUser;
		} else {
			return null;
		}
	}

	@RequestMapping("/user/loginuserinfo")
	public @ResponseBody LoginUserVO getSessionUser(HttpServletRequest request, HttpServletResponse response) {
		LoginUserVO loginUser = (LoginUserVO) request.getSession(true).getAttribute(UserUtils.session_user);
		if (null == loginUser) {
			return null;
		}
		User user = this.getLoginUser(request, response, userRepo, true);
		UserUtils.copyUserProperties(user, loginUser);
		// get default userContact
		if (null != user.userContacts && user.userContacts.size() > 0) {
			for (UserContact uc : user.userContacts) {
				if (uc.isDefault.equals("Y")) {
					UserContact userContactInfo = new UserContact();
					try {
						CommonUtils.copyProperties(uc, userContactInfo);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					loginUser.userContact = userContactInfo;
				}
			}
		}
		return loginUser;
	}

	@RequestMapping("/locations")
	public @ResponseBody List<LocationVO> getLocationMap() {
		List<Location> provinces = locationRepo.findByLocationTypeOrderByLocationKey(LocationType.PROVINCE.toString());
		List<Location> cities = locationRepo.findByLocationTypeOrderByLocationKey(LocationType.CITY.toString());
		List<Location> districts = locationRepo.findByLocationTypeOrderByLocationKey(LocationType.AREA.toString());

		List<LocationVO> locationMap = new ArrayList<LocationVO>();
		for (Location province : provinces) {
			LocationVO pvo = new LocationVO();
			pvo.locationKey = province.locationKey;
			pvo.locationValue = province.locationValue;
			List<LocationVO> childrenCity = new ArrayList<LocationVO>();
			for (Location city : cities) {
				if (city.parentKey.equals(pvo.locationKey)) {
					LocationVO cvo = new LocationVO();
					cvo.locationKey = city.locationKey;
					cvo.locationValue = city.locationValue;
					List<LocationVO> childrenDis = new ArrayList<LocationVO>();
					for (Location district : districts) {
						if (district.parentKey.equals(cvo.locationKey)) {
							LocationVO dvo = new LocationVO();
							dvo.locationKey = district.locationKey;
							dvo.locationValue = district.locationValue;
							childrenDis.add(dvo);
						}
					}
					cvo.children = childrenDis;
					childrenCity.add(cvo);
				}
			}
			pvo.children = childrenCity;
			locationMap.add(pvo);
		}
		return locationMap;
	}

	@RequestMapping("/user/favourite/add/{pid}")
	public void addFavourite(@PathVariable("pid") Long pid, HttpServletRequest request, HttpServletResponse response) {
		UserFavourite favourite = new UserFavourite();
		User user = this.getLoginUser(request, response, userRepo, true);
		favourite.userId = user.userId;
		favourite.productId = pid;
		favouriteService.addFavourite(favourite);
	}

	@RequestMapping("/user/favourite/remove/{pid}")
	public void removeFavourite(@PathVariable("pid") Long pid, HttpServletRequest request, HttpServletResponse response) {
		User user = this.getLoginUser(request, response, userRepo, true);
		favouriteService.removeFavourite(user.userId, pid);
	}

	@RequestMapping(value = "/user/changePassword", method = RequestMethod.POST)
	public @ResponseBody Map<String, String> changePassword(@RequestBody Map<String, String> postMap,
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> returnMap = new HashMap<String, String>();
		String oldPassword = postMap.get("oldPassword");
		String newPassword = postMap.get("newPassword");
		User loginUser = this.getLoginUser(request, response, userRepo, true);
		User user = userService.userLogin(loginUser.mobile, oldPassword, request, false);
		if (null == user) {
			returnMap.put("msg", "密码错误");
		} else {
			user.password = EncodingUtil.encode("SHA1", newPassword);
			userRepo.save(user);
			returnMap.put("msg", "修改成功");
		}
		return returnMap;
	}
}
