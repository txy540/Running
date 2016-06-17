package com.example.util;

import java.math.BigDecimal;

public class Utils {
	/**
	 * 用 BigDecimal实现保留两位小数
	 */
	public static double getValueWith2Suffix(double dbl) {
		try {
			BigDecimal bg = new BigDecimal(dbl);
			// 保留2位小数
			return bg.setScale(2, BigDecimal.ROUND_CEILING).doubleValue();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}