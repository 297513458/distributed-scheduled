package com.htxx.core.common;

import java.security.MessageDigest;

import org.springframework.util.Assert;

/**
 * MD5 签名校验
 * 
 */
public class MD5Util {

	/**
	 * MD5加密
	 * 
	 * @param data
	 *            被加密的字符串，默认utf-8编码
	 * @return
	 */
	public static String MD5Encode(String data) {
		return MD5Encode(data, "utf-8");
	}

	/**
	 * 使用制定编码加密
	 * 
	 * @param data需要加密的数据
	 * @param encode编码
	 * @return
	 */
	public static String MD5Encode(String data, String encode) {
		Assert.notNull(data, "数据不能为空");
		Assert.notNull(encode, "编码不能为空");
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(data.getBytes(encode));
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer();
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0) {
					i = i + 256;
				}
				if (i < 16) {
					buf.append("0");
				}
				buf.append(Integer.toHexString(i));
			}
			data = buf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data.toLowerCase();
	}

	/**
	 * MD5数字签名验证
	 * 
	 * @param data
	 *            被加密的数据
	 * @param sign
	 *            签名字符串
	 * @return
	 */
	public static boolean checkMD5Signature(String data, String sign) {
		boolean flag = false;
		String encryptStr = MD5Encode(data.trim());
		if (encryptStr.equals(sign)) {
			flag = true;
		}

		return flag;
	}
}
