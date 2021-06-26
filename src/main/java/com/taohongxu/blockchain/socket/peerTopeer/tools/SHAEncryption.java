package com.taohongxu.blockchain.socket.peerTopeer.tools;


import cn.hutool.crypto.digest.DigestUtil;

public class SHAEncryption {
    public static String SHAByHutool(String data){
        return DigestUtil.sha256Hex(data);
    }

}


