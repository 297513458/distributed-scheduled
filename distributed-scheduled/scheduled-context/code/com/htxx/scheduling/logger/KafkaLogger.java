package com.htxx.scheduling.logger;

import com.htxx.core.logger.Logger;
import com.htxx.core.logger.LoggerLevel;

public class KafkaLogger extends KafkaLoggerAppender implements Logger {
	private static final Logger logger = new KafkaLogger();

	protected static final Logger getLogger() {
		return logger;
	}

	/**
	 * 输出跟踪信息
	 *
	 * @param message
	 *            信息内容
	 */
	public void trace(String message) {
		log(LoggerLevel.TRACE, message, null);
	}

	/**
	 * 输出跟踪信息
	 *
	 * @param e
	 *            异常信息
	 */
	public void trace(Throwable e) {
		log(LoggerLevel.TRACE, null, e);
	}

	/**
	 * 输出跟踪信息
	 *
	 * @param message
	 *            信息内容
	 * @param e
	 *            异常信息
	 */
	public void trace(String message, Throwable e) {
		log(LoggerLevel.TRACE, message, e);
	}

	/**
	 * 输出调试信息
	 *
	 * @param message
	 *            信息内容
	 */
	public void debug(String message) {
		log(LoggerLevel.DEBUG, message, null);
	}

	/**
	 * 输出调试信息
	 *
	 * @param message
	 *            信息内容
	 */
	public void debug(Throwable e) {
		log(LoggerLevel.DEBUG, null, e);
	}

	/**
	 * 输出调试信息
	 * 
	 * @param message
	 * @param e
	 */
	public void debug(String message, Throwable e) {
		log(LoggerLevel.DEBUG, message, e);
	}

	/**
	 * 输出普通信息
	 * 
	 * @param message
	 * @param e
	 */
	public void info(String message, Throwable e) {
		log(LoggerLevel.INFO, message, e);
	}

	/**
	 * 输出普通信息
	 *
	 * @param message
	 *            信息内容
	 */
	public void info(String message) {
		log(LoggerLevel.INFO, message, null);
	}

	/**
	 * 输出普通信息
	 * 
	 * @param e
	 */
	public void info(Throwable e) {
		log(LoggerLevel.INFO, null, e);
	}

	/**
	 * 输出错误信息
	 * 
	 * @param message
	 */
	public void error(String message) {
		log(LoggerLevel.ERROR, message, null);
	}

	/**
	 * 输出警告信息
	 * 
	 * @param message
	 */
	public void error(Throwable e) {
		log(LoggerLevel.ERROR, null, e);
	}

	/**
	 * 输出错误日志
	 * 
	 * @param message
	 * @param e
	 */
	public void error(String message, Throwable e) {
		log(LoggerLevel.ERROR, message, e);
	}

	/**
	 * 输出警告信息
	 * 
	 * @param message
	 * @param e
	 */
	public void warn(String message, Throwable e) {
		log(LoggerLevel.WARN, message, e);
	}

	/**
	 * 输出警告信息
	 * 
	 * @param message
	 */
	public void warn(String message) {
		log(LoggerLevel.WARN, message, null);
	}

	/**
	 * 输出警告信息
	 * 
	 * @param e
	 */
	public void warn(Throwable e) {
		log(LoggerLevel.WARN, null, e);
	}

}