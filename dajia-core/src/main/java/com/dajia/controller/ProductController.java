package com.dajia.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dajia.domain.Product;
import com.dajia.repository.ProductRepo;
import com.dajia.service.ProductService;
import com.dajia.util.CommonUtils;

@RestController
public class ProductController {
	Logger logger = LoggerFactory.getLogger(ProductController.class);

	@Autowired
	private ProductRepo productRepo;

	@Autowired
	private ProductService productService;

	@RequestMapping("/products")
	public List<Product> allProducts() {
		// Pageable pageable = new PageRequest(1, 20);
		List<Product> products = (List<Product>) productRepo
				.findByIsActiveOrderByCreatedDateDesc(CommonUtils.is_active_y);
		return products;
	}

	@RequestMapping("/product/{pid}")
	public Product product(@PathVariable("pid") Long pid) {
		Product product = productRepo.findOne(pid);
		product.productImages.size();
		return product;
	}

	// @RequestMapping("/greeting", method = RequestMethod.GET)
	// public Greeting greeting(@RequestParam(value = "name", defaultValue =
	// "World") String name) {
	// return new Greeting(counter.incrementAndGet(), String.format(template,
	// name));
	// }

}
