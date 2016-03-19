package com.dajia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RouteController extends BaseController {

	@RequestMapping("/app")
	public String appHome() {
		return "redirect:app/index.html";
	}

	@RequestMapping("/admin")
	public String adminHome() {
		return "redirect:admin/index.html";
	}
}