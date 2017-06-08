package com.htxx.core.common;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CommonUtil {

	/**
	 * 把整型转为byte类型
	 * 
	 * @param i
	 * @return
	 */
	public static byte[] intToByte(int data) {
		byte[] result = new byte[4];
		result[0] = (byte) ((data >> 24) & 0xFF);
		result[1] = (byte) ((data >> 16) & 0xFF);
		result[2] = (byte) ((data >> 8) & 0xFF);
		result[3] = (byte) (data & 0xFF);
		return result;
	}

	/**
	 * 把byte转为整型
	 * 
	 * @param b
	 * @return
	 */
	public static int byteToInt(byte[] b) {
		return (b[3] & 0XFF) | (b[2] & 0XFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0XFF) << 24;
	}

	/**
	 * 潜复制，对不同对象的相同属性进行赋值，被改变的是des
	 * 
	 * @param source
	 * @param desObject目标对象
	 */
	public static void simpleValueCopy(Object source, Object desObject) {
		if (source == null || desObject == null)
			return;
		Method[] sourceMethods = source.getClass().getMethods();
		Method[] desMethods = desObject.getClass().getMethods();
		for (Method m : sourceMethods) {
			String methodName = m.getName();
			if (methodName.startsWith("get")) {
				String methodTmp = methodName.replaceFirst("get", "set");
				for (Method desMethod : desMethods) {
					try {
						if (methodTmp.equals(desMethod.getName())) {
							desMethod.invoke(desObject, m.invoke(source, new Object[] {}));
						}
					} catch (Exception e) {
					}
				}
			} else if (methodName.startsWith("is")) {
				String methodTmp = methodName.replaceFirst("is", "set");
				for (Method desMethod : desMethods) {
					try {
						if (methodTmp.equals(desMethod.getName())) {
							desMethod.invoke(desObject, m.invoke(source, new Object[] {}));
						}
					} catch (Exception e) {
					}
				}
			}
		}
	}

	/**
	 * 产生六位随机数，前面兩位是日期其他四位是随机数
	 * 
	 * @return
	 */
	public static String createRandomNumber() {
		Integer random = new Random().nextInt(9999);
		if (random != null && random < 1000) {
			random += 1000;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(new SimpleDateFormat("dd").format(new Date()));
		sb.append(random);
		return sb.toString();
	}

	private static final BigDecimal THOUSANDS = new BigDecimal("1000");

	/**
	 * 对金额进行除1000操作，默认精确到小数点两位
	 * 
	 * @param money金额
	 * @param scale精度
	 *            小數點的位數
	 * 
	 * @return
	 */
	public static String changeMoney(String money, int scale) {
		try {
			BigDecimal b = new BigDecimal(money).divide(THOUSANDS);
			if (b.toString().indexOf(".") > 0) {
				if (scale <= 0)
					b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
				else
					b = b.setScale(scale, BigDecimal.ROUND_HALF_UP);
			}
			return b.toString();
		} catch (Exception e) {
		}
		return null;
	}

	public static String firstToLower(String word) {
		if (word == null || word.trim().length() < 1)
			return null;
		char[] chars = new char[1];
		chars[0] = word.charAt(0);
		String temp = new String(chars);
		if (chars[0] >= 'A' && chars[0] <= 'Z') {// 当为字母时，则转换为小写
			return word.replaceFirst(temp, temp.toLowerCase());
		}
		return word;
	}

	public static void main(String[] args) {
		System.err.println(firstToLower(""));
	}
}