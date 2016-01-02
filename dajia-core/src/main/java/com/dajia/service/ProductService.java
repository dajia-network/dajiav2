package com.dajia.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dajia.domain.Product;
import com.dajia.repository.ProductRepo;
import com.dajia.util.ApiUtils;
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

	public List<Product> loadProductsAllFromApi() {
		String token = "";
		try {
			token = apiService.loadApiToken();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		logger.info("access token: " + token);
		String paramStr = ApiUtils.allProductsParamStr();
		String publicStr = ApiUtils.allProductsPublicStr(token);
		String allProductsUrl = ApiUtils.allProductsUrl();
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

	public Product loadProductFromApi(String refId) {
		String token = "";
		try {
			token = apiService.loadApiToken();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		String paramStr = ApiUtils.productParamStr(refId);
		String publicStr = ApiUtils.productPublicStr(token);
		String productUrl = ApiUtils.productUrl();
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

	@Transactional
	public void updateProductPrice(Long productId, BigDecimal price) {
		Product product = productRepo.findOne(productId);
		if (null != product && null != product.refId) {
			String token = "";
			try {
				token = apiService.loadApiToken();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
			String paramStr = ApiUtils.updateProductParamStr(product.refId, price.toString());
			String publicStr = ApiUtils.updateProductPublicStr(token);
			String productUrl = ApiUtils.updateProductUrl();
			logger.info("productUrl: " + productUrl);
			RestTemplate restTemplate = new RestTemplate();
			String retrunJsonStr = restTemplate.getForObject(productUrl, String.class, paramStr, publicStr);
			logger.info("retrunJsonStr: " + retrunJsonStr);
			product.currentPrice = price;
			productRepo.save(product);
		}
	}

	public void syncProductsAll() {
		List<Product> productList = this.loadProductsAllFromApi();
		this.syncProducts(productList);
	}

	public void syncProducts(List<Product> products) {
		for (Product product : products) {
			Product p = productRepo.findByRefId(product.refId);
			if (null != p) {
				try {
					CommonUtils.copyProperties(product, p);
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

	private List<Product> convertJson2Products(String jsonStr) throws JsonParseException, JsonMappingException,
			IOException {
		List<Product> productList = new ArrayList<Product>();
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new HashMap<String, Object>();
		map = mapper.readValue(jsonStr, HashMap.class);
		List<Map> itemList = (List) ((Map) map.get("result")).get("items");
		for (Map itemMap : itemList) {
			productList.add(this.productMapper(itemMap));
		}
		return productList;
	}

	private Product convertJson2Product(String jsonStr) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new HashMap<String, Object>();
		map = mapper.readValue(jsonStr, HashMap.class);
		Map itemMap = (Map) map.get("result");
		return this.productMapper(itemMap);
	}

	private Product productMapper(Map itemMap) {
		Product product = new Product();
		product.refId = (String) itemMap.get("itemid");
		product.name = (String) itemMap.get("item_name");
		product.brief = (String) itemMap.get("item_desc");
		product.stock = ((Integer) itemMap.get("stock")).longValue();
		product.currentPrice = new BigDecimal((String) itemMap.get("price"));
		product.productImagesExt = (List<String>) itemMap.get("imgs");
		product.productImagesThumbExt = (List<String>) itemMap.get("thumb_imgs");
		return product;
	}
}
