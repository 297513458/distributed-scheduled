package com.zjaisino.task;

import org.springframework.stereotype.Service;
import com.htxx.core.annotation.Registering;
import com.htxx.core.logger.Logger;
import com.htxx.core.pojo.SchedulingDTO;
import com.htxx.scheduling.logger.LoggerFactory;

@Service
public class Check {
	Logger logger = LoggerFactory.getKafkaLogger();

	@Registering("删除")
	public void delete() {
		logger.error("delete");
	}

	@Registering("修改")
	public SchedulingDTO update() {
		logger.warn("update");
		return null;
	}

	@Registering
	public String select() {
		logger.info("select");
		return null;
	}
}