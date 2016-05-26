package com.dajia;

import javax.servlet.Filter;

import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.dajia.filter.AdminFilter;
import com.dajia.filter.AuthFilter;
import com.dajia.filter.WechatFilter;

@Configuration
@EnableWebMvc
public class ServerConfig extends WebMvcAutoConfigurationAdapter {
	// specific project configuration
	@Bean
	public FilterRegistrationBean authFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(authFilter());
		registration.addUrlPatterns("/user/*");
		// registration.addInitParameter("paramName", "paramValue");
		registration.setName("authFilter");
		return registration;
	}

	@Bean
	public FilterRegistrationBean adminFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(adminFilter());
		registration.addUrlPatterns("/admin/*");
		registration.setName("adminFilter");
		return registration;
	}

	@Bean
	public FilterRegistrationBean wechatFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(wechatFilter());
		registration.addUrlPatterns("/app");
		registration.addUrlPatterns("/app/index.html");
		registration.addUrlPatterns("/app/index.html*");
		registration.setName("wechatFilter");
		return registration;
	}

	@Bean(name = "authFilter")
	public Filter authFilter() {
		return new AuthFilter();
	}

	@Bean(name = "adminFilter")
	public Filter adminFilter() {
		return new AdminFilter();
	}

	@Bean(name = "wechatFilter")
	public Filter wechatFilter() {
		return new WechatFilter();
	}
}
