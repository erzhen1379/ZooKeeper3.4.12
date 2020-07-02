package com.github.demo.seri;


import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * 通过客户端，获取节点数据
 */
public class GetDataRequestTest implements Watcher {

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        String path="/zk-hosts-getDate1";
        ZooKeeper zookeeper = new ZooKeeper("172.16.3.200:2181", 5000, new GetDataRequestTest());
        //创建父节点
       zookeeper.create(path,"test1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //实际上是调用GetDataRequest请求
        String s = new String(zookeeper.getData(path, true, null));
        System.out.println(s);
        System.out.println("111");
    }

    @Override
    public void process(WatchedEvent event) {

    }
}
