package com.htxx.core.logger.log4j2;

import org.apache.logging.log4j.LogManager;

import com.htxx.core.logger.Logger;
import com.htxx.core.logger.LoggerAdapter;

public class Log4j2LoggerAdapter implements LoggerAdapter {

	public Log4j2LoggerAdapter() {
	}

	public Logger getLogger(Class<?> key) {
		return new Log4j2Logger(LogManager.getLogger(key));
	}

	public Logger getLogger(String key) {
		return new Log4j2Logger(LogManager.getLogger(key));
	}

}