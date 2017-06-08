package com.htxx.scheduling.logger;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoggerMulticaster extends org.springframework.context.event.SimpleApplicationEventMulticaster {
	private Executor taskExecutor;

	@Override
	protected Executor getTaskExecutor() {
		if (this.taskExecutor == null)
			this.taskExecutor = Executors.newFixedThreadPool(20);
		return taskExecutor;
	}
}