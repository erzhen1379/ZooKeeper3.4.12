package com.github.test.datatree;


import junit.framework.TestCase;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.server.DataTree;


import org.junit.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 测试
 */
public class TestDataTree extends TestCase {
    protected static final Logger LOG = LoggerFactory.getLogger(TestDataTree.class);



    /**
     * For ZOOKEEPER-1755 - Test race condition when taking dumpEphemerals and
     * removing the session related ephemerals from DataTree structure
     */
    @Test(timeout = 60000)
    public void testDumpEphemerals() throws Exception {
        int count = 100;
        long session = 1000;
        long zxid = 2000;
        final DataTree dataTree = new DataTree();
        LOG.info("Create {} zkclient sessions and its ephemeral nodes", count);
        //创建临时节点
        createEphemeralNode(session, dataTree, count);
        //final修饰的对象只能指向唯一一个对象，不可以再将它指向其他对象
        //并发原子类
        //类提供了可以原子读取和写入的底层布尔值的操作，并且还包含高级原子操作。 AtomicBoolean支持基础布尔变量上的原子操   　　作
        final AtomicBoolean exceptionDuringDumpEphemerals = new AtomicBoolean(
                false);
        System.out.println("exceptionDuringDumpEphemerals:" + exceptionDuringDumpEphemerals);
        final AtomicBoolean running = new AtomicBoolean(true);
        System.out.println("exceptionDuringDumpEphemerals:" + running);
        Thread thread = new Thread() {
            public void run() {
                PrintWriter pwriter = new PrintWriter(new StringWriter());
                try {
                    while (running.get()) {
                        //Write a text dump of all the ephemerals in the datatree
                        dataTree.dumpEphemerals(pwriter);
                    }
                } catch (Exception e) {
                    LOG.error("Received exception while dumpEphemerals!", e);
                    exceptionDuringDumpEphemerals.set(true);
                }
            }

            ;
        };
        thread.start();
        LOG.debug("Killing {} zkclient sessions and its ephemeral nodes", count);
        //关闭session
        killZkClientSession(session, zxid, dataTree, count);
        running.set(false);
        thread.join();
        Assert.assertFalse("Should have got exception while dumpEphemerals!",
                exceptionDuringDumpEphemerals.get());
    }

    private void killZkClientSession(long session, long zxid,
                                     final DataTree dataTree, int count) {
        for (int i = 0; i < count; i++) {
            dataTree.killSession(session + i, zxid);
        }
    }

    /**
     * 创建临时节点
     *
     * @param session
     * @param dataTree
     * @param count
     * @throws KeeperException.NoNodeException
     * @throws KeeperException.NodeExistsException
     */
    private void createEphemeralNode(long session, final DataTree dataTree,
                                     int count) throws KeeperException.NoNodeException, KeeperException.NodeExistsException {
        for (int i = 0; i < count; i++) {
            dataTree.createNode("/test" + i, new byte[0], null, session + i,
                    dataTree.getNode("/").stat.getCversion() + 1, 1, 1);
        }
    }


}
