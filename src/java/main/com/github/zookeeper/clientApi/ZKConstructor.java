package com.github.zookeeper.clientApi;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

public class ZKConstructor implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        ZooKeeper zookeeper = new ZooKeeper("172.16.3.200:2181", 5000, new ZKConstructor());
        System.out.println(zookeeper.getState());
        long sessionId = zookeeper.getSessionId();
        byte[] sessionPasswd = zookeeper.getSessionPasswd();
        //通过复用sessionId和session passwd
        zookeeper = new ZooKeeper("172.16.3.200:2181", 5000, new ZKConstructor(), sessionId, sessionPasswd);
        Thread.sleep(Integer.MAX_VALUE);

        try {
            connectedSemaphore.await();
        } catch (InterruptedException e) {
        }

        System.out.println("zookeeper session established");

    }

    /**
     * 处理来自zk服务的watcher通知
     *
     * @param event
     */
    @Override
    public void process(WatchedEvent event) {
        System.out.println("处理来自zk的wather通知的process");
        System.out.println("receive watched event " + event);
        if (KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }
}
