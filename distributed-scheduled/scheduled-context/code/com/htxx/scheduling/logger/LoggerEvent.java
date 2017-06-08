package com.htxx.scheduling.logger;

import org.springframework.context.ApplicationEvent;

import com.htxx.core.logger.LoggerDTO;

public class LoggerEvent extends ApplicationEvent {

	private static final long serialVersionUID = -3877784101034532834L;

	public LoggerEvent(LoggerDTO source) {
		super(source);
	}

}