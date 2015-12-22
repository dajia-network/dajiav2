package com.dajia.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

	@RequestMapping("/products")
	public List test2() {
		List resulst = new ArrayList();
		return resulst;
	}

	// @RequestMapping("/greeting")
	// public Greeting greeting(@RequestParam(value="name",
	// defaultValue="World") String name) {
	// return new Greeting(counter.incrementAndGet(),
	// String.format(template, name));
	// }
}
