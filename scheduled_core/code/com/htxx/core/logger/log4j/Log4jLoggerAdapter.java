package com.htxx.core.logger.log4j;

import org.apache.log4j.LogManager;

import com.htxx.core.logger.Logger;
import com.htxx.core.logger.LoggerAdapter;

public class Log4jLoggerAdapter implements LoggerAdapter {
	
	public Logger getLogger(Class<?> key) {
		return new Log4jLogger(LogManager.getLogger(key));
	}

	public Logger getLogger(String key) {
		return new Log4jLogger(LogManager.getLogger(key));
	}

}