package com.abhishek.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher;

public class zkTest {
	private static ZooKeeper zk=null;
	public static void main(String[] args) {
		try {
			 zk = new ZooKeeper("localhost", 1000, new Watcher() {

				public void process(WatchedEvent we) {
					System.out.println("Type :" + we.getType() + ", String :" + we.getPath());
					if (we.getState() == KeeperState.SyncConnected) {
						// connectionLatch.countDown();
					}
					if (we.getType() == EventType.NodeDataChanged) {
						try {
							System.out.println(new String(zk.getData("/FirstZooConfig", true, null)));
						} catch (Exception ex) {
							System.out.println(ex.getMessage());
						}
					}
				}
			});
			if (zk.exists("/FirstZooConfig", true) == null) {
				zk.create("/FirstZooConfig", "com.test.validate=true".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
						CreateMode.PERSISTENT);
				System.out.println("Znode created");
			}else{
				zk.setData("/FirstZooConfig", "updated from java code".getBytes(), zk.exists("/FirstZooConfig", true).getVersion());
			}
			System.out.println("connection done");
			Thread.sleep(100000L);
			zk.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
