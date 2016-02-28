package com.dajia.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dajia.domain.Product;
import com.dajia.domain.User;
import com.dajia.domain.UserOrder;
import com.dajia.repository.ProductRepo;
import com.dajia.service.OrderService;
import com.dajia.service.ProductService;
import com.dajia.service.UserService;
import com.dajia.util.CommonUtils;
import com.dajia.vo.PaginationVO;

@RestController
public class AdminController extends BaseController {
	Logger logger = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	private ProductRepo productRepo;

	@Autowired
	private OrderService orderService;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@RequestMapping("/robotorder/{pid}")
	public @ResponseBody UserOrder robotOrder(@PathVariable("pid") Long pid) {
		UserOrder order = orderService.generateRobotOrder(pid, 1);
		return order;
	}

	@RequestMapping("/sync")
	public @ResponseBody Map<String, String> syncAllProducts() {
		productService.syncProductsAll();
		Map<String, String> map = new HashMap<String, String>();
		map.put("result", "success");
		return map;
	}

	@RequestMapping("/products/{page}")
	public PaginationVO<Product> productsByPage(@PathVariable("page") Integer pageNum) {
		Page<Product> products = productService.loadProductsByPage(pageNum);
		PaginationVO<Product> page = CommonUtils.generatePaginationVO(products, pageNum);
		return page;
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

	@RequestMapping("/users/{page}")
	public PaginationVO<User> usersByPage(@PathVariable("page") Integer pageNum) {
		Page<User> users = userService.loadUersByPage(pageNum);
		PaginationVO<User> page = CommonUtils.generatePaginationVO(users, pageNum);
		return page;
	}
}
