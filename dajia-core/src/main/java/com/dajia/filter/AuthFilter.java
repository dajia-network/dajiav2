package com.dajia.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dajia.vo.LoginUserVO;

public class AuthFilter implements Filter {
	Logger logger = LoggerFactory.getLogger(AuthFilter.class);

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {
		logger.info("in AuthFilter...");
		HttpServletRequest request = (HttpServletRequest) req;
		HttpSession session = request.getSession(true);
		LoginUserVO loginUser = (LoginUserVO) session.getAttribute("user");

		HttpServletResponse response = (HttpServletResponse) res;
		if (null == loginUser || null == loginUser.userId) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		chain.doFilter(req, res);
	}

	public void destroy() {
	}

	public void init(FilterConfig arg0) throws ServletException {
	}

}
