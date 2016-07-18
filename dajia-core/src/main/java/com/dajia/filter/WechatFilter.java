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
import com.dajia.util.CommonUtils;
import com.dajia.util.UserUtils;
import com.dajia.vo.LoginUserVO;

public class WechatFilter implements Filter {
	Logger logger = LoggerFactory.getLogger(WechatFilter.class);

	@Autowired
	private UserService userService;

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		String reqUrl = request.getRequestURI();
		logger.info("requestURL: " + reqUrl);
		HttpSession session = request.getSession(true);
		LoginUserVO loginUser = (LoginUserVO) session.getAttribute(UserUtils.session_user);
		logger.info("hasLoginUser: " + (null != loginUser));
		HttpServletResponse response = (HttpServletResponse) res;

		if ((null == loginUser || null == loginUser.oauthUserId) && reqUrl.indexOf("/wechat/login") == -1) {
			String ua = request.getHeader("user-agent");
			// is Wechat browser
			if (null != ua && ua.toLowerCase().indexOf("micromessenger") > 0) {
				boolean isCookieLogin = false;
				Cookie[] cookies = request.getCookies();
				if (null != cookies) {
					for (Cookie cookie : cookies) {
						String name = cookie.getName();
						if (name.equals("dajia_user_oauth_id")) {
							logger.info("user has cookies...");
							String oauthUserId = cookie.getValue();
							User user = userService.oauthLogin("Wechat", oauthUserId, request);
							if (null != user) {
								loginUser = UserUtils.addLoginSession(loginUser, user, request);
								isCookieLogin = true;
							}
						}
					}
				}
				if (!isCookieLogin) {
					logger.info("user does not have cookies...");
					String refUserId = request.getParameter(CommonUtils.ref_user_id);
					String productId = request.getParameter(CommonUtils.product_id);
					String refOrderId = request.getParameter(CommonUtils.ref_order_id);
					logger.info("refUserId:" + refUserId + "||productId:" + productId);
					if (!CommonUtils.checkParameterIsNull(refUserId)) {
						if (!CommonUtils.checkParameterIsNull(refOrderId)) {
							response.sendRedirect("/wechat/login?refUserId=" + refUserId + "&productId=" + productId
									+ "&refOrderId=" + refOrderId);
						} else {
							response.sendRedirect("/wechat/login?refUserId=" + refUserId + "&productId=" + productId);
						}
					} else if (!CommonUtils.checkParameterIsNull(productId)) {
						response.sendRedirect("/wechat/login?productId=" + productId);
					} else {
						response.sendRedirect("/wechat/login");
					}
					return;
				}
			}
		}
		chain.doFilter(req, res);
	}

	public void destroy() {
	}

	public void init(FilterConfig arg0) throws ServletException {
	}

}
