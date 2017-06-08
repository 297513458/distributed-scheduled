package com.htxx.scheduling.controller;

import javax.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.htxx.core.common.MessageDTO;
import com.htxx.scheduling.common.ResultJson;
import com.htxx.scheduling.pojo.Admin;
import com.htxx.scheduling.service.AdminService;

@Controller
public class AdminController {
	protected static final Logger log = LogManager.getLogger(AdminController.class);
	@Resource
	private AdminService adminService;

	@ResponseBody
	@RequestMapping("/admin/login")
	public String login(@RequestBody String body) throws Exception {
		Admin vo = JSON.parseObject(body, new TypeReference<MessageDTO<Admin>>() {
		}).getData();
		return ResultJson.toJSONString(this.adminService.login(vo.getName(), vo.getPassword()));
	}
}