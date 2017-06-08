package com.htxx.core.logger;

import com.htxx.core.logger.Logger;

public interface LoggerAdapter {
	Logger getLogger(Class<?> key);

	Logger getLogger(String key);
}