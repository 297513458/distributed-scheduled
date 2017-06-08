package com.htxx.scheduling.common;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.ClusterStatus;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

public class HbaseClient {

	public static void main(String[] args) throws Exception {
		/*
		 * >>>zookeeper--->>>ROOT-(单Region)--->>>.mema.--->>>表 -ROOT-
		 * 表包含.META.表所在的region列表，该表只会有一个Region； Zookeeper中记录了-ROOT-表的location。
		 * .META. 表包含所有的用户空间region列表，以及RegionServer的服务器地址。 必须配置hosts绑定才能连上
		 */
		Configuration config = HBaseConfiguration.create();
		config.set("hbase.zookeeper.property.clientPort", "2181");
		config.set("hbase.zookeeper.quorum", "10.0.0.122,10.0.0.120,10.0.0.121");
		Connection connection = ConnectionFactory.createConnection(config);
		try {
			HBaseAdmin admin = (HBaseAdmin) connection.getAdmin();
			admin.tableExists("bestdatabase");
			ClusterStatus cs = admin.getClusterStatus();
			System.err.println(cs.getServers());
			Table table = connection.getTable(TableName.valueOf("bestdatabase"));
			table.getTableDescriptor();
			try {
				Get g = new Get(Bytes.toBytes("1"));
				Result r = table.get(g);
				byte[] value = r.getValue(Bytes.toBytes("message"), Bytes.toBytes("url"));
				String valueStr = Bytes.toString(value);
				System.out.println("GET: " + valueStr);
				for (int t = 0; t < 1000000; t++) {
					List<Put> putlist=new ArrayList<Put>();
					for (int p = 0; p < 1000; p++) {
						Put put = new Put(Bytes.toBytes(UUID.randomUUID().toString()));
						put.addImmutable(Bytes.toBytes("message"), Bytes.toBytes("url"), put.getRow());
						putlist.add(put);
					}
					table.put(putlist);
				}
				Scan s = new Scan();
				s.addColumn(Bytes.toBytes("message"), Bytes.toBytes("url"));
				ResultScanner scanner = table.getScanner(s);
				try {
					for (Result rr = scanner.next(); rr != null; rr = scanner.next()) {
						System.out.println(
								"Found row: " + rr + rr.getValue(Bytes.toBytes("message"), Bytes.toBytes("url")));
					}
				} finally {
					scanner.close();
				}
			} finally {
				if (table != null)
					table.close();
			}
		} finally {
			connection.close();
		}
	}
}