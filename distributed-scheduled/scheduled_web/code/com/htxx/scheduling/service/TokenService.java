package com.htxx.scheduling.service;

import com.htxx.scheduling.pojo.AdminVO;

public interface TokenService {

	/**
	 * 把数据放入redis并且返回token
	 * 
	 * @param t
	 * @return
	 */
	public String saveToken(AdminVO t);

	/**
	 * 验证token
	 * 
	 * @param token
	 * @param t
	 * @return
	 */
	public Boolean checkToken(String token);

	/**
	 * 获取token
	 * 
	 * @param token
	 * @return
	 */
	public AdminVO getToken(String token);

	public void delete(String token);
}