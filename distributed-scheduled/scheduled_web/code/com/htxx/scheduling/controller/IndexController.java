package com.htxx.scheduling.controller;

import javax.annotation.Resource;

import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.htxx.core.common.MessageDTO;
import com.htxx.scheduling.common.ResultJson;
import com.htxx.scheduling.pojo.Scheduling;
import com.htxx.scheduling.service.SchedulingService;

@Controller
public class IndexController {
	protected static final Logger log = LogManager.getLogger(IndexController.class);
	@Resource
	private SchedulingService schedulingService;

	@ResponseBody
	@RequestMapping("/index/{clazz:.*}")
	public String method(@PathVariable String clazz, @RequestBody String body) throws Exception {
		log.info(ContextLoader.getCurrentWebApplicationContext());
		MessageDTO<Scheduling> result = JSON.parseObject(body, new TypeReference<MessageDTO<Scheduling>>() {
		});
		// String operate = result.getOperate();
		Scheduling vo = result.getData();
		if (vo == null)
			vo = new Scheduling();
		vo.setClazz(clazz);
		return ResultJson.toJSONString(this.schedulingService.queryByClass(vo));
	}

	@ResponseBody
	@RequestMapping(value = "/index/{clazz:.*}/{method}", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8;")
	public String detail(@PathVariable String clazz, @PathVariable String method) throws Exception {
		try {
			log.info("返回{}数据{}", clazz, method);
			return ResultJson.toJSONString(this.schedulingService.query(clazz, method));
		} catch (Exception e) {
			((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse()
					.setStatus(HttpStatus.SC_BAD_REQUEST);
			return ResultJson.toJSONString(HttpStatus.SC_BAD_REQUEST, "操作失败");
		}
	}

	@ResponseBody
	@RequestMapping("/index/")
	public String list(@RequestBody String body) throws Exception {
		Scheduling vo = JSON.parseObject(body, Scheduling.class);
		return ResultJson.toJSONString(this.schedulingService.queryByClass(vo));
	}

	@ResponseBody
	@RequestMapping(value = "/index/{clazz:.*}/{method}", method = RequestMethod.DELETE)
	public String delete(@PathVariable String clazz, @PathVariable String method, @RequestBody String body)
			throws Exception {
		try {
			log.info("{}{}数据{}", clazz, method, body);
			MessageDTO<Scheduling> result = JSON.parseObject(body, new TypeReference<MessageDTO<Scheduling>>() {
			});
			// String operate = result.getOperate();
			Scheduling vo = result.getData();
			if (vo == null)
				vo = new Scheduling();
			vo.setClazz(clazz);
			vo.setMethod(method);
			return ResultJson.toJSONString(this.schedulingService.delete(vo));
		} catch (Exception e) {
			((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse()
					.setStatus(HttpStatus.SC_BAD_REQUEST);
			return ResultJson.toJSONString(HttpStatus.SC_BAD_REQUEST, "操作失败");
		}
	}

	@ResponseBody
	@RequestMapping(value = "/index/{clazz:.*}/{method}", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8;")
	public String dispose(@PathVariable String clazz, @PathVariable String method, @RequestBody String body)
			throws Exception {
		try {
			log.info("{}{}数据{}", clazz, method, body);
			MessageDTO<Scheduling> result = JSON.parseObject(body, new TypeReference<MessageDTO<Scheduling>>() {
			});
			String operate = result.getOperate();
			Scheduling vo = result.getData();
			if (vo == null)
				vo = new Scheduling();
			vo.setClazz(clazz);
			vo.setMethod(method);
			if ("DEPLOY".equalsIgnoreCase(operate))
				return ResultJson.toJSONString(this.schedulingService.updateDeploy(vo));
			else if ("SAVE".equalsIgnoreCase(operate))
				try {
					return ResultJson.toJSONString(this.schedulingService.save(vo));
				} catch (DuplicateKeyException e) {
					return ResultJson.toJSONString(300, "已经有相同的数据");
				}
			else if ("SYNC".equalsIgnoreCase(operate))
				return ResultJson.toJSONString(this.schedulingService.updateSync(vo));
			else if ("DELETE".equalsIgnoreCase(operate))
				return ResultJson.toJSONString(this.schedulingService.delete(vo));
			else
				return ResultJson.toJSONString(this.schedulingService.queryByClass(vo));
		} catch (Exception e) {
			((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse()
					.setStatus(HttpStatus.SC_BAD_REQUEST);
			return ResultJson.toJSONString(HttpStatus.SC_BAD_REQUEST, "操作失败");
		}
	}

	@ResponseBody
	@RequestMapping("/zk/deploy")
	public String listDeployTask() throws Exception {
		// log.info(ContextLoader.getCurrentWebApplicationContext());
		// MessageDTO<Scheduling> result = JSON.parseObject(null, new
		// TypeReference<MessageDTO<Scheduling>>() {
		// });
		// Scheduling vo = result.getData();
		// if (vo == null)
		// vo = new Scheduling();
		// vo.setClazz(clazz);
		return ResultJson.toJSONString(this.schedulingService.listDeployTask(""));
	}

	@ResponseBody
	@RequestMapping("/zk/registering")
	public String listRegisterTask() throws Exception {
		// log.info(ContextLoader.getCurrentWebApplicationContext());
		// MessageDTO<Scheduling> result = JSON.parseObject(null, new
		// TypeReference<MessageDTO<Scheduling>>() {
		// });
		// Scheduling vo = result.getData();
		// if (vo == null)
		// vo = new Scheduling();
		// vo.setClazz(clazz);
		return ResultJson.toJSONString(this.schedulingService.listRegisteringTask(""));
	}

	@ResponseBody
	@RequestMapping(value = "/zk/{clazz:.*}/{method}", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8;")
	public String detailzk(@PathVariable String clazz, @PathVariable String method) throws Exception {
		try {
			log.info("返回{}数据{}", clazz, method);
			return ResultJson.toJSONString(this.schedulingService.getDeployScheduling(clazz, method));
		} catch (Exception e) {
			((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse()
					.setStatus(HttpStatus.SC_BAD_REQUEST);
			return ResultJson.toJSONString(HttpStatus.SC_BAD_REQUEST, "操作失败");
		}
	}
}