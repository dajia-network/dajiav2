package com.dajia.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.dajia.domain.UserFavourite;
import com.dajia.repository.ProductRepo;
import com.dajia.repository.UserFavouriteRepo;
import com.dajia.repository.UserRepo;
import com.dajia.service.ProductService;
import com.dajia.util.CommonUtils;
import com.dajia.vo.PaginationVO;

@RestController
public class ProductController extends BaseController {
	Logger logger = LoggerFactory.getLogger(ProductController.class);

	@Autowired
	private ProductRepo productRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private UserFavouriteRepo favouriteRepo;

	@Autowired
	private ProductService productService;

	@RequestMapping("/products")
	public List<Product> allProducts() {
		List<Product> products = productService.loadAllProducts();
		return products;
	}

	@RequestMapping("/products/{page}")
	public PaginationVO<Product> productByPage(@PathVariable("page") Integer pageNum) {
		Page<Product> products = productService.loadProductsByPage(pageNum);
		PaginationVO<Product> page = new PaginationVO<Product>();
		page.results = products.getContent();
		page.totalPages = products.getTotalPages();
		page.totalCount = products.getNumberOfElements();
		page.currentPage = pageNum;
		return page;
	}

	@RequestMapping("/product/{pid}")
	public Product product(@PathVariable("pid") Long pid) {
		Product product = productService.loadProductDetail(pid);
		return product;
	}

	@RequestMapping("/user/checkfav/{pid}")
	public boolean checkFav(@PathVariable("pid") Long pid, HttpServletRequest request) {
		User user = this.getLoginUser(request, null, userRepo, false);
		if (null != user) {
			UserFavourite favourite = favouriteRepo.findByUserIdAndProductId(user.userId, pid);
			if (null != favourite) {
				return true;
			}
		}
		return false;
	}

	@RequestMapping("/user/favourites")
	public List<Product> myFavourites(HttpServletRequest request, HttpServletResponse response) {
		User user = this.getLoginUser(request, response, userRepo, true);
		if (null == user) {
			return null;
		}
		List<Product> products = productService.loadFavProductsByUserId(user.userId);
		return products;
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
	public @ResponseBody Map<String, String> syncAllProducts() {
		productService.syncProductsAll();
		Map<String, String> map = new HashMap<String, String>();
		map.put("result", "success");
		return map;
	}
}
