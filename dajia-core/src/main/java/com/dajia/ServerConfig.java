package com.dajia;

import javax.servlet.Filter;

import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.dajia.filter.AuthFilter;

@Configuration
@EnableWebMvc
public class ServerConfig extends WebMvcAutoConfigurationAdapter {
	// specific project configuration
	@Bean
	public FilterRegistrationBean someFilterRegistration() {

		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(authFilter());
		registration.addUrlPatterns("/user/*");
		// registration.addInitParameter("paramName", "paramValue");
		registration.setName("authFilter");
		return registration;
	}

	@Bean(name = "authFilter")
	public Filter authFilter() {
		return new AuthFilter();
	}
}
