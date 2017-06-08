package com.htxx.scheduling.watcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
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
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;
import com.htxx.core.annotation.Registering;
import com.htxx.core.common.CommonUtil;
import com.htxx.core.common.MD5Util;
import com.htxx.core.logger.Logger;
import com.htxx.core.logger.LoggerFactory;
import com.htxx.core.pojo.Node;
import com.htxx.core.pojo.PathType;
import com.htxx.core.pojo.SchedulingDTO;
import com.htxx.core.util.NativeUtil;
import com.htxx.core.util.ProfileUtil;
import com.htxx.scheduling.kafka.KafkaClient;

@Component
public class ZookeeperSchedulingWatcher extends ScheduledTaskRegistrar {
	private String zookeeperServers;
	public static final String PATH = "/scheduling";
	private ZooKeeper zk = null;
	private String servicePath;
	@Resource
	private ApplicationContext applicationContext;
	private static Logger logger = LoggerFactory.getLogger(KafkaClient.class);
	Map<String, SchedulingDTO> schMap = null;

	public void reloadTask() {
		try {
			this.addCronTask(new ScheduledMethodRunnable(this, "keepalived"), "0 0 0 * * *");
		} catch (Exception e) {
		}
		super.scheduleTasks();
	}

	/**
	 * 注册任务到服务器端
	 * 
	 * @param beanid
	 *            spring里的beanId
	 * @param type
	 *            类型
	 * @param method
	 *            方法
	 * @param message
	 *            描述
	 */
	private void registering(String beanId, String type, String method, Object message) {
		try {
			StringBuffer taskNodePath = new StringBuffer();
			taskNodePath.append(PathType.TOPNODE.getPath());
			taskNodePath.append(PathType.REGISTERING.getPath());
			List<ACL> list = new ArrayList<ACL>();
			ACL e = new ACL(Perms.ALL, new Id(ProfileUtil.getAuthinfoScheme(),
					DigestAuthenticationProvider.generateDigest(ProfileUtil.getAuthinfoPassword())));
			list.add(e);
			Stat stat = zk.exists(taskNodePath.toString(), true);
			if (stat == null) {
				zk.create(taskNodePath.toString(), null, list, CreateMode.PERSISTENT);
			}
			taskNodePath.append("/");
			taskNodePath.append(type);
			Node node = new Node();
			node.setPlatform("");
			node.setBeanId(beanId);
			if (node.getBeanId() == null) {
				String[] info = node.getClazz().split("[.]");
				node.setBeanId(CommonUtil.firstToLower(info[info.length - 1]));
			}
			node.setClazz(type);
			node.setPlatform("");
			node.setAllowHost("*");
			node.setAllowHost("*");
			node.setType(Node.Type.CLASS.name());
			String messagestr = JSON.toJSONString(node);
			Stat deployStat = zk.exists(taskNodePath.toString(), true);
			if (deployStat == null) {
				zk.create(taskNodePath.toString(), messagestr.getBytes("utf-8"), list, CreateMode.PERSISTENT);
			} else
				zk.setData(taskNodePath.toString(), messagestr.getBytes("utf-8"), deployStat.getVersion());
			taskNodePath.append("/");
			taskNodePath.append(method);
			Stat methodStat = zk.exists(taskNodePath.toString(), true);
			node.setType(Node.Type.METHOD.name());
			node.setMethod(method);
			node.setStatus(Node.Status.DEFAULT.getKey());
			node.setPath(taskNodePath.toString());
			if (message != null)
				node.setMessage(message.toString());
			messagestr = JSON.toJSONString(node);
			if (methodStat == null) {
				zk.create(taskNodePath.toString(), messagestr.getBytes("utf-8"), list, CreateMode.PERSISTENT);
			} else
				zk.setData(taskNodePath.toString(), messagestr.getBytes("utf-8"), -1);
			taskNodePath.append("/clients");
			Stat cstat = zk.exists(taskNodePath.toString(), true);
			if (cstat == null) {
				zk.create(taskNodePath.toString(), null, list, CreateMode.PERSISTENT);
			}
			taskNodePath.append("/");
			// 设置客户端列表
			String result = zk.create(taskNodePath.toString(), null, list, CreateMode.EPHEMERAL_SEQUENTIAL);
			if (ipport != null && ipport.length() > 0) {
				taskNodePath.append(result);
				zk.setData(result, ipport.getBytes("utf-8"), -1);
			}
		} catch (Exception e) {
			logger.warn("执行注册失败", e);
		}
	}

	private void scanner() {
		for (String bean : applicationContext.getBeanDefinitionNames()) {
			try {
				Object object = applicationContext.getBean(bean);
				Method[] methods = object.getClass().getDeclaredMethods();
				for (Method method : methods) {
					if (method.getParameterCount() == 0) {
						Annotation[] annotations = method.getDeclaredAnnotations();
						for (Annotation annotation : annotations) {
							if (Registering.class.getTypeName().equals(annotation.annotationType().getTypeName())) {
								Method[] annmethods = annotation.annotationType().getDeclaredMethods();
								for (Method annmet : annmethods) {
									if (annmet.getName().equals("value"))
										this.registering(bean, object.getClass().getTypeName(), method.getName(),
												annmet.invoke(annotation));
								}
							}
						}
					}
				}
			} catch (Exception e) {
				logger.warn("扫描失败", e);
			}
		}
	}

	Watcher zkwatcher = new Watcher() {
		@Override
		public void process(WatchedEvent event) {
			try {
				logger.info(event.getType().name());
				if (event.getType() == Watcher.Event.EventType.NodeDataChanged)
					handler(event.getPath(), applicationContext);
				else if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged)
					handlerClass(event.getPath(), applicationContext);
				else if (event.getType() == Watcher.Event.EventType.NodeCreated)
					handlerClass(event.getPath(), applicationContext);
				else if (event.getType() == Watcher.Event.EventType.NodeDeleted)
					handlerClassDelete(event.getPath(), applicationContext);
				else if (event.getState() == Watcher.Event.KeeperState.Expired) {
					this.wait(3000);
					connection();
				} else if (event.getState() == Watcher.Event.KeeperState.Disconnected) {
					this.wait(3000);
					connection();
				} else if (event.getState() == Watcher.Event.KeeperState.AuthFailed) {
					logger.info("连接zookeeper认证失败");
					countdownlatch.countDown();
				} else if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
					logger.info("连接到zookeeper");
					countdownlatch.countDown();
					scannerDeployed();
					KafkaClient.createProducer(zk);
				}
			} catch (Exception e) {
				logger.info("连接到zookeeper出错", e);
			} finally {
				countdownlatch.countDown();
			}
		};
	};

	public void keepalived() {
	}

	@PostConstruct
	public void init() {
		this.reloadTask();
		try {
			this.connection();
		} catch (Exception e) {
			logger.warn("连接zoo失败", e);
		}
		this.scanner();
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
			servicePath = ProfileUtil.getServicePath();
			if (servicePath != null && servicePath.trim().length() > 0)
				watcherPath.append("/").append(servicePath);
			Stat stat = zk.exists(watcherPath.toString(), true);
			if (stat == null)
				zk.create(watcherPath.toString(), null, list, CreateMode.PERSISTENT);
		} else {
			logger.error("scheduled.zookeeper.servers没有配置,zookeeper任务调度没有地洞");
		}
		ipport = NativeUtil.ipport(zookeeperServers.split(",")[0]);
		try {
			this.notifyAll();
		} catch (Exception e) {
		}
		return zk;
	}

	/**
	 * 取消
	 * 
	 * @param clazz
	 * @param method
	 */
	public void cancel(String clazz, String method) {
		this.cancel(clazz, method, false);

	}

	public void cancel(String clazz, String method, boolean f) {
		try {
			logger.info("尝试取消任务" + clazz + ":" + method);
			for (Field field : this.getClass().getSuperclass().getDeclaredFields()) {
				field.setAccessible(true);
				if ("scheduledTasks".equals(field.getName())) {
					Object object = field.get(this);
					if (object instanceof Set) {
						@SuppressWarnings("unchecked")
						Set<ScheduledTask> set = (Set<ScheduledTask>) object;
						for (ScheduledTask task : set) {
							if (f)
								task.cancel();
							else {
								for (Field taskField : task.getClass().getDeclaredFields()) {
									taskField.setAccessible(true);
									if (taskField.getName().equals("future")) {
										Future<?> future = (Future<?>) taskField.get(task);
										future.cancel(false);
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void cancelAll(Boolean f) {
		try {
			super.setCronTasksList(null);
			for (Field field : this.getClass().getSuperclass().getDeclaredFields()) {
				field.setAccessible(true);
				if ("scheduledTasks".equals(field.getName())) {
					Object object = field.get(this);
					if (object instanceof Set) {
						@SuppressWarnings("unchecked")
						Set<ScheduledTask> set = (Set<ScheduledTask>) object;
						for (ScheduledTask task : set) {
							if (f)
								task.cancel();
							else {
								for (Field taskField : task.getClass().getDeclaredFields()) {
									taskField.setAccessible(true);
									if (taskField.getName().equals("future")) {
										Future<?> future = (Future<?>) taskField.get(task);
										future.cancel(false);
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * method节点变化
	 * 
	 * @param path
	 * @param applicationContext
	 */
	public void handler(String path, ApplicationContext applicationContext) {
		try {
			logger.info(path);
			if (path.startsWith(PATH + "/deploy")) {
				zk.getChildren(path, true);
				Boolean reload = false;
				SchedulingDTO old = null;
				if (schMap == null)
					schMap = new HashMap<String, SchedulingDTO>();
				SchedulingDTO vo = readMethodNode(zk.getData(path, true, null));
				String key = vo.getClazz() + ":" + vo.getMethod();
				if (schMap.containsKey(key)) {
					old = schMap.get(key);
				} else {
					reload = true;
				}
				schMap.put(key, vo);
				if (!reload
						&& !MD5Util.MD5Encode(JSON.toJSONString(vo)).equals(MD5Util.MD5Encode(JSON.toJSONString(old))))
					reload = true;
				if (reload)
					this.run();
			}
		} catch (Exception e) {
		}
	}

	private String checkCode = null;

	private SchedulingDTO readMethodNode(byte[] nodebyte) {
		if (nodebyte != null) {
			try {
				Node node = JSON.parseObject(new String(nodebyte), Node.class);
				if (node != null && node.getType().equalsIgnoreCase(Node.Type.METHOD.name())) {
					SchedulingDTO vo = new SchedulingDTO();
					String key = node.getClazz() + ":" + node.getMethod();
					vo.setClazz(node.getClazz());
					vo.setCron(node.getCron());
					vo.setBeanid(node.getBeanId());
					vo.setMethod(node.getMethod());
					vo.setPlatform(node.getPlatform());
					vo.setAllowHost(node.getAllowHost());
					vo.setStatus(node.getStatus());
					vo.setPath(key);
					return vo;
				}
			} catch (Exception e) {
			}
		}
		return null;
	}

	public void addTask(Object task, SchedulingDTO vo) throws Exception {
		if (task != null && vo.getCron() != null) {
			super.addCronTask(new ScheduledMethodRunnable(task, vo.getMethod()), vo.getCron());
			String path = PATH + "/deploy/" + vo.getPath() + "/clients/"
					+ NativeUtil.ipport(ProfileUtil.getZookeeperServers());
			if (zk.exists(path, false) != null)
				zk.create(path, null, null, CreateMode.EPHEMERAL);
		}
	}

	public void removeTask(SchedulingDTO vo) throws Exception {
		if (vo == null || vo.getClass() == null || vo.getMethod() == null)
			return;
		List<CronTask> cronlist = super.getCronTaskList();
		List<CronTask> newcronlist = new ArrayList<CronTask>();
		boolean reload = false;
		for (CronTask cron : cronlist) {
			Method[] methods = cron.getRunnable().getClass().getDeclaredMethods();
			for (Method method : methods) {
				try {
					if ("getMethod".equals(method.getName())) {
						String clazz = null;
						String taskMethod = null;
						Method m = (Method) method.invoke(cron.getRunnable());
						clazz = m.getDeclaringClass().getName();
						taskMethod = m.getName();
						if (clazz.equals(vo.getClazz()) && taskMethod.equals(vo.getMethod())) {
							reload = true;
							this.cancel(vo.getClazz(), vo.getMethod());
						} else {
							newcronlist.add(cron);
						}
					}
				} catch (Exception e) {
				}
			}
		}
		if (reload) {
			logger.info("重新加载任务");
			super.setCronTasksList(newcronlist);
		}
	}

	/**
	 * class节点变化
	 * 
	 * @param path
	 * @param applicationContext
	 */
	public void handlerClass(String path, ApplicationContext applicationContext) {
		try {
			logger.info(path);
			if (path.startsWith(PATH + "/deploy")) {
				zk.getChildren(path, true);
				Boolean reload = false;
				SchedulingDTO old = null;
				if (schMap == null)
					schMap = new HashMap<String, SchedulingDTO>();
				byte[] nodebyte = zk.getData(path, true, null);
				if (nodebyte != null) {
					List<String> list = zk.getChildren(path, true);
					for (String method : list) {
						SchedulingDTO vo = readMethodNode(zk.getData(path + "/" + method, true, null));
						String key = vo.getClazz() + ":" + vo.getMethod();
						if (schMap.containsKey(key)) {
							old = schMap.get(key);
						} else {
							reload = true;
							schMap.put(key, vo);
						}
						if (!reload && MD5Util.MD5Encode(JSON.toJSONString(vo))
								.equals(MD5Util.MD5Encode(JSON.toJSONString(old))))
							reload = true;
					}
				}
				if (reload)
					this.run();
			}
		} catch (Exception e) {
		}
	}

	/**
	 * class节点变化
	 * 
	 * @param path
	 * @param applicationContext
	 */
	public void handlerClassDelete(String path, ApplicationContext applicationContext) {
		try {
			logger.info(path);
			if (path.startsWith(PATH + "/deploy")) {
				// Boolean reload = false;
				SchedulingDTO old = null;
				if (schMap == null)
					schMap = new HashMap<String, SchedulingDTO>();
				path = path.replace(PATH + "/deploy", "");
				String[] info = path.split("/");
				String key = info[1] + ":" + info[2];
				if (schMap.containsKey(key)) {
					old = schMap.remove(key);
					if (old != null)
						this.cancel(old.getClazz(), old.getMethod());
				}
				// if (reload)
				// this.run();
			}
		} catch (Exception e) {
		}
	}

	public void run() {
		if (schMap != null && !schMap.isEmpty()) {
			String newCheckCode = MD5Util.MD5Encode(JSON.toJSONString(schMap));
			if (!newCheckCode.equals(checkCode)) {
				checkCode = newCheckCode;
				this.cancelAll(false);
				Iterator<Entry<String, SchedulingDTO>> it = schMap.entrySet().iterator();
				while (it.hasNext()) {
					try {
						Entry<String, SchedulingDTO> entry = it.next();
						SchedulingDTO vo = entry.getValue();
						if (vo.getBeanid() == null || vo.getBeanid().trim().length() == 0 || vo.getCron() == null) {
							continue;
						}
						Object object = applicationContext.getBean(vo.getBeanid());
						// String[]
						// beans=this.applicationContext.getBeanDefinitionNames();
						// for(String bean:beans){
						// object = applicationContext.getBean(bean);
						// }
						System.err.println(vo.getClazz() + ":" + vo.getMethod() + ":" + vo.getCron());
						if (object != null && vo.getCron() != null) {
							addTask(object, vo);
						}
					} catch (org.springframework.beans.factory.NoSuchBeanDefinitionException e) {
						logger.debug("忽略任务", e);
					} catch (Exception e) {
						logger.warn("开始任务出错", e);
					}
				}
				System.err.println(super.getCronTaskList());
				logger.info("开始任务调度");
				this.reloadTask();
			} else
				logger.info("任务调度规则没有改变");
		}
	}

	/**
	 * 扫描任务
	 */
	private void scannerDeployed() {
		try {
			schMap = new HashMap<String, SchedulingDTO>();
			List<String> clazzList = zk.getChildren(PATH + "/deploy", true);
			for (String clazz : clazzList) {
				String clapath = PATH + "/deploy/" + clazz;
				List<String> methodList = zk.getChildren(clapath, true);
				for (String method : methodList) {
					try {
						String methodPath = clapath + "/" + method;
						SchedulingDTO vo = this.readMethodNode(zk.getData(methodPath, true, null));
						if (vo != null) {
							String key = vo.getClazz() + ":" + vo.getMethod();
							schMap.put(key, vo);
						}
					} catch (Exception e) {
						logger.warn("扫描任务出错", e);
					}
				}
			}
		} catch (Exception e1) {
			logger.warn("扫描任务出错", e1);
		}
		this.run();
	}

}