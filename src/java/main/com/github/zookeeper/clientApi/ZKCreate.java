package com.github.zookeeper.clientApi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.ZooDefs.Ids;


import java.util.concurrent.CountDownLatch;

/**
 * 创建节点
 */
public class ZKCreate implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        ZooKeeper zookeeper = new ZooKeeper("172.16.3.200:2181", 5000, new ZKCreate());
        System.out.println(zookeeper.getState());

        zookeeper.create("/zk-test-ephemeral-", "".getBytes(),
                Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
                new IStringCallback(), "I am context.");
        try {
            connectedSemaphore.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("zookeeper session established");
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println("处理来自zk的wather通知的process");
        System.out.println("receive watched event " + event);
        if (Event.KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }


}
