package com.htxx.core.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

public class KeyUtil {
	private static final Logger log = LogManager.getLogger(KeyUtil.class);
	// 必须是16位
	private static final String cookieKey = "12345678!@#$%^&*";
	// 必须是16位
	private static final String COOKIEKEY_COMMON = "1234567812345678";
	private static final String CHARACTER_ENCODEING = "utf-8";

	/**
	 * 简单cookie加密方式
	 * 
	 * @param message
	 * @return
	 */
	public static final String cookieCommonEncrypt(String message) {
		try {
			return AESUtil.Encrypt(message, COOKIEKEY_COMMON);
		} catch (Exception e) {
			log.error("cookie加密出错", e);
		}
		return null;
	}

	/**
	 * 简单cookie解密方式
	 * 
	 * @param message
	 * @return
	 */
	public static final String cookieCommonDencrypt(String message) {
		try {
			return AESUtil.Decrypt(message, COOKIEKEY_COMMON);
		} catch (Exception e) {
			log.error("cookie解密出错", e);
		}
		return null;
	}

	/**
	 * cookie加密方式
	 * 
	 * @param message
	 * @return
	 */
	public static final String cookieEncrypt(String message) {
		try {
			return AESUtil.Encrypt(message, cookieKey);
		} catch (Exception e) {
			log.error("cookie加密出错", e);
		}
		return null;
	}

	/**
	 * cookie解密方式
	 * 
	 * @param message
	 * @return
	 */
	public static final String cookieDencrypt(String message) {
		try {
			return AESUtil.Decrypt(message, cookieKey);
		} catch (Exception e) {
			log.error("cookie解密出错", e);
		}
		return null;
	}

	/**
	 * 切换顺序
	 * 
	 * @param changeword
	 * @return
	 */
	private static final String changeWord(String changeword) {
		StringBuffer message = new StringBuffer();
		int length = changeword.length();
		for (int i = length - 1; i >= 0; i--) {
			message.append(changeword.charAt(i));
		}
		return message.toString();
	}

	/**
	 * 验证码加密方式
	 * 
	 * @param message
	 * @return
	 */
	public static final String checkwordEncrypt(String checkword) {
		try {
			return MD5Util.MD5Encode(changeWord(checkword));
		} catch (Exception e) {
			log.error("验证码加密出错", e);
		}
		return null;
	}

	/**
	 * 验证码是否正确
	 * 
	 * @param message
	 * @param checkwordDencrypt
	 * @return
	 */
	public static final boolean verifyCheckword(String checkword,
			String checkwordDencrypt) {
		try {
			return checkwordEncrypt(checkword).equals(checkwordDencrypt);
		} catch (Exception e) {
			log.error("验证码解密出错");
		}
		return false;
	}

	/**
	 * 客户用户密码加密方式
	 * 
	 * @param message
	 * @return
	 */
	public static final String passwordEncrypt(String message) {
		return changeWord(MD5Util.MD5Encode(MD5Util
				.MD5Encode(changeWord(MD5Util.MD5Encode(message)))));
	}

	/**
	 * 截取指定长度的文字，如果不够长补0
	 * 
	 * @param key
	 * @param length
	 * @return
	 */
	private static String disploseKey(String key, int length) {
		try {
			key = AESUtil.byte2hex(StringUtils.trimWhitespace(key).getBytes(
					"utf-8"));
		} catch (Exception e) {
		}
		if (key == null)
			key = "";
		if (key.length() != length) {
			if (key.length() > length)
				key = key.substring(0, length);
			else {
				StringBuffer sb = new StringBuffer(key);
				int remainLength = length - sb.length();
				for (int s = 0; s < remainLength; s++)
					sb.append("0");
				key = sb.toString();
			}
		}
		return key;
	}

	/**
	 * 验证普通用户密码
	 * 
	 * @param password
	 * @param passwordEncrypt
	 * @return
	 */
	public static final boolean verifyPassword(String password,
			String passwordEncrypt) {
		if (passwordEncrypt(password).equals(passwordEncrypt))
			return true;
		else
			return false;
	}

	/**
	 * 地址编码方式
	 * 
	 * @param message
	 * @return
	 */
	public static final String urlEncode(String message) {
		try {
			return java.net.URLEncoder.encode(message, CHARACTER_ENCODEING);
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 地址解码方式
	 * 
	 * @param message
	 * @return
	 */
	public static final String urlDencode(String message) {
		try {
			return java.net.URLDecoder.decode(message, CHARACTER_ENCODEING);
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 通用编码方式
	 * 
	 * @param message
	 * @param key
	 * @return
	 */
	public static final String commonEncode(String message, String key) {
		try {
			if (key != null && message != null && key.trim().length() > 0
					&& message.trim().length() > 0)
				return AESUtil.EncodeBase64AES(message, disploseKey(key, 16));
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 通用解码方式
	 * 
	 * @param message
	 * @param key
	 * @return
	 */
	public static final String commonDecode(String message, String key) {
		try {
			if (key != null && message != null && key.trim().length() > 0
					&& message.trim().length() > 0)
				return AESUtil.DecodeBase64AES(message, disploseKey(key, 16));
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 采用16位转换的aes加密
	 * 
	 * @param message
	 * @param key
	 * @return
	 */
	public static final String commonHEXEncode(String message, String key) {
		try {
			if (key != null && message != null && key.trim().length() > 0
					&& message.trim().length() > 0)
				return AESUtil.Encrypt(message, disploseKey(key, 16));
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 采用16位转换的aes解密
	 * 
	 * @param message
	 * @param key
	 * @return
	 */
	public static final String commonHEXDecode(String message, String key) {
		try {
			if (key != null && message != null && key.trim().length() > 0
					&& message.trim().length() > 0)
				return AESUtil.Decrypt(message, disploseKey(key, 16));
		} catch (Exception e) {
		}
		return null;
	}
}
