package com.htxx.core.util;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

import com.htxx.core.logger.Logger;
import com.htxx.core.logger.LoggerFactory;
import com.htxx.core.util.NativeUtil;

public class NativeUtil {
	private static Logger logger = LoggerFactory.getLogger(NativeUtil.class);

	public static String ipport(String socketString) {
		String os = System.getProperty("os.name");
		if (socketString == null)
			return null;
		String key = socketString.split(",")[0].split(";")[0];
		if (os.toLowerCase().startsWith("win")) {
			return windowIpport(key);
		} else {
			return linuxIpport(key);
		}
	}

	public static String linuxIpport(String socketString) {
		String[] command = new String[] { "/bin/sh", "-c", "netstat -natpl|grep " + socketString };
		Process process;
		try {
			process = Runtime.getRuntime().exec(command);
			LineNumberReader reader = new LineNumberReader(new InputStreamReader(process.getInputStream(), "utf-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.indexOf(socketString) > 0 && line.indexOf("ESTABLISHED") > 0)
					break;
			}
			if (line != null) {
				String[] port = line.split(" ");
				for (String p : port) {
					if (p.indexOf(":") >= 0 && !socketString.equalsIgnoreCase(p))
						return p;
				}
			}
		} catch (Exception e) {
			logger.warn("获取ip地址错误", e);
		}
		return null;
	}

	public static void main(String[] args) {
		System.out.println(ipport("192.168.210.66:2181"));
	}

	public static String windowIpport(String socketString) {
		try {
			Process process = Runtime.getRuntime().exec("cmd /c netstat -nao");
			String line = null;
			LineNumberReader reader = new LineNumberReader(new InputStreamReader(process.getInputStream(), "gbk"));
			while ((line = reader.readLine()) != null) {
				if (line.indexOf(socketString) > 0 && line.indexOf("ESTABLISHED") > 0)
					break;
			}
			if (line != null) {
				String[] port = line.split(" ");
				for (String p : port) {
					if (p.indexOf(":") >= 0 && !socketString.equalsIgnoreCase(p))
						return p;
				}
			}
		} catch (Exception e) {
			logger.warn("获取ip地址错误", e);
		}
		return null;
	}
}
