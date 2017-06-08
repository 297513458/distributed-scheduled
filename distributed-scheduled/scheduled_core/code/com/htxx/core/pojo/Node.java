package com.htxx.core.pojo;

public class Node {

	private String type;
	private String clazz;
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private String method;
	private String beanId;
	private String allowHost;
	private String cron;
	private String clients;
	private String path;
	private String platform;
	private String status;
	private String message;

	public String getAllowHost() {
		return allowHost;
	}

	public void setAllowHost(String allowHost) {
		this.allowHost = allowHost;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public String getClients() {
		return clients;
	}

	public void setClients(String clients) {
		this.clients = clients;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getBeanId() {
		return beanId;
	}

	public enum Status {
		DELETE("DELETE", -9, "已经删除"), STOP("CANCEL", -2, "已经停止"), CANCEL("CANCEL", -1, "已经取消"), DEFAULT("DEFAULT", 0,
				"默认"), READY("READY", 1, "就绪"), DEPLOY("DEPLOY", 2, "发布"), RUNNING("RUNNING", 9, "运行中");

		private String key;

		private int value;
		private String message;

		public String getMessage() {
			return message;
		}

		public void setMessage(String value) {
			this.message = value;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		private Status(String key, int value, String message) {
			this.key = key;
			this.value = value;
			this.message = message;
		}

	}

	public void setBeanId(String beanid) {
		this.beanId = beanid;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public enum Type {
		CLASS, METHOD, NODE
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

}