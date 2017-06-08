package com.htxx.scheduling.kafka;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.context.ApplicationListener;
import com.htxx.core.logger.Logger;
import com.htxx.core.logger.LoggerDTO;
import com.htxx.core.logger.LoggerFactory;
import com.htxx.core.logger.LoggerLevel;
import com.htxx.scheduling.logger.LoggerEvent;
import com.htxx.scheduling.watcher.ZookeeperSchedulingWatcher;

public class KafkaClient {
	private static Lock lock = new ReentrantLock();
	private static Producer<String, String> producer;
	private static String topic = "scheduledlog";
	private static ZooKeeper zookeeper = null;
	private static Logger logger = LoggerFactory.getLogger(KafkaClient.class);
	private static LoggerLevel level = null;

	private static void send(LoggerDTO dto) {
		if (dto != null) {
			if (producer == null)
				createProducer(zookeeper);
			if (producer == null) {
				logger.warn("kafka日志没有启动:" + dto.getMessage());
			}
			try {
				if (topic == null)
					topic = "schedulinglog";
				if (level == null)
					level = LoggerLevel.INFO;
				if (level.ordinal() <= dto.getType().ordinal()) {
					String th = "";
					StringWriter writer = null;
					if (dto.getThrowable() != null) {
						writer = new StringWriter();
						dto.getThrowable().printStackTrace(new PrintWriter(writer));
						th = writer.toString();
					}
					logger.info("发送日志到kafka:topic:" + topic + " 级别:" + dto.getType().name() + " 描述:" + dto.getMessage()
							+ "\n" + th);
					producer.send(
							new ProducerRecord<String, String>(topic, dto.getType().name() + dto.getMessage(), th));
				}
				// producer.flush();
			} catch (Exception e) {
				logger.warn("发送日志到kafka失败:topic:" + topic + " 级别:" + dto.getType().name() + " 描述:" + dto.getMessage(),
						dto.getThrowable());
			}
		}
	}

	public static Producer<String, String> createProducer(ZooKeeper zk) {
		if (zk != null)
			zookeeper = zk;
		if (producer == null) {
			try {
				lock.lock();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("key.serializer", StringSerializer.class.getName());
				map.put("value.serializer", StringSerializer.class.getName());
				StringBuffer levelpath = new StringBuffer(ZookeeperSchedulingWatcher.PATH).append("/kafka")
						.append("/level");
				if (zk.exists(levelpath.toString(), true) != null) {
					byte[] levelbyte = zk.getData(levelpath.toString(), true, null);
					if (levelbyte != null) {
						try {
							level = Enum.valueOf(LoggerLevel.class, new String(levelbyte).toUpperCase());
						} catch (Exception e) {
						}
					}
				}
				map.put("client.id", String.valueOf(zk.hashCode()));
				StringBuffer topicpath = new StringBuffer(ZookeeperSchedulingWatcher.PATH).append("/kafka")
						.append("/topic");
				map.put("acks", "0");
				if (zk.exists(topicpath.toString(), true) != null) {
					byte[] topicby = zk.getData(topicpath.toString(), true, null);
					if (topicby != null) {
						topic = new String(topicby);
					}
				}
				StringBuffer serverspath = new StringBuffer(ZookeeperSchedulingWatcher.PATH).append("/kafka")
						.append("/servers");
				if (zk.exists(serverspath.toString(), true) != null) {
					byte[] kabyte = zk.getData(serverspath.toString(), true, null);
					if (kabyte != null) {
						map.put("bootstrap.servers", new String(kabyte));
						producer = new KafkaProducer<String, String>(map);
						logger.warn("kafka日志启动");
					} else {
						logger.warn("kafka日志发送失败");
					}
				}
			} catch (Exception e) {
				logger.warn("kafka日志发送失败", e);
			} finally {
				lock.unlock();
			}
		}
		return producer;
	}

	public static ApplicationListener<LoggerEvent> APPLICATIONLISTENER = new ApplicationListener<LoggerEvent>() {
		@Override
		public void onApplicationEvent(LoggerEvent event) {
			if (event.getSource() instanceof LoggerDTO) {
				try {
					send((LoggerDTO) event.getSource());
				} catch (Exception e) {
				}
			}
		}
	};
}