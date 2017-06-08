package com.htxx.scheduling.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.htxx.core.common.CommonUtil;
import com.htxx.core.pojo.Node;
import com.htxx.scheduling.dao.SchedulingDAO;
import com.htxx.scheduling.pojo.Scheduling;
import com.htxx.scheduling.registrar.SchedulingRegistrar;
import com.htxx.scheduling.service.SchedulingService;

@Service("schedulingService")
public class SchedulingServiceImpl implements SchedulingService {
	@Resource
	private SchedulingDAO schedulingDAO;

	@Resource
	private SchedulingRegistrar schedulingRegistrar;

	public List<Scheduling> queryByClass(Scheduling vo) {
		return this.schedulingDAO.queryByClass(vo);
	}

	public boolean deploy(String type, String beanid, String method, String cron) {
		return Boolean.TRUE;
	}

	@Override
	public Scheduling query(String clazz, String method) {
		return this.schedulingDAO.queryByScheduling(clazz, method);
	}

	@Override
	public Boolean updateStatus(Scheduling entity) {
		String status = entity.getStatus();
		entity = this.schedulingDAO.queryByScheduling(entity.getClazz(), entity.getMethod());
		if (entity != null)
			entity.setStatus(status.toUpperCase());
		if (schedulingRegistrar.updateStatus(entity.getPlatform(), entity.getClazz(), entity.getMethod(),
				entity.getStatus()))
			return this.schedulingDAO.updateStatus(entity) > 0;
		else
			return false;
	}

	@Override
	public Boolean updateSync(Scheduling entity) {
		entity = this.schedulingDAO.queryByScheduling(entity.getClazz(), entity.getMethod());
		if (entity != null) {
			if (schedulingRegistrar.updateStatus(entity.getPlatform(), entity.getClazz(), entity.getMethod(),
					entity.getStatus())) {
				entity.setSyncStatus(true);
				return this.schedulingDAO.updateSync(entity) > 0;
			}
		}
		return false;
	}

	/**
	 * 查询zk中的任务信息
	 */
	public Node getDeployScheduling(String clazz, String method) {
		return this.schedulingRegistrar.getDeployScheduling(clazz, method);
	}

	/**
	 * 查询zk中的任务信息
	 */
	public List<Node> listRegisteringTask(String platform) {
		return this.schedulingRegistrar.listRegisteringTask(platform);
	}

	/**
	 * 查询zk中的任务信息
	 */
	public List<Node> listDeployTask(String platform) {
		return this.schedulingRegistrar.listDeployTask(platform);
	}

	@Override
	public Boolean updateDeploy(Scheduling entity) {
		entity = this.schedulingDAO.queryByScheduling(entity.getClazz(), entity.getMethod());
		if (entity != null)
			entity.setStatus(Node.Status.DEPLOY.getKey());
		if (schedulingRegistrar.deploy(entity.getClazz(), entity.getBeanId(), entity.getMethod(), entity.getCron())) {
			entity.setSyncTime(new Date());
			entity.setSyncStatus(true);
			return this.schedulingDAO.updateStatus(entity) > 0;
		} else
			return false;
	}

	@Override
	public Boolean save(Scheduling entity) {
		if (entity == null)
			return Boolean.FALSE;
		entity.setId(UUID.randomUUID().toString());
		if (entity.getBeanId() == null) {
			String[] info = entity.getClazz().split("[.]");
			entity.setBeanId(CommonUtil.firstToLower(info[info.length - 1]));
		}
		if (entity.getStatus() == null)
			entity.setStatus(Node.Status.DEFAULT.getKey());
		entity.setSyncStatus(false);
		return this.schedulingDAO.insert(entity) > 0;
	}

	@Override
	public Boolean delete(Scheduling entity) {
		if (entity != null)
			if (this.schedulingRegistrar.delete(entity.getClazz(), entity.getMethod()))
				return this.schedulingDAO.delete(entity) > 0;
		return false;
	}
}