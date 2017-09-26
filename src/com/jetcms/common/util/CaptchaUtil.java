package com.jetcms.common.util;

import java.util.Random;

/**
 * 验证码
 * 
 * @author chengjie
 * @version 1.0 2016年6月8日
 * @since 1.0
 */
public class CaptchaUtil {

	/**
	 * 生成六位随机码
	 * 
	 */
	public static String generateVerityCode() {
		StringBuilder sRand = new StringBuilder();
		String[] array = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
		Random rand = new Random();
		for (int i = 10; i > 1; i--) {
			int index = rand.nextInt(i);
			String tmp = array[index];
			array[index] = array[i - 1];
			array[i - 1] = tmp;
		}
		for (int i = 6; i > 0; i--) {
			sRand.append(array[i]);
		}
		return sRand.toString();
	}
}
