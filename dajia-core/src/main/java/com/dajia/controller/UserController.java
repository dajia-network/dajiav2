package com.dajia.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dajia.domain.User;
import com.dajia.repository.UserRepo;
import com.dajia.service.UserService;
import com.dajia.util.CommonUtils;
import com.dajia.vo.LoginUserVO;

@RestController
public class UserController {
	Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/user/login", method = RequestMethod.POST)
	public @ResponseBody LoginUserVO userLogin(@RequestBody LoginUserVO loginUser) {
		User user = userRepo.findByMobile(loginUser.mobile);
		loginUser.userId = user.userId;
		return loginUser;
	}

	@RequestMapping(value = "/user/signup", method = RequestMethod.POST)
	public @ResponseBody LoginUserVO userSignup(@RequestBody LoginUserVO loginUser, HttpServletRequest request) {
		loginUser.loginIP = request.getRemoteAddr();
		loginUser.loginDate = new Date();
		
		User user = new User();
		CommonUtils.copyUserProperties(loginUser, user);
		userService.userSignup(user);

		loginUser.userId = user.userId;
		loginUser.userName = user.userName;
		return loginUser;
	}
}
