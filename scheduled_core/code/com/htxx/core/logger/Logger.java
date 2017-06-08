package com.htxx.core.logger;

public interface Logger {
	/**
	 * 输出跟踪信息
	 *
	 * @param message
	 *            信息内容
	 */
	public void trace(String message);

	/**
	 * 输出跟踪信息
	 *
	 * @param e
	 *            异常信息
	 */
	public void trace(Throwable e);

	/**
	 * 输出跟踪信息
	 *
	 * @param message
	 *            信息内容
	 * @param e
	 *            异常信息
	 */
	public void trace(String message, Throwable e);

	/**
	 * 输出调试信息
	 *
	 * @param message
	 *            信息内容
	 */
	public void debug(String message);

	/**
	 * 输出调试信息
	 *
	 * @param message
	 *            信息内容
	 */
	public void debug(Throwable e);

	/**
	 * 输出调试信息
	 * 
	 * @param message
	 * @param e
	 */
	public void debug(String message, Throwable e);

	/**
	 * 输出普通信息
	 * 
	 * @param message
	 * @param e
	 */
	public void info(String message, Throwable e);

	/**
	 * 输出普通信息
	 *
	 * @param message
	 *            信息内容
	 */
	public void info(String message);

	/**
	 * 输出普通信息
	 * 
	 * @param e
	 */
	public void info(Throwable e);

	/**
	 * 输出错误信息
	 * 
	 * @param message
	 */
	public void error(String message);

	/**
	 * 输出警告信息
	 * 
	 * @param message
	 */
	public void error(Throwable e);

	/**
	 * 输出错误日志
	 * 
	 * @param message
	 * @param e
	 */
	public void error(String message, Throwable e);

	/**
	 * 输出警告信息
	 * 
	 * @param message
	 * @param e
	 */
	public void warn(String message, Throwable e);

	/**
	 * 输出警告信息
	 * 
	 * @param message
	 */
	public void warn(String message);

	/**
	 * 输出警告信息
	 * 
	 * @param e
	 */
	public void warn(Throwable e);
}