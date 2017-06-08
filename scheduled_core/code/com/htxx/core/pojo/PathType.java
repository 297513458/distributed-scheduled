package com.htxx.core.pojo;

public enum PathType {
	KAFKALOG("kafka", "/kafka", "卡夫卡日志"), DEPLOY("deploy", "/deploy", "发布的节点"), REGISTERING("registering",
			"/registering", "注册的节点"), TOPNODE("scheduling", "/scheduling", "主节点");
	private String key;

	private String message;
	private String path;

	public String getMessage() {
		return message;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setMessage(String value) {
		this.message = value;
	}

	private PathType(String key, String path, String message) {
		this.key = key;
		this.message = message;
		this.path = path;
	}

}