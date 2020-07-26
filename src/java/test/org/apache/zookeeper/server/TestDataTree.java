package org.apache.zookeeper.server;


import junit.framework.TestCase;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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

    private DataTree dt;


    @Before
    public void setUp() throws Exception {
        dt = new DataTree();
    }

    @After
    public void tearDown() throws Exception {
        dt = null;
    }

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
        //当调用了 Thread.Join()方法后,当前线程会立即被执行,其他所有的线程会被暂停执行.,当这个线程执行完后,其他线程才会继续执行.
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

    /**
     * 测试
     */
    @Test(timeout = 60000)
    public void testRootWatchTriggered() throws KeeperException.NoNodeException, KeeperException.NodeExistsException {
        System.out.println("测试根基节点触发watch");
        class MyWatcher implements Watcher {
            boolean fired = false;

            @Override
            public void process(WatchedEvent event) {
                if (event.getPath().equals("/")) {
                    //触发回调函数
                    fired = true;
                }
            }
        }
        //创建一个内部类
        MyWatcher myWatcher = new MyWatcher();
        //在根节点设置一个watcher
        dt.getChildren("/", new Stat(), myWatcher);

        dt.createNode("/xyz", new byte[0], null, 0, dt.getNode("/").stat.getCversion() + 1, 1, 1);
        Assert.assertFalse("Root node watch not triggered", !myWatcher.fired);


    }

}
