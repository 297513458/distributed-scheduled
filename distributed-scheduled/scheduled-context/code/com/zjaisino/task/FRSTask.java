package com.zjaisino.task;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.htxx.core.logger.LoggerFactory;

public class FRSTask {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		LoggerFactory.getLogger(FRSTask.class).info(new ClassPathXmlApplicationContext("applicationContext.xml").getId());
	}
}