package com.dajia.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dajia.domain.User;
import com.dajia.domain.UserOrder;
import com.dajia.domain.UserShare;
import com.dajia.repository.UserRepo;
import com.dajia.repository.UserShareRepo;
import com.dajia.util.CommonUtils;
import com.dajia.vo.ProductVO;

@Service
public class UserShareService {
	Logger logger = LoggerFactory.getLogger(UserShareService.class);

	@Autowired
	private UserShareRepo userShareRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private OrderService orderService;

	@Autowired
	private ProductService productService;

	public void addUserShare(UserShare userShare) {
		userShare.shareType = CommonUtils.ShareType.BUY_SHARE.getKey();

		if (null == userShare.userId || null == userShare.visitUserId || null == userShare.orderId
				|| null == userShare.productId || null == userShare.productItemId) {
			return;
		}
		if (userShare.userId == userShare.visitUserId) {
			return;
		}
		UserOrder order = orderService.findOneOrderByProductItemIdAndUserId(userShare.productItemId, userShare.userId);
		if (null == order) {
			return;
		}
		ProductVO product = productService.loadProductDetailByItemId(userShare.productItemId);
		if (null == product || product.productStatus != CommonUtils.ProductStatus.VALID.getKey()) {
			return;
		}
		if (!product.isPromoted.equalsIgnoreCase(CommonUtils.YesNoStatus.YES.toString())) {
			return;
		}
		List<UserShare> userShares = userShareRepo.findByUserIdAndVisitUserIdAndProductItemIdAndShareType(
				userShare.userId, userShare.visitUserId, userShare.productItemId, userShare.shareType);
		if (null != userShares && userShares.size() > 0) {
			return;
		}
		List<UserShare> userSharesReverse = userShareRepo.findByUserIdAndVisitUserIdAndProductItemIdAndShareType(
				userShare.visitUserId, userShare.userId, userShare.productItemId, userShare.shareType);
		if (null != userSharesReverse && userSharesReverse.size() > 0) {
			return;
		}
		User user = userRepo.findByUserId(userShare.visitUserId);
		if (null == user) {
			return;
		}
		userShare.visitUserName = user.userName;
		userShare.visitHeadImgUrl = user.headImgUrl;

		userShareRepo.save(userShare);
	}
}
