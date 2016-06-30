package com.dajia.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
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
import com.dajia.domain.UserOrder;
import com.dajia.repository.ProductRepo;
import com.dajia.repository.UserFavouriteRepo;
import com.dajia.repository.UserOrderRepo;
import com.dajia.repository.UserRewardRepo;
import com.dajia.util.ApiKdtUtils;
import com.dajia.util.ApiWdUtils;
import com.dajia.util.CommonUtils;
import com.dajia.util.CommonUtils.ActiveStatus;
import com.dajia.util.CommonUtils.OrderStatus;
import com.dajia.util.CommonUtils.ProductStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProductService {
	Logger logger = LoggerFactory.getLogger(ProductService.class);

	@Autowired
	private ApiService apiService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private RewardService rewardService;

	@Autowired
	private ProductRepo productRepo;

	@Autowired
	private UserFavouriteRepo favouriteRepo;

	@Autowired
	private UserOrderRepo orderRepo;

	@Autowired
	private UserRewardRepo rewardRepo;

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
		List<Product> products = this.loadProductsAllFromApiKdt();
		this.syncProducts(products);
	}

	public void syncProducts(List<Product> products) {
		for (Product product : products) {
			Product p = productRepo.findByRefId(product.refId);
			if (null != p) {
				if (p.isActive.equalsIgnoreCase(CommonUtils.ActiveStatus.NO.toString())) {
					continue; // skip inactive product
				}
				try {
					CommonUtils.copyProductProperties(product, p);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
				productRepo.save(p);
			} else {
				// new product from KDT
				product.originalPrice = product.currentPrice;
				product.productStatus = ProductStatus.INVALID.getKey();
				productRepo.save(product);
			}
		}
	}

	public Product loadProductDetail(Long pid) {
		Product product = productRepo.findOne(pid);
		if (null == product) {
			return null;
		}
		product.productImages.size();

		product.status4Show = getProductStatusStr(product.productStatus);
		calcPrice(product);

		return product;
	}

	public List<Product> loadAllValidProducts() {
		List<Product> products = (List<Product>) productRepo.findByProductStatusAndIsActiveOrderByExpiredDateAsc(
				ProductStatus.VALID.getKey(), ActiveStatus.YES.toString());
		return products;
	}

	public List<Product> loadAllValidProductsWithPrices() {
		List<Product> products = (List<Product>) productRepo.findByProductStatusAndIsActiveOrderByExpiredDateAsc(
				ProductStatus.VALID.getKey(), ActiveStatus.YES.toString());
		for (Product product : products) {
			calcPrice(product);
		}
		return products;
	}

	public Page<Product> loadAllValidProductsWithPricesByPage(Integer pageNum) {
		List<Integer> productStatusList = new ArrayList<Integer>();
		productStatusList.add(ProductStatus.VALID.getKey());
		productStatusList.add(ProductStatus.EXPIRED.getKey());
		Pageable pageable = new PageRequest(pageNum - 1, CommonUtils.page_item_perpage_5);
		Page<Product> products = productRepo.findByProductStatusInAndIsActiveOrderByExpiredDateAsc(productStatusList,
				ActiveStatus.YES.toString(), pageable);
		for (Product product : products) {
			calcPrice(product);
		}
		return products;
	}

	public Page<Product> loadProductsByPage(Integer pageNum) {
		Pageable pageable = new PageRequest(pageNum - 1, CommonUtils.page_item_perpage);
		Page<Product> products = productRepo.findByIsActiveOrderByStartDateDesc(ActiveStatus.YES.toString(), pageable);
		for (Product product : products) {
			product.status4Show = getProductStatusStr(product.productStatus);
		}
		return products;
	}

	@Transactional
	public void productSold(UserOrder order) {
		// update order
		order.orderStatus = OrderStatus.PAIED.getKey();
		orderRepo.save(order);
		// update product price
		Product product = productRepo.findOne(order.productId);
		if (null != product) {
			if (null == product.sold) {
				product.sold = 0L;
			}
			product.sold += order.quantity;
			product.stock -= order.quantity;
			if (product.stock < 0L) {
				product.stock = 0L;
			}
			calcCurrentPrice(product, order.quantity);
		}
		productRepo.save(product);

		if (null != order.refUserId) {
			// generate reward
			rewardService.createReward(order, product);
		}
	}

	public List<Product> loadFavProductsByUserId(Long userId) {
		List<UserFavourite> favourites = favouriteRepo.findByUserIdOrderByCreatedDateDesc(userId);
		List<Long> productIds = new ArrayList<Long>();
		for (UserFavourite favourite : favourites) {
			productIds.add(favourite.productId);
		}
		List<Product> products = (List<Product>) productRepo.findByProductIdInAndIsActive(productIds,
				ActiveStatus.YES.toString());
		for (Product product : products) {
			product.status4Show = getProductStatusStr(product.productStatus);
		}
		return products;
	}

	public void updateProductExpireStatus(Date date) {
		List<Product> products = this.loadAllValidProducts();
		for (Product product : products) {
			if (null == product.expiredDate || product.expiredDate.before(date)) {
				logger.info("Product " + product.name + " (" + product.productId + ") is expired.");
				product.productStatus = ProductStatus.EXPIRED.getKey();
				productRepo.save(product);
				orderService.orderRefund(product);
			}
		}
	}

	private void calcCurrentPrice(Product product, int quantity) {
		List<Price> prices = product.prices;
		for (Price price : prices) {
			// for edge logic
			if (price.sold < product.sold && price.sold > product.sold - quantity) {
				product.currentPrice = price.targetPrice;
				quantity = product.sold.intValue() - price.sold.intValue();
			}
			if (price.sold >= product.sold) {
				BigDecimal priceOff = product.currentPrice.add(price.targetPrice.negate()).divide(
						new BigDecimal(price.sold - product.sold + 1), 2, RoundingMode.HALF_UP);
				product.currentPrice = product.currentPrice.add(priceOff.multiply(new BigDecimal(quantity)).negate());
				break;
			}
		}
	}

	private BigDecimal calcNextOff(Product product) {
		List<Price> prices = product.prices;
		long sold = product.sold == null ? 0L : product.sold + 1;
		for (Price price : prices) {
			if (price.sold >= sold) {
				return product.currentPrice.add(price.targetPrice.negate()).divide(
						new BigDecimal(price.sold - sold + 1), 2, RoundingMode.HALF_UP);
			}
		}
		return null;
	}

	private void calcPrice(Product product) {
		BigDecimal targetPrice = product.originalPrice;
		long soldNeeded = 0L;
		for (Price price : product.prices) {
			if (price.targetPrice.compareTo(targetPrice) < 0) {
				targetPrice = price.targetPrice;
				soldNeeded = price.sold;
			}
		}
		if (null != product.originalPrice) {
			product.targetPrice = targetPrice;
			product.soldNeeded = soldNeeded - CommonUtils.getLongValue(product.sold);
			product.priceOff = product.originalPrice.add(product.currentPrice.negate());
			product.nextOff = calcNextOff(product);
			product.progressValue = calcProgress(product);
		}
	}

	private long calcProgress(Product product) {
		BigDecimal totalOff = product.originalPrice.add(product.targetPrice.negate());
		if (totalOff.longValue() == 0) {
			return 100L;
		}
		Double progress = product.priceOff.divide(totalOff, 2, RoundingMode.HALF_UP).doubleValue() * 100;
		return progress.longValue();
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

	private String getProductStatusStr(Integer key) {
		if (null == key) {
			return null;
		}
		String returnStr = null;
		if (key.equals(ProductStatus.INVALID.getKey())) {
			returnStr = ProductStatus.INVALID.getValue();
		} else if (key.equals(ProductStatus.VALID.getKey())) {
			returnStr = ProductStatus.VALID.getValue();
		} else if (key.equals(ProductStatus.EXPIRED.getKey())) {
			returnStr = ProductStatus.EXPIRED.getValue();
		}
		return returnStr;
	}
}
