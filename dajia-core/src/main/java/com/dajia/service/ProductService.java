package com.dajia.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dajia.domain.Price;
import com.dajia.domain.Product;
import com.dajia.domain.UserFavourite;
import com.dajia.repository.ProductRepo;
import com.dajia.repository.UserFavouriteRepo;
import com.dajia.util.ApiKdtUtils;
import com.dajia.util.ApiWdUtils;
import com.dajia.util.CommonUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProductService {
	Logger logger = LoggerFactory.getLogger(ProductService.class);

	@Autowired
	private ApiService apiService;

	@Autowired
	private ProductRepo productRepo;

	@Autowired
	private UserFavouriteRepo favouriteRepo;

	public List<Product> loadProductsAllFromApiWd() {
		String token = "";
		try {
			token = apiService.loadApiWdToken();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		logger.info("access token: " + token);
		String paramStr = ApiWdUtils.allProductsParamStr();
		String publicStr = ApiWdUtils.allProductsPublicStr(token);
		String allProductsUrl = ApiWdUtils.allProductsUrl();
		logger.info("allProductsUrl: " + allProductsUrl);
		RestTemplate restTemplate = new RestTemplate();
		String retrunJsonStr = restTemplate.getForObject(allProductsUrl, String.class, paramStr, publicStr);
		logger.info("retrunJsonStr: " + retrunJsonStr);
		List<Product> productList = new ArrayList<Product>();
		try {
			productList = this.convertJson2Products(retrunJsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return productList;
	}

	public List<Product> loadProductsAllFromApiKdt() {
		String retrunJsonStr = apiService.sendGet2Kdt(ApiKdtUtils.method_get_onsale_items, null);
		logger.info("retrunJsonStr: " + retrunJsonStr);
		List<Product> productList = new ArrayList<Product>();
		try {
			productList = this.convertJson2Products(retrunJsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return productList;
	}

	public Product loadProductFromApiWd(String refId) {
		String token = "";
		try {
			token = apiService.loadApiWdToken();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		String paramStr = ApiWdUtils.productParamStr(refId);
		String publicStr = ApiWdUtils.productPublicStr(token);
		String productUrl = ApiWdUtils.productUrl();
		logger.info("productUrl: " + productUrl);
		RestTemplate restTemplate = new RestTemplate();
		String retrunJsonStr = restTemplate.getForObject(productUrl, String.class, paramStr, publicStr);
		logger.info("retrunJsonStr: " + retrunJsonStr);
		Product product = new Product();
		try {
			product = this.convertJson2Product(retrunJsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return product;
	}

	public Product loadProductFromApiKdt(String refId) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(ApiKdtUtils.key_refid, refId);
		String retrunJsonStr = apiService.sendGet2Kdt(ApiKdtUtils.method_get_item, params);
		logger.info("retrunJsonStr: " + retrunJsonStr);
		Product product = new Product();
		try {
			product = this.convertJson2Product(retrunJsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return product;
	}

	@Transactional
	public void updateProductPrice(Long productId, BigDecimal price) {
		Product product = productRepo.findOne(productId);
		if (null != product && null != product.refId) {
			String token = "";
			try {
				token = apiService.loadApiWdToken();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
			String paramStr = ApiWdUtils.updateProductParamStr(product.refId, price.toString());
			String publicStr = ApiWdUtils.updateProductPublicStr(token);
			String productUrl = ApiWdUtils.updateProductUrl();
			logger.info("productUrl: " + productUrl);
			RestTemplate restTemplate = new RestTemplate();
			String retrunJsonStr = restTemplate.getForObject(productUrl, String.class, paramStr, publicStr);
			logger.info("retrunJsonStr: " + retrunJsonStr);
			product.currentPrice = price;
			productRepo.save(product);
		}
	}

	public void syncProductsAll() {
		List<Product> productList = this.loadProductsAllFromApiKdt();
		this.syncProducts(productList);
	}

	public void syncProducts(List<Product> products) {
		for (Product product : products) {
			Product p = productRepo.findByRefId(product.refId);
			p.productImages.size();
			if (null != p) {
				try {
					CommonUtils.copyProductProperties(product, p);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
				productRepo.save(p);
			} else {
				product.originalPrice = product.currentPrice;
				productRepo.save(product);
			}
		}
	}

	public Product loadProductDetail(Long pid) {
		Product product = productRepo.findOne(pid);
		product.productImages.size();

		BigDecimal targetPrice = product.originalPrice;
		long soldNeeded = 0L;
		for (Price price : product.prices) {
			if (price.targetPrice.compareTo(targetPrice) < 0) {
				targetPrice = price.targetPrice;
				soldNeeded = price.sold;
			}
		}
		product.targetPrice = targetPrice;
		product.soldNeeded = soldNeeded - CommonUtils.getLongValue(product.sold);
		product.priceOff = product.originalPrice.add(product.currentPrice.negate());
		return product;
	}

	public List<Product> loadAllProducts() {
		// Pageable pageable = new PageRequest(1, 20);
		List<Product> products = (List<Product>) productRepo
				.findByIsActiveOrderByExpiredDateAsc(CommonUtils.ActiveStatus.YES.toString());
		for (Product product : products) {
			product.priceOff = product.originalPrice.add(product.currentPrice.negate());
		}
		return products;
	}

	public Page<Product> loadProductsByPage(Integer pageNum) {
		Pageable pageable = new PageRequest(pageNum - 1, CommonUtils.page_item_perpage);
		Page<Product> products = productRepo.findByIsActiveOrderByExpiredDateAsc(
				CommonUtils.ActiveStatus.YES.toString(), pageable);
		return products;
	}

	public void productSold(Long productId, Integer quantity) {
		Product product = productRepo.findOne(productId);
		if (null != product) {
			if (null == product.sold) {
				product.sold = 0L;
			}
			product.sold += 1;
			product.stock -= 1;
			calcCurrentPrice(product);
		}
		productRepo.save(product);
	}

	public List<Product> loadFavProductsByUserId(Long userId) {
		List<UserFavourite> favourites = favouriteRepo.findByUserIdOrderByCreatedDateDesc(userId);
		List<Long> productIds = new ArrayList<Long>();
		for (UserFavourite favourite : favourites) {
			productIds.add(favourite.productId);
		}
		List<Product> products = (List<Product>) productRepo.findByProductIdIn(productIds);
		return products;
	}

	private void calcCurrentPrice(Product product) {
		List<Price> prices = product.prices;
		for (Price price : prices) {
			if (price.sold >= product.sold) {
				BigDecimal priceOff = product.currentPrice.add(price.targetPrice.negate()).divide(
						new BigDecimal(price.sold - product.sold + 1), 2, RoundingMode.HALF_UP);
				product.currentPrice = product.currentPrice.add(priceOff.negate());
				break;
			}
		}
	}

	private List<Product> convertJson2Products(String jsonStr) throws JsonParseException, JsonMappingException,
			IOException {
		List<Product> productList = new ArrayList<Product>();
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new HashMap<String, Object>();
		map = mapper.readValue(jsonStr, HashMap.class);
		List<Map> itemList = (List) ((Map) map.get("response")).get("items");
		for (Map itemMap : itemList) {
			productList.add(ApiKdtUtils.productMapper(itemMap));
		}
		return productList;
	}

	private Product convertJson2Product(String jsonStr) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new HashMap<String, Object>();
		map = mapper.readValue(jsonStr, HashMap.class);
		Map itemMap = (Map) map.get("response");
		return ApiKdtUtils.productMapper(itemMap);
	}
}
