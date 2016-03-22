package com.dajia.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dajia.util.CommonUtils;

@RestController
public class WechatController extends BaseController {
	Logger logger = LoggerFactory.getLogger(WechatController.class);

	@RequestMapping("/wechat")
	public @ResponseBody String wechatShakeHand(HttpServletRequest request) {
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echostr = request.getParameter("echostr");

		logger.info("signature: " + signature);
		logger.info("timestamp: " + timestamp);
		logger.info("nonce: " + nonce);

		String token = CommonUtils.wechat_api_token;
		String tmpStr = "";
		try {
			tmpStr = getSHA1(token, timestamp, nonce);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
		}

		logger.info("+++++++++++++++++++++tmpStr   " + tmpStr);
		logger.info("---------------------signature   " + signature);

		if (!tmpStr.isEmpty() && tmpStr.equals(signature)) {
			return echostr;
		} else {
			return "Validation Failed.";
		}
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
