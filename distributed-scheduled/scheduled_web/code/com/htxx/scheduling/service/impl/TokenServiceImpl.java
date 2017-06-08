package com.htxx.scheduling.service.impl;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.htxx.core.common.MD5Util;
import com.htxx.scheduling.common.ResultJson;
import com.htxx.scheduling.common.SpringLogInterceptor;
import com.htxx.scheduling.pojo.AdminVO;
import com.htxx.scheduling.service.TokenService;

@Service("tokenService")
public class TokenServiceImpl implements TokenService {

	@Value("${token.key}")
	private String tokenKey;
	@Resource
	private RedisTemplate<String, AdminVO> redisTemplate;

	@Override
	public AdminVO getToken(String token) {
		return redisTemplate.opsForValue().get(token);
	}

	@Override
	public String saveToken(AdminVO t) {
		if (t == null)
			return null;
		if (t.getRandomCode() == null)
			t.setRandomCode(UUID.randomUUID().toString());
		String token = MD5Util.MD5Encode(JSON.toJSONString(t), "utf-8");
		redisTemplate.opsForValue().set(token, t, 20, TimeUnit.MINUTES);
		AdminVO admin = getToken(token);
		if (admin != null) {
			ResultJson.TOKENLOCAL.set(token);
			SpringLogInterceptor.CURRENTUSERLOCAL.set(admin);
			delete(t.getRandomCode());
		}
		return ResultJson.TOKENLOCAL.get();
	}

	@Override
	public void delete(String token) {
		try {
			this.redisTemplate.delete(token);
		} catch (Exception e) {
		}
	}

	@Override
	public Boolean checkToken(String token) {
		Boolean result = Boolean.FALSE;
		try {
			AdminVO t = getToken(token);
			if (t != null) {
				String newtoken = MD5Util.MD5Encode(JSON.toJSONString(t), "utf-8");
				result = newtoken.equals(token);
				if (result) {
					t.setRandomCode(token);
					this.saveToken(t);
				}
			}
		} catch (Exception e) {
		}
		return result;
	}

}
