package com.zjaisino.task;

import org.springframework.stereotype.Service;
import com.htxx.core.annotation.Registering;
import com.htxx.core.logger.Logger;
import com.htxx.core.pojo.SchedulingDTO;
import com.htxx.scheduling.logger.LoggerFactory;

@Service
public class AutoCheckTask {
	Logger logger = LoggerFactory.getKafkaLogger();

	@Registering("一个测试任务")
	public void autoChecked() {
		logger.error("autoChecked任务启动");
		try{
			Thread.currentThread().sleep(2);
			System.out.println("Hello autoChecked");
			logger.error("autoChecked任务启动");
			                    } catch (InterruptedException e) {  
	                        // TODO Auto-generated catch block  
	                        e.printStackTrace();  
	                    }
			logger.error("autoChecked任务启动10");}

	@Registering("一个测试任务")
	public SchedulingDTO checked() {
		logger.warn("checked任务启动");
		return null;
	}

	@Registering
	public String send() {
		logger.info("send任务启动");
		return null;
	}
}