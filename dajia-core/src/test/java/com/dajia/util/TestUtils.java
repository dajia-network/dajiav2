package com.dajia.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Test;

public class TestUtils {

	@Test
	public void test() throws UnsupportedEncodingException {
		String[] strings = { "æµ¦", "ä¸æµ·", "上海", "æµ¦ä¸æ°åº" };
		for (String string : strings) {
			String res = CommonUtils.stringCharsetConvert(string, "ISO-8859-1");
			System.out.println(string + ":" + res);
		}
		String password = "654321";
		password = EncodingUtil.encode("SHA1", password);
		System.out.println(password);
	}

	@Test
	public void test2() {
		Integer value = 1;
		BigDecimal bigVal = new BigDecimal(new Double(value) / 100);
		System.out.println(bigVal.doubleValue());
	}

	@Test
	public void test3() {
		String str = "redirect:app/index.html#" + null;
		System.out.println(str);
	}
	
	public static void main(String[] args) {
		Long priceSold = 6L;
		Long sold = 5L;
		int quantity = 2;
		BigDecimal currentPrice = new BigDecimal(39.5);
		BigDecimal targetPrice = new BigDecimal(37.7);
		
		if (priceSold < sold && priceSold > sold - quantity) {
			currentPrice = targetPrice;
			quantity = sold.intValue() - priceSold.intValue();
		}
		if (priceSold >= sold) {
			BigDecimal priceOff = currentPrice.add(targetPrice.negate()).divide(
					new BigDecimal(priceSold - sold + 2), 2, RoundingMode.HALF_UP);
			currentPrice = currentPrice.add(priceOff.multiply(new BigDecimal(quantity))
					.negate());
		}
		System.out.println(currentPrice.doubleValue());
	}
}
