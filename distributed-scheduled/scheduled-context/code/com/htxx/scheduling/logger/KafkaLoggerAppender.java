package com.htxx.scheduling.logger;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.htxx.core.logger.LoggerDTO;
import com.htxx.core.logger.LoggerLevel;
import com.htxx.scheduling.kafka.KafkaClient;

public class KafkaLoggerAppender {

	private static final org.springframework.context.event.ApplicationEventMulticaster APPLICATIONEVENTMULTICASTER = new org.springframework.context.event.SimpleApplicationEventMulticaster() {
		private Executor taskExecutor;

		@Override
		protected Executor getTaskExecutor() {
			if (this.taskExecutor == null)
				this.taskExecutor = Executors.newFixedThreadPool(20);
			return taskExecutor;
		}
	};

	protected static final void log(LoggerLevel type, String message, Throwable e) {
		LoggerDTO event = new LoggerDTO();
		event.setMessage(message);
		event.setType(type);
		event.setThrowable(e);
		APPLICATIONEVENTMULTICASTER.multicastEvent(new LoggerEvent(event));
	}

	static {
		APPLICATIONEVENTMULTICASTER.addApplicationListener(KafkaClient.APPLICATIONLISTENER);
	}

}