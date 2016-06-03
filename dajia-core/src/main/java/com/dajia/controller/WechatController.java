package com.dajia.controller;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dajia.domain.User;
import com.dajia.repository.PropertyRepo;
import com.dajia.service.ApiService;
import com.dajia.service.UserService;
import com.dajia.util.ApiWechatUtils;
import com.dajia.util.CommonUtils;
import com.dajia.util.RandomString;
import com.dajia.util.UserUtils;
import com.dajia.vo.LoginUserVO;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Controller
public class WechatController extends BaseController {
	Logger logger = LoggerFactory.getLogger(WechatController.class);

	@Autowired
	private ApiService apiService;

	@Autowired
	private UserService userService;

	@Autowired
	private PropertyRepo propertyRepo;

	@RequestMapping("/wechat/login")
	public String wechatLogin(HttpServletRequest request) {
		String refUserId = request.getParameter(CommonUtils.ref_user_id);
		String productId = request.getParameter(CommonUtils.product_id);
		String orderId = request.getParameter(CommonUtils.order_id);
		logger.info("refUserId:" + refUserId + "||productId:" + productId + "||orderId:" + orderId);
		String url = apiService.getWechatOauthUrl(refUserId, productId, orderId);
		return "redirect:" + url;
	}

	@RequestMapping("/wechatoauth")
	public String wechatOauth(HttpServletRequest request) {
		LoginUserVO loginUser = null;
		String code = request.getParameter("code");
		String state = request.getParameter("state");
		logger.info("get state from Wechat: " + state);
		Map<String, String> userInfoMap = null;
		try {
			userInfoMap = apiService.loadWechatUserInfo(code);
		} catch (JsonParseException e) {
			logger.error(e.getMessage(), e);
		} catch (JsonMappingException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		if (null != userInfoMap && userInfoMap.containsKey("openid")) {
			String openid = userInfoMap.get("openid");
			User user = userService.oauthLogin(ApiWechatUtils.wechat_oauth_type, openid, userInfoMap, request);
			loginUser = UserUtils.addLoginSession(loginUser, user, request);
			request.getSession().setAttribute("oauthLogin", "success");
		}
		if (null != state && !state.equalsIgnoreCase(CommonUtils.state_string)) {
			String[] stateArray = state.split("_");
			if (stateArray.length == 3) {
				String refUserId = stateArray[0];
				String productId = stateArray[1];
				String orderId = stateArray[2];
				return "redirect:app/index.html?refUserId=" + refUserId + "&productId=" + productId + "&orderId="
						+ orderId + "#/tab/prod/" + productId;
			}
		}
		return "redirect:app/index.html";
	}

	@RequestMapping("/wechat")
	public @ResponseBody String wechatShakeHand(HttpServletRequest request) {
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echostr = request.getParameter("echostr");

		logger.info("signature: " + signature);
		logger.info("timestamp: " + timestamp);
		logger.info("nonce: " + nonce);

		String token = ApiWechatUtils.wechat_api_token;
		String tmpStr = "";
		try {
			tmpStr = getSHA1(token, timestamp, nonce);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
		}

		logger.info("+++++++++++++++++++++tmpStr " + tmpStr);
		logger.info("---------------------signature " + signature);

		if (!tmpStr.isEmpty() && tmpStr.equals(signature)) {
			return echostr;
		} else {
			return "Validation Failed.";
		}
	}

	@RequestMapping("/wechat/signature")
	public @ResponseBody Map<String, String> wechatSignature(HttpServletRequest request) {
		Map<String, String> returnMap = new HashMap<String, String>();
		String appId = propertyRepo.findByPropertyKey(ApiWechatUtils.wechat_app_key).propertyValue;
		String timestamp = String.valueOf(System.currentTimeMillis());
		String nonceStr = (new RandomString(8)).nextString();
		String signature = apiService.getWechatSignature(timestamp, nonceStr, request.getHeader("referer"));
		returnMap.put("appId", appId);
		returnMap.put("timestamp", timestamp);
		returnMap.put("nonceStr", nonceStr);
		returnMap.put("signature", signature);
		return returnMap;
	}

	private String getSHA1(String token, String timestamp, String nonce) throws NoSuchAlgorithmException {
		if (null == token || null == timestamp || null == nonce) {
			return "";
		}
		String[] array = new String[] { token, timestamp, nonce };
		StringBuffer sb = new StringBuffer();
		// String sort
		Arrays.sort(array);
		for (int i = 0; i < 3; i++) {
			sb.append(array[i]);
		}
		String str = sb.toString();
		// SHA1 signature generate
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(str.getBytes());
		byte[] digest = md.digest();

		StringBuffer hexstr = new StringBuffer();
		String shaHex = "";
		for (int i = 0; i < digest.length; i++) {
			shaHex = Integer.toHexString(digest[i] & 0xFF);
			if (shaHex.length() < 2) {
				hexstr.append(0);
			}
			hexstr.append(shaHex);
		}
		return hexstr.toString();
	}
}
