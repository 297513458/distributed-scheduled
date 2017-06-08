package com.htxx.core.logger.log4j;

import org.apache.log4j.Level;

import com.htxx.core.logger.Logger;

public class Log4jLogger implements Logger {

	private static final String callerFQCN = Log4jLogger.class.getName();

	private final org.apache.log4j.Logger logger;

	public Log4jLogger(org.apache.log4j.Logger logger) {
		this.logger = logger;
	}

	public void trace(String msg) {
		logger.log(callerFQCN, Level.TRACE, msg, null);
	}

	public void trace(Throwable e) {
		logger.log(callerFQCN, Level.TRACE, e == null ? null : e.getMessage(), e);
	}

	public void trace(String msg, Throwable e) {
		logger.log(callerFQCN, Level.TRACE, msg, e);
	}

	public void debug(String msg) {
		logger.log(callerFQCN, Level.DEBUG, msg, null);
	}

	public void debug(Throwable e) {
		logger.log(callerFQCN, Level.DEBUG, e == null ? null : e.getMessage(), e);
	}

	public void debug(String msg, Throwable e) {
		logger.log(callerFQCN, Level.DEBUG, msg, e);
	}

	public void info(String msg) {
		logger.log(callerFQCN, Level.INFO, msg, null);
	}

	public void info(Throwable e) {
		logger.log(callerFQCN, Level.INFO, e == null ? null : e.getMessage(), e);
	}

	public void info(String msg, Throwable e) {
		logger.log(callerFQCN, Level.INFO, msg, e);
	}

	public void warn(String msg) {
		logger.log(callerFQCN, Level.WARN, msg, null);
	}

	public void warn(Throwable e) {
		logger.log(callerFQCN, Level.WARN, e == null ? null : e.getMessage(), e);
	}

	public void warn(String msg, Throwable e) {
		logger.log(callerFQCN, Level.WARN, msg, e);
	}

	public void error(String msg) {
		logger.log(callerFQCN, Level.ERROR, msg, null);
	}

	public void error(Throwable e) {
		logger.log(callerFQCN, Level.ERROR, e == null ? null : e.getMessage(), e);
	}

	public void error(String msg, Throwable e) {
		logger.log(callerFQCN, Level.ERROR, msg, e);
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
		return logger.isEnabledFor(Level.WARN);
	}

	public boolean isErrorEnabled() {
		return logger.isEnabledFor(Level.ERROR);
	}

}