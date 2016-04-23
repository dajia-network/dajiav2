package com.dajia.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dajia.domain.User;
import com.dajia.service.UserService;
import com.dajia.util.UserUtils;
import com.dajia.vo.LoginUserVO;

public class AuthFilter implements Filter {
	Logger logger = LoggerFactory.getLogger(AuthFilter.class);

	@Autowired
	private UserService userService;

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {
		// logger.info("in AuthFilter...");
		HttpServletRequest request = (HttpServletRequest) req;
		HttpSession session = request.getSession(true);
		LoginUserVO loginUser = (LoginUserVO) session.getAttribute(UserUtils.session_user);

		HttpServletResponse response = (HttpServletResponse) res;
		if (null == loginUser || null == loginUser.userId) {
			boolean loginSuccess = false;
			// auto login base on user cookies
			Cookie[] cookies = request.getCookies();
			if (null != cookies) {
				for (Cookie cookie : cookies) {
					String name = cookie.getName();
					if (name.equals("dajia_user_oauth_id")) {
						String oauthUserId = cookie.getValue();
						User user = userService.oauthLogin("Wechat", oauthUserId, request);
						if (null != user) {
							loginUser = UserUtils.addLoginSession(loginUser, user, request);
							if (null != loginUser) {
								loginSuccess = true;
							}
						}
					}
				}
				if (!loginSuccess) {
					for (Cookie cookie : cookies) {
						String name = cookie.getName();
						if (name.equals("dajia_user_mobile")) {
							String mobile = cookie.getValue();
							User user = userService.userLogin(mobile, null, request, true);
							if (null != user) {
								loginUser = UserUtils.addLoginSession(loginUser, user, request);
							}
						}
					}
				}
			}
		}
		// if (null == loginUser || null == loginUser.userId) {
		// response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		// }
		chain.doFilter(req, res);
	}

	public void destroy() {
	}

	public void init(FilterConfig arg0) throws ServletException {
	}

}
