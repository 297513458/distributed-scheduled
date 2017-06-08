package com.htxx.scheduling.handler;

import org.springframework.context.ApplicationContext;

public interface ScheduleHandler {
	/**
	 * 处理请求
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public void handler(String data, ApplicationContext applicationContext) throws Exception;

	/**
	 * 取消任务
	 * 
	 * @return
	 */
	public void cancelAll();

	/**
	 * 暂停某任务
	 * 
	 * @param task
	 * @return
	 */
	public String pause(String task);

	/**
	 * 添加新任务
	 * 
	 * @param path
	 * @return
	 */
	public String add(String task);

	/**
	 * 删除某任务
	 * 
	 * @param task
	 * @return
	 */
	public String remove(String task);

	/**
	 * 重新启动任务
	 * 
	 * @param task
	 * @return
	 */
	public String resume(String task);

	/**
	 * 处理请求
	 * 
	 * @param task
	 * @return
	 */
	public String start(String task);
}