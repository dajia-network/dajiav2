package com.dajia.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
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
		List<Product> products = productService.loadAllProducts();
		return products;
	}

	@RequestMapping("/product/{pid}")
	public Product product(@PathVariable("pid") Long pid) {
		Product product = productService.loadProductDetail(pid);
		return product;
	}

	@RequestMapping(value = "/product/{pid}", method = RequestMethod.POST)
	public @ResponseBody Product modifyProduct(@PathVariable("pid") Long pid, @RequestBody Product productVO) {
		if (pid == productVO.productId) {
			Product product = productRepo.findOne(pid);
			CommonUtils.updateProductWithReq(product, productVO);
			productRepo.save(product);
			return product;
		} else {
			return null;
		}
	}

	@RequestMapping("/sync")
	public Map<String, String> syncAllProducts() {
		productService.syncProductsAll();
		Map<String, String> map = new HashMap<String, String>();
		map.put("result", "success");
		return map;
	}
}
