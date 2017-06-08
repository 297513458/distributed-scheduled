package com.htxx.core.logger.slf4j;

import com.htxx.core.logger.Logger;
import com.htxx.core.logger.LoggerAdapter;

public class Slf4jLoggerAdapter implements LoggerAdapter {

	public Logger getLogger(String key) {
		return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(key));
	}

	public Logger getLogger(Class<?> key) {
		return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(key));
	}

}