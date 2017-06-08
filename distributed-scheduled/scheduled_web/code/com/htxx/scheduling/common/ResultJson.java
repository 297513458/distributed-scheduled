package com.htxx.scheduling.common;

import java.util.Date;

import org.apache.http.HttpStatus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.htxx.core.common.MessageDTO;

public class ResultJson {
	public static final ThreadLocal<String> TOKENLOCAL = new ThreadLocal<String>();

	public static <T> String toJSONString(T t, Integer code) {
		return toJSONString(t, code, null);
	}

	public static <T> String toJSONString(Integer code, String message) {
		return toJSONString(null, code, message);
	}

	public static <T> String toJSONString(T t, Integer code, String message) {
		MessageDTO<T> msg = new MessageDTO<T>();
		if (code == null)
			code = HttpStatus.SC_OK;
		if (code == HttpStatus.SC_OK)
			msg.setMessage("成功");
		else
			msg.setMessage("错误");
		msg.setCode(code);
		msg.setData(t);
		msg.setToken(token());
		msg.setMessage(message);
		msg.setTime(new Date());
		try {
			return JSON.toJSONString(msg, SerializerFeature.WriteDateUseDateFormat);
		} catch (Exception e) {
		} finally {
			TOKENLOCAL.remove();
		}
		return "";
	}

	public static <T> String toJSONString(T t) {
		return toJSONString(t, null);
	}

	public static String token() {
		return TOKENLOCAL.get();
	}
}