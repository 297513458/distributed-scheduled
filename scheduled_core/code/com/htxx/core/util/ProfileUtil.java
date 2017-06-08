package com.htxx.core.util;

import java.util.Properties;
import com.htxx.core.logger.Logger;
import com.htxx.core.logger.LoggerFactory;
import com.htxx.core.util.ProfileUtil;

public class ProfileUtil {
	private static Logger logger = LoggerFactory.getLogger(ProfileUtil.class);
	private static String ZookeeperServers = null;
	private static String authinfoPassword = null;
	private static String authinfoScheme = null;
	private static Boolean enableKafkalog = Boolean.FALSE;
	private static String servicePath = null;
	private static String serverId = null;

	static {
		Properties p = new Properties();
		try {
			p.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("scheduling.pro"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ZookeeperServers = p.getProperty("zookeeper.servers", "");
		authinfoScheme = p.getProperty("zookeeper.authinfo.scheme", "digest");
		serverId = p.getProperty("servers.id", "");
		authinfoPassword = p.getProperty("zookeeper.authinfo.password");
		try {
			enableKafkalog = Boolean.parseBoolean(p.getProperty("kafka.log", "false"));
		} catch (Exception e) {
		}
		servicePath = p.getProperty("zookeeper.serverPath", "");
	}

	public static Logger getLogger() {
		return logger;
	}

	public static String getZookeeperServers() {
		return ZookeeperServers;
	}

	public static String getAuthinfoPassword() {
		return authinfoPassword;
	}

	public static String getAuthinfoScheme() {
		return authinfoScheme;
	}

	public static Boolean getEnableKafkalog() {
		return enableKafkalog;
	}

	public static String getServicePath() {
		return servicePath;
	}

	public static String getServerId() {
		return serverId;
	}

}