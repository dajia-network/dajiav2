package com.dajia.util;

import javax.servlet.http.HttpServletRequest;

import com.dajia.domain.User;
import com.dajia.vo.LoginUserVO;

public class UserUtils {

	public static String session_user = "user";

	public static String generateUserName(Long userId) {
		String userName = "用户" + String.valueOf(userId);
		return userName;
	}

	public static void copyUserProperties(LoginUserVO src, User target) {
		target.email = src.email;
		target.mobile = src.mobile;
		if (null != src.userName) {
			target.userName = src.userName;
		}
		target.password = src.password;
		target.lastVisitDate = src.loginDate;
		target.lastVisitIP = src.loginIP;
	}

	public static LoginUserVO addLoginSession(LoginUserVO loginUser, User user, HttpServletRequest request) {
		if (null == loginUser) {
			loginUser = new LoginUserVO();
		}
		loginUser.userId = user.userId;
		loginUser.userName = user.userName;
		request.getSession().setAttribute(UserUtils.session_user, loginUser);
		return loginUser;
	}

}