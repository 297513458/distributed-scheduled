package com.htxx.core.logger.log4j2;

import com.htxx.core.logger.Logger;

public class Log4j2Logger implements Logger {

	private final org.apache.logging.log4j.Logger logger;

	public Log4j2Logger(org.apache.logging.log4j.Logger logger) {
		this.logger = logger;
	}

	public void trace(String message) {
		logger.trace(message);
	}

	public void trace(Throwable e) {
		logger.trace(e);
	}

	public void trace(String message, Throwable e) {
		logger.trace(message, e);
	}

	public void debug(String message) {
		logger.trace(message);
	}

	public void debug(Throwable e) {
		logger.debug(e);
	}

	public void debug(String message, Throwable e) {
		logger.debug(message, e);
	}

	public void info(String message) {
		logger.info(message);
	}

	public void info(Throwable e) {
		logger.info(e);
	}

	public void info(String message, Throwable e) {
		logger.info(message, e);
	}

	public void warn(String message) {
		logger.warn(message);
	}

	public void warn(Throwable e) {
		logger.warn(e);
	}

	public void warn(String message, Throwable e) {
		logger.warn(message, e);
	}

	public void error(String message) {
		logger.error(message);
	}

	public void error(Throwable e) {
		logger.error(e);
	}

	public void error(String message, Throwable e) {
		logger.error(message, e);
	}

	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

}