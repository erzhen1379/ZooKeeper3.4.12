package com.github.testAtomicBoolean;

import java.util.concurrent.TimeUnit;

public class mainWork {
    public static void main(final String[] arg) throws InterruptedException {
        Work work = new Work();
        work.start();
        TimeUnit.SECONDS.sleep(1);  //暂停一秒
        work.stop();
    }
}