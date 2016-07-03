package com.dajia.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dajia.domain.Product;
import com.dajia.domain.ProductItem;
import com.dajia.domain.User;
import com.dajia.domain.UserFavourite;
import com.dajia.repository.ProductRepo;
import com.dajia.repository.UserFavouriteRepo;
import com.dajia.repository.UserRepo;
import com.dajia.service.ProductService;
import com.dajia.util.CommonUtils;
import com.dajia.vo.PaginationVO;
import com.dajia.vo.ProductVO;

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
	public List<ProductVO> allProducts() {
		List<ProductVO> products = productService.loadAllValidProductsWithPrices();
		return products;
	}

	@RequestMapping("/products/{page}")
	public PaginationVO<ProductItem> allProductsByPage(@PathVariable("page") Integer pageNum) {
		Page<ProductItem> products = productService.loadAllValidProductsWithPricesByPage(pageNum);
		PaginationVO<ProductItem> page = CommonUtils.generatePaginationVO(products, pageNum);
		return page;
	}

	@RequestMapping("/product/{pid}")
	public ProductVO product(@PathVariable("pid") Long pid) {
		ProductVO productVO = productService.loadProductDetail(pid);
		return productVO;
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
	public List<ProductVO> myFavourites(HttpServletRequest request, HttpServletResponse response) {
		User user = this.getLoginUser(request, response, userRepo, true);
		if (null == user) {
			return null;
		}
		List<Product> products = productService.loadFavProductsByUserId(user.userId);
		return productService.converProductVOListFromP(products);
	}
}
