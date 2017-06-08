package com.htxx.core.logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.htxx.core.logger.jdk.JdkLoggerAdapter;
import com.htxx.core.logger.log4j.Log4jLoggerAdapter;
import com.htxx.core.logger.log4j2.Log4j2LoggerAdapter;
import com.htxx.core.logger.slf4j.Slf4jLoggerAdapter;
import com.htxx.core.logger.Logger;
import com.htxx.core.logger.LoggerAdapter;

public class LoggerFactory {

	private static LoggerAdapter LOGGERADAPTER;
	private static final ConcurrentMap<String, Logger> LOGGERCONCURRENTMAP = new ConcurrentHashMap<String, Logger>();
	static {
		try {
			org.apache.logging.log4j.Logger.class.getName();
			setLoggerAdapter(new Log4j2LoggerAdapter());
		} catch (Throwable e1) {
			try {
				org.apache.log4j.Logger.class.getName();
				setLoggerAdapter(new Log4jLoggerAdapter());
			} catch (Throwable e2) {
				try {
					org.slf4j.Logger.class.getName();
					setLoggerAdapter(new Slf4jLoggerAdapter());
				} catch (Throwable e3) {
					java.util.logging.Logger.class.getName();
					setLoggerAdapter(new JdkLoggerAdapter());
				}
			}
		}
	}

	public static Logger getLogger(Class<?> key) {
		return getLogger(key.getName());
	}

	public static Logger getLogger() {
		return getLogger(LoggerFactory.class);
	}

	public static Logger getLogger(String key) {
		Logger logger = LOGGERCONCURRENTMAP.get(key);
		if (logger == null) {
			LOGGERCONCURRENTMAP.putIfAbsent(key, LOGGERADAPTER.getLogger(key));
			logger = LOGGERCONCURRENTMAP.get(key);
		}
		return logger;
	}

	private static void setLoggerAdapter(LoggerAdapter loggerAdapter) {
		if (loggerAdapter != null) {
			Logger logger = loggerAdapter.getLogger(LoggerFactory.class.getName());
			logger.info("using logger: " + loggerAdapter.getClass().getName());
			LoggerFactory.LOGGERADAPTER = loggerAdapter;
			for (Map.Entry<String, Logger> entry : LOGGERCONCURRENTMAP.entrySet()) {
				System.err.println(LOGGERADAPTER.getLogger(entry.getKey()));
			}
		}
	}

}