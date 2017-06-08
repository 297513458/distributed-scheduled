package com.htxx.scheduling.registrar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.htxx.core.logger.Logger;
import com.htxx.core.logger.LoggerFactory;
import com.htxx.core.pojo.Node;
import com.htxx.core.util.NativeUtil;
import com.htxx.core.util.ProfileUtil;
import com.htxx.scheduling.kafka.KafkaClient;

@Component
public class SchedulingRegistrar {
	private String zookeeperServers;
	private static final String PATH = "/scheduling";
	private ZooKeeper zk = null;
	@Resource
	private ApplicationContext applicationContext;
	private static Logger logger = LoggerFactory.getLogger(KafkaClient.class);

	/**
	 * 注册任务到服务器端
	 * 
	 * @param beanid
	 *            spring里的bean id
	 * @param type
	 *            类型
	 * @param method
	 *            方法
	 * @param message
	 *            描述
	 */
	public void deploy(String type, String beanid, String method, String cron) {
		try {
			StringBuffer taskNodePath = new StringBuffer();
			taskNodePath.append(PATH);
			taskNodePath.append("/deploy");
			List<ACL> list = new ArrayList<ACL>();
			ACL e = new ACL(Perms.ALL, new Id(ProfileUtil.getAuthinfoScheme(),
					DigestAuthenticationProvider.generateDigest(ProfileUtil.getAuthinfoPassword())));
			list.add(e);
			Node dto = new Node();
			dto.setBeanId(beanid);
			dto.setClazz(type);
			dto.setPath(taskNodePath.toString());
			dto.setPlatform("");
			taskNodePath.append("/");
			taskNodePath.append(type);
			Stat stat = zk.exists(taskNodePath.toString(), true);
			if (stat == null) {
				zk.create(taskNodePath.toString(), JSON.toJSONString(dto).getBytes("utf-8"), list,
						CreateMode.PERSISTENT);
			} else {
				zk.setData(taskNodePath.toString(), JSON.toJSONString(dto).getBytes("utf-8"), -1);
			}
			taskNodePath.append("/");
			taskNodePath.append(method);
			dto.setCron(cron);
			dto.setAllowHost("*");
			dto.setType(Node.Type.METHOD.name());
			dto.setMethod(method);
			dto.setStatus(Node.Status.READY.getKey());
			Stat methodStat = zk.exists(taskNodePath.toString(), true);
			if (methodStat == null) {
				zk.create(taskNodePath.toString(), JSON.toJSONString(dto).getBytes("utf-8"), list,
						CreateMode.PERSISTENT);
			} else
				zk.setData(taskNodePath.toString(), JSON.toJSONString(dto).getBytes("utf-8"), -1);
			Stat maxClientstat = zk.exists(taskNodePath.toString() + "/maxClient", true);
			if (maxClientstat == null) {
				zk.create(taskNodePath.toString() + "/maxClient", String.valueOf(Integer.MAX_VALUE).getBytes("utf-8"),
						list, CreateMode.PERSISTENT);
			} else {
				zk.setData(taskNodePath.toString() + "/maxClient", String.valueOf(Integer.MAX_VALUE).getBytes("utf-8"),
						-1);
			}
		} catch (Exception e) {
			logger.error("发布任务出错", e);
		}
	}

	Watcher zkwatcher = new Watcher() {
		@Override
		public void process(WatchedEvent event) {
			try {
				if (event.getType() == Watcher.Event.EventType.NodeDataChanged)
					handler(new String(zk.getData(event.getPath(), zkwatcher, null), "utf-8"), applicationContext);
				else if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged)
					handler(new String(zk.getData(event.getPath(), zkwatcher, null), "utf-8"), applicationContext);
				else if (event.getType() == Watcher.Event.EventType.NodeCreated)
					handler(new String(zk.getData(event.getPath(), zkwatcher, null), "utf-8"), applicationContext);
				else if (event.getType() == Watcher.Event.EventType.NodeDeleted)
					handler(new String(zk.getData(event.getPath(), zkwatcher, null), "utf-8"), applicationContext);
				else if (event.getState() == Watcher.Event.KeeperState.Expired) {
					this.wait(3000);
					connection();
				} else if (event.getState() == Watcher.Event.KeeperState.Disconnected) {
					connection();
				} else if (event.getState() == Watcher.Event.KeeperState.AuthFailed) {
					logger.error("连接zookeeper认证失败");
					countdownlatch.countDown();
				} else if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
					logger.error("连接到zookeeper");
					countdownlatch.countDown();
				}
			} catch (Exception e) {
				logger.error("连接到zookeeper出错", e);
			} finally {
				countdownlatch.countDown();
			}
		};
	};

	public void keepalived() {
	}

	@PostConstruct
	public void init() {
		try {
			this.connection();
		} catch (Exception e) {
		}
	}

	private static CountDownLatch countdownlatch = new CountDownLatch(1);
	private String ipport = null;

	public ZooKeeper connection() throws Exception {
		zookeeperServers = ProfileUtil.getZookeeperServers();
		if (zookeeperServers != null && zookeeperServers.trim().length() > 0) {
			zk = new ZooKeeper(zookeeperServers, 2000, zkwatcher);
			countdownlatch.await();
			zk.addAuthInfo(ProfileUtil.getAuthinfoScheme(), ProfileUtil.getAuthinfoPassword().getBytes());
			List<ACL> list = new ArrayList<ACL>();
			ACL e = new ACL(Perms.ALL, new Id(ProfileUtil.getAuthinfoScheme(),
					DigestAuthenticationProvider.generateDigest(ProfileUtil.getAuthinfoPassword())));
			list.add(e);
			StringBuffer watcherPath = new StringBuffer();
			watcherPath.append(PATH);
			Stat stat = zk.exists(watcherPath.toString(), true);
			if (stat == null)
				zk.create(watcherPath.toString(), null, list, CreateMode.PERSISTENT);
		} else {
			logger.error("scheduled.zookeeper.servers没有配置,zookeeper任务调度没有地洞");
		}
		ipport = NativeUtil.ipport(zookeeperServers.split(",")[0]);
		this.notifyAll();
		return zk;
	}

	public void handler(String data, ApplicationContext applicationContext) {
	}

	public static void main(String[] args) {
		SchedulingRegistrar object = new SchedulingRegistrar();
		object.init();
		object.deploy("com.zjaisino.task.AutoCheckTask", "autoCheckTask", "autoChecked", "0/3 * * * * *");
		object.deploy("com.zjaisino.task.AutoCheckTask", "autoCheckTask", "checked", "0/3 * * * * *");
		object.deploy("com.zjaisino.task.AutoCheckTask", "autoCheckTask", "send", "0/3 * * * * *");
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}