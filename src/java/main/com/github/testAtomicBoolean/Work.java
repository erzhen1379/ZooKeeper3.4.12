package com.github.testAtomicBoolean;

import java.util.concurrent.atomic.AtomicBoolean;

public class Work {
    private AtomicBoolean flag;

    public Work() {
        this.flag = new AtomicBoolean(false);
        //通过构造函数加载时赋值（默认false）
    }

    /**
     * 启动线程
     */
    public void start() {
        //方法一
       if (!flag.get()) {//get()返回当前值
            synchronized (flag) {
                flag.set(true);//设定给定制
                System.out.println("Work------开始执行！");
            }
        } else {
            System.out.println("Work之前已经开始了！");
        }
        //方法二
   /*    System.out.println("flag修改前值------" + flag.get());
        if (flag.compareAndSet(false, true)) {
            System.out.println("Work------开始执行！");
            System.out.println("flag修改后值------" + flag.get());
        }*/
    }

    /**
     * 停止线程
     */
    public void stop() {
        //上面在synchronized中已经设置flag为true时候，
        if (flag.get()) {//get()返回当前值
            synchronized (flag) {
                System.out.println("Work------已经停止！");
                flag.set(false);//设定给定制
            }
        } else {
            System.out.println("Work之前已经停止了！");
        }
    }
}