package com.htxx.scheduling.handler;

import org.springframework.context.ApplicationContext;

public class JavaTimerScheduleHandler implements ScheduleHandler {

	@Override
	public void handler(String data, ApplicationContext applicationContext) throws Exception {
	}

	@Override
	public String pause(String task) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String add(String task) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String remove(String task) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String resume(String task) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String start(String task) {
		return null;
	}

	@Override
	public void cancelAll() {
	}
}