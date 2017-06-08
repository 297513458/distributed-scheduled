package com.htxx.core.logger.jdk;

import com.htxx.core.logger.Logger;
import com.htxx.core.logger.LoggerAdapter;

public class JdkLoggerAdapter implements LoggerAdapter {

	public Logger getLogger(Class<?> key) {
		return new JdkLogger(java.util.logging.Logger.getLogger(key == null ? "" : key.getName()));
	}

	public Logger getLogger(String key) {
		return new JdkLogger(java.util.logging.Logger.getLogger(key));
	}
}