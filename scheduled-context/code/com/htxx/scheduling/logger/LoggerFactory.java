package com.htxx.scheduling.logger;

import com.htxx.core.logger.Logger;

public class LoggerFactory extends com.htxx.core.logger.LoggerFactory {

	public static Logger getKafkaLogger() {
		return KafkaLogger.getLogger();
	}
}