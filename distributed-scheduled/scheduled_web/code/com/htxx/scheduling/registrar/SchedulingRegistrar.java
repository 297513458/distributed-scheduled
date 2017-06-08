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
import com.htxx.core.pojo.PathType;
import com.htxx.core.util.NativeUtil;
import com.htxx.core.util.ProfileUtil;

@Component
public class SchedulingRegistrar {
	private String zookeeperServers;
	private ZooKeeper zk = null;
	@Resource
	private ApplicationContext applicationContext;
	private static Logger logger = LoggerFactory.getLogger(SchedulingRegistrar.class);

	/**
	 * 发布任务到服务器端
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
	public Boolean deploy(String type, String beanid, String method, String cron) {
		try {
			StringBuffer taskNodePath = new StringBuffer();
			taskNodePath.append(PathType.TOPNODE.getPath());
			taskNodePath.append(PathType.DEPLOY.getPath());
			List<ACL> list = new ArrayList<ACL>();
			ACL e = new ACL(Perms.ALL, new Id(ProfileUtil.getAuthinfoScheme(),
					DigestAuthenticationProvider.generateDigest(ProfileUtil.getAuthinfoPassword())));
			list.add(e);
			Stat stat = zk.exists(taskNodePath.toString(), true);
			if (stat == null) {
				zk.create(taskNodePath.toString(), null, list, CreateMode.PERSISTENT);
			}
			Node dto = new Node();
			dto.setBeanId(beanid);
			dto.setClazz(type);
			dto.setPlatform("");
			taskNodePath.append("/");
			taskNodePath.append(type);
			stat = zk.exists(taskNodePath.toString(), true);
			if (stat == null) {
				this.save(taskNodePath.toString(), JSON.toJSONString(dto));
			}
			taskNodePath.append("/");
			taskNodePath.append(method);
			dto.setCron(cron);
			dto.setAllowHost("*");
			dto.setType(Node.Type.METHOD.name());
			dto.setMethod(method);
			dto.setStatus(Node.Status.READY.getKey());
			dto.setPath(taskNodePath.toString());
			return this.save(dto.getPath(), JSON.toJSONString(dto));
		} catch (Exception e) {
			logger.error("发布任务出错", e);
		}
		return false;
	}

	public Boolean updateStatus(String platform, String clazz, String method, String status) {
		try {
			String path = this.fixPath(clazz, method);
			String json = this.read(path, true);
			if (json == null)
				return false;
			Node node = JSON.parseObject(json, Node.class);
			node.setStatus(Node.Status.READY.getKey());
			node.setPath(path);
			return this.save(node.getPath(), JSON.toJSONString(node));
		} catch (Exception e) {
			logger.error("修改任务出错", e);
		}
		return false;
	}

	public Boolean updateStatus(String clazz, String method, String status) {
		return this.updateStatus(null, clazz, method, status);
	}

	public Boolean delete(String clazz, String method) {
		try {
			String path = this.fixPath(clazz, method);
			List<ACL> list = new ArrayList<ACL>();
			ACL e = new ACL(Perms.ALL, new Id(ProfileUtil.getAuthinfoScheme(),
					DigestAuthenticationProvider.generateDigest(ProfileUtil.getAuthinfoPassword())));
			list.add(e);
			try {
				zk.setACL(path, list, -1);
			} catch (Exception e1) {
			}
			Stat stat = zk.exists(path, true);
			if (stat == null) {
				return true;
			}
			List<String> tlist = zk.getChildren(path, false);
			for (String k : tlist) {
				zk.delete(path + "/" + k, -1);
			}
			zk.delete(path, -1);
			return true;
		} catch (Exception e) {
			logger.error("删除任务节点出错", e);
		}
		return false;
	}

	Watcher zkwatcher = new Watcher() {
		@Override
		public void process(WatchedEvent event) {
			try {
				if ((event.getType() == Watcher.Event.EventType.NodeDataChanged)
						|| (event.getType() == Watcher.Event.EventType.NodeChildrenChanged)
						|| (event.getType() == Watcher.Event.EventType.NodeCreated)
						|| (event.getType() == Watcher.Event.EventType.NodeDeleted))
					handler(zk.getData(event.getPath(), zkwatcher, null), applicationContext);
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
			watcherPath.append(PathType.TOPNODE.getPath());
			Stat stat = zk.exists(watcherPath.toString(), true);
			if (stat == null)
				zk.create(watcherPath.toString(), null, list, CreateMode.PERSISTENT);
		} else {
			logger.error("scheduled.zookeeper.servers没有配置,zookeeper任务调度没有地洞");
		}
		ipport = NativeUtil.ipport(zookeeperServers);
		this.notifyAll();
		return zk;
	}

	/**
	 * 发布任务到服务器端
	 * 
	 * @param message
	 *            描述
	 */
	public Boolean save(String key, String value) {
		return this.save(key, value, true);
	}

	/**
	 * 修改数据
	 * 
	 * @param key
	 *            路径
	 * @param value
	 *            值
	 * @param acl
	 *            是否有权限控制
	 * @return
	 */
	public Boolean save(String key, String value, Boolean acl) {
		try {
			if (key == null || key.trim().length() == 0)
				return false;
			key = key.trim();
			byte[] data = null;
			if (value != null)
				data = value.trim().getBytes("utf-8");
			List<ACL> list = null;
			if (acl) {
				list = new ArrayList<ACL>();
				ACL e = new ACL(Perms.ALL, new Id(ProfileUtil.getAuthinfoScheme(),
						DigestAuthenticationProvider.generateDigest(ProfileUtil.getAuthinfoPassword())));
				list.add(e);
			}
			Stat stat = zk.exists(key.toString(), true);
			if (stat == null) {
				zk.create(key, data, list, CreateMode.PERSISTENT);
			} else {
				zk.setData(key, data, -1);
			}
			return true;
		} catch (Exception e) {
			logger.error("修改节点数据出错", e);
		}
		return false;
	}

	public void handler(byte[] data, ApplicationContext applicationContext) {
	}

	public List<Node> listDeployTask(String path) {
		return this.listTask(PathType.DEPLOY.getKey(), path);
	}

	/**
	 * 获取注册的任务列表
	 * 
	 * @param path
	 * @return
	 */
	public List<Node> listRegisteringTask(String path) {
		return this.listTask(PathType.REGISTERING.getKey(), path);
	}

	/**
	 * 获取注册的任务列表
	 * 
	 * @param path
	 * @return
	 */
	public List<Node> listTask(String type, String path) {
		StringBuffer taskNodePath = new StringBuffer("/");
		taskNodePath.append(type);
		taskNodePath.append(path);
		List<String> clazzList = this.list(taskNodePath.toString(), true);
		if (clazzList == null || clazzList.isEmpty())
			return null;
		List<Node> taskList = new ArrayList<Node>();
		for (String clazz : clazzList) {
			try {
				StringBuffer clazzpath = new StringBuffer(taskNodePath);
				clazzpath.append("/");
				clazzpath.append(clazz.trim());
				List<String> list = this.list(clazzpath.toString(), true);
				if (list != null) {
					for (String method : list) {
						Node dto = this.getScheduling(type, clazz, method);
						if (dto != null)
							taskList.add(dto);
					}
				}
			} catch (Exception e) {
				logger.error("修改节点数据出错", e);
			}
		}
		return taskList;
	}

	public Node getScheduling(String type, String platform, String clazz, String method) {
		try {
			return JSON.parseObject(this.read(this.fixPath(type, platform, clazz, method, null), true), Node.class);
		} catch (Exception e) {
		}
		return null;
	}

	public Node getDeployScheduling(String clazz, String method) {
		return this.getScheduling(PathType.DEPLOY.getKey(), clazz, method);
	}

	public Node getScheduling(String type, String clazz, String method) {
		return this.getScheduling(type, null, clazz, method);
	}

	public List<String> list(String path, Boolean acl) {
		try {
			StringBuffer sb = new StringBuffer(PathType.TOPNODE.getPath());
			if (path != null && path.trim().length() > 0)
				sb.append(path.trim());
			if (sb.toString().endsWith("/"))
				sb.deleteCharAt(sb.length() - 1);
			List<ACL> list = null;
			if (acl) {
				list = new ArrayList<ACL>();
				ACL e = new ACL(Perms.ALL, new Id(ProfileUtil.getAuthinfoScheme(),
						DigestAuthenticationProvider.generateDigest(ProfileUtil.getAuthinfoPassword())));
				list.add(e);
			}
			Stat stat = zk.exists(sb.toString(), true);
			if (stat != null) {
				List<String> chlist = zk.getChildren(sb.toString(), acl);
				return chlist;
			}
			return null;
		} catch (Exception e) {
			logger.error("修改节点数据出错", e);
		}
		return null;
	}

	public static void main(String[] args) {
		SchedulingRegistrar schedulingRegistrar = new SchedulingRegistrar();
		schedulingRegistrar.init();
		schedulingRegistrar.deploy("com.zjaisino.task.AutoCheckTask", "autoCheckTask", "autoChecked", "0/3 * * * * *");
	}

	public String fixPath(String platform, String clazz, String method, String nodepath) {
		return this.fixPath(PathType.DEPLOY.getKey(), platform, clazz, method, nodepath);
	}

	public String fixPath(String clazz, String method, String nodepath) {
		return this.fixPath(PathType.DEPLOY.getKey(), null, clazz, method, nodepath);
	}

	public String fixPath(String clazz, String method) {
		return this.fixPath(PathType.DEPLOY.getKey(), null, clazz, method, null);
	}

	public String fixPath(String type, String platform, String clazz, String method, String nodepath) {
		StringBuffer taskNodePath = new StringBuffer();
		taskNodePath.append(PathType.TOPNODE.getPath());
		taskNodePath.append("/");
		taskNodePath.append(type);
		if (platform != null && platform.trim().length() > 0) {
			taskNodePath.append("/");
			taskNodePath.append(platform.trim());
		}
		if (clazz != null && clazz.trim().length() > 0) {
			taskNodePath.append("/");
			taskNodePath.append(clazz.trim());
		}
		if (method != null && method.trim().length() > 0) {
			taskNodePath.append("/");
			taskNodePath.append(method.trim());
		}
		if (nodepath != null && nodepath.trim().length() > 0) {
			taskNodePath.append("/");
			taskNodePath.append(nodepath.trim());
		}
		return taskNodePath.toString();
	}

	public String read(String key, Boolean acl) {
		try {
			if (key == null || key.trim().length() == 0)
				return null;
			key = key.trim();
			List<ACL> list = null;
			if (acl) {
				list = new ArrayList<ACL>();
				ACL e = new ACL(Perms.ALL, new Id(ProfileUtil.getAuthinfoScheme(),
						DigestAuthenticationProvider.generateDigest(ProfileUtil.getAuthinfoPassword())));
				list.add(e);
			}
			Stat stat = zk.exists(key.toString(), true);
			if (stat != null) {
				byte[] data = zk.getData(key, true, null);
				if (data != null)
					return new String(data, "utf-8");
			}
		} catch (Exception e) {
			logger.error("修改节点数据出错", e);
		}
		return null;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}