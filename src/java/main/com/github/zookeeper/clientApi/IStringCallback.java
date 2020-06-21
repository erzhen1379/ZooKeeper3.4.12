package com.github.zookeeper.clientApi;

import org.apache.zookeeper.AsyncCallback;

/**
 * 接口回调方法
 */
class IStringCallback implements AsyncCallback.StringCallback {
    public void processResult(int rc, String path, Object ctx, String name) {
        System.out.println("Create path result: [" + rc + ", " + path + ", "
                + ctx + ", real path name: " + name);
    }
}