package com.dajia.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dajia.domain.Product;
import com.dajia.repository.ProductRepo;

@RestController
public class ProductController {
	Logger logger = LoggerFactory.getLogger(ProductController.class);

	@Autowired
	private ProductRepo productRepo;

	@RequestMapping("/products")
	public List<Product> allProducts() {
		// Pageable pageable = new PageRequest(1, 20);
		List<Product> result = (List<Product>) productRepo.findAll();
		return result;
	}

	// @RequestMapping("/greeting")
	// public Greeting greeting(@RequestParam(value="name",
	// defaultValue="World") String name) {
	// return new Greeting(counter.incrementAndGet(),
	// String.format(template, name));
	// }
}
