package com.htxx.core.logger;

import java.io.Serializable;

import com.htxx.core.logger.LoggerLevel;

public class LoggerDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4368782429116105165L;
	private Throwable throwable;
	private LoggerLevel type;
	private String message;

	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LoggerLevel getType() {
		return type;
	}

	public void setType(LoggerLevel type) {
		this.type = type;
	}
}
