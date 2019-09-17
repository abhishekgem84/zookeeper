package com.abhishek.zk;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;

public class ZooProperty {
	private static ZooKeeper zk = null;
	private final static String path = "/propfiles";
	// /propfiles/application_sys1.properties
	private final static CountDownLatch connectedSignal = new CountDownLatch(1);
	public static void main(String[] args) {
		try {
			zk = new ZooKeeper("localhost:2181,localhost:2182,localhost:2183", 1000, new Watcher() {

				public void process(WatchedEvent we) {
					System.out.println("Type :" + we.getType() + ", String :" + we.getPath());
					if (we.getState() == KeeperState.SyncConnected) {
						connectedSignal.countDown();
					}
					if (we.getType() == EventType.NodeDataChanged) {
						try {
							System.out.println(new String(zk.getData(path, true, null)));
						} catch (Exception ex) {
							System.out.println(ex.getMessage());
						}
					}
				}
			});
			
			connectedSignal.await();

			if (zk.exists(path + "/application_sys1.properties", true) == null) {
				zk.create(path, "child.nodes.created=true".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
						CreateMode.PERSISTENT);
				System.out.println("Znode created");
				List<String> lines = Files.readAllLines(Paths.get("d:/", "application_sys1.properties"),
						StandardCharsets.UTF_8);
				StringBuffer sb = new StringBuffer();
				for (String temp : lines) {
					// System.out.println(temp);
					sb.append(temp);
					sb.append(System.getProperty("line.separator"));
				}
				zk.create(path + "/application_sys1.properties", sb.toString().getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
						CreateMode.PERSISTENT);
				System.out.println("Child Znode created");
			} else {
				String getData = new String(zk.getData(path + "/application_sys1.properties", true, null));
				System.out.println(getData);
				Properties properties = new Properties();
				properties.load(new StringReader(getData));
				// Enumeration ennn=properties.elements();
				// while(ennn.hasMoreElements()){
				// System.out.println(ennn.nextElement());
				// }
				System.out.println(properties.get("com.sdl.umang.inapp.notification.alarm"));
			}

			zk.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
