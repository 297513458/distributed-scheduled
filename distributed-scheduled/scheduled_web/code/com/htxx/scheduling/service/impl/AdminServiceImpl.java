package com.htxx.scheduling.service.impl;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.htxx.core.common.KeyUtil;
import com.htxx.scheduling.dao.AdminDAO;
import com.htxx.scheduling.pojo.Admin;
import com.htxx.scheduling.pojo.AdminVO;
import com.htxx.scheduling.service.AdminService;
import com.htxx.scheduling.service.TokenService;

@Service("adminService")
public class AdminServiceImpl implements AdminService {
	@Resource
	private AdminDAO adminDAO;
	@Resource
	private TokenService tokenService;

	@Override
	public Admin login(String name, String password) {
		Admin vo = new Admin();
		if (password != null) {
			vo.setName(name);
			vo.setPassword(password);
			password = KeyUtil.passwordEncrypt(JSON.toJSONString(vo));
		}
		AdminVO admin = this.adminDAO.queryByNameAndPassword(name, password);
		if (admin != null) {
			admin.setPassword(null);
			tokenService.saveToken(admin);
			return admin;

		} else
			return null;
	}

	@Override
	public Boolean updateStatus(Admin entity) {
		return this.updateStatus(entity);
	}
}