package com.htxx.core.common;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 * aes加密
 * 
 * @author root
 *
 */
public class AESUtil {
	public static String Decrypt(String sSrc, String sKey) throws Exception {
		try { // 判断Key是否正确
			if (sKey == null) {
				throw new IllegalAccessException("Key为空null");
			}
			// 判断Key是否为16位
			if (sKey.length() != 16) {
				throw new IllegalAccessException("Key长度不是16位");
			}
			byte[] raw = sKey.getBytes("UTF-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] encrypted1 = hex2byte(sSrc);
			byte[] original = cipher.doFinal(encrypted1);
			return new String(original, "UTF-8");
		} catch (Exception e) {
		}
		return null;
	}

	// 判断Key是否正确
	public static String Encrypt(String sSrc, String sKey) {
		try {
			// 判断Key是否为16位
			if (sKey.length() != 16) {
				throw new IllegalAccessException("Key长度不是16位");
			}
			byte[] raw = sKey.getBytes("UTF-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(sSrc.getBytes("UTF-8"));
			return byte2hex(encrypted).toLowerCase();
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 采用base64编码的aes加密算法
	 * 
	 * @param string
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String EncodeBase64AES(String string, String key) throws Exception {
		// 判断Key是否为16位
		if (key.length() != 16) {
			throw new IllegalAccessException("Key长度不是16位");
		}
		byte[] raw = key.getBytes("UTF-8");
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec ivParameterSpec = new IvParameterSpec(raw);
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);
		byte[] encrypted = cipher.doFinal(string.getBytes("UTF-8"));
		return DatatypeConverter.printBase64Binary(encrypted);
	}

	/**
	 * 采用base64编码的aes解密算法
	 * 
	 * @param string
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String DecodeBase64AES(String string, String key) throws Exception {
		// 判断Key是否为16位
		if (key.length() != 16) {
			throw new IllegalAccessException("Key长度不是16位");
		}
		byte[] raw = key.getBytes("UTF-8");
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec ivParameterSpec = new IvParameterSpec(raw);
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);
		byte[] encrypted1 = DatatypeConverter.parseBase64Binary(string);
		byte[] original = cipher.doFinal(encrypted1);
		return new String(original);
	}

	public static byte[] hex2byte(String strhex) {
		if (strhex == null) {
			return null;
		}
		int l = strhex.length();
		if (l % 2 == 1) {
			return null;
		}
		byte[] b = new byte[l / 2];
		for (int i = 0; i != l / 2; i++) {
			b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2), 16);
		}
		return b;
	}

	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase();
	}
}