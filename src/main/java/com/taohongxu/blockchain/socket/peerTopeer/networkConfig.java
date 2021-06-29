package com.taohongxu.blockchain.socket.peerTopeer;

import org.springframework.beans.factory.annotation.Value;

/**
 * 基于t-io的区块链底层的网络平台的常量
 * */


public interface networkConfig {

    /**
     * 服务器地址
     * 部署之后这里可以更改为部署后的地址输入为 0.0.0.0*/
    public static final String server = "0.0.0.0";

    /**
     * 监听端口*/
    public static final int port = 6179;

    /**
     * 心跳超时时间*/
    public static final int TIMEOUT = 50000;
}
