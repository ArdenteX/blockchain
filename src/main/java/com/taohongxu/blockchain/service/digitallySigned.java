package com.taohongxu.blockchain.service;

import com.alibaba.fastjson.JSON;
import com.taohongxu.blockchain.Entity.blockEntity.block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author ArdentXu
 * 2021/4/13
 * （节点）私钥加密公钥解密，嵌套在加密封装中，里面存放的是新的区块或要传递的区块*/
@Service
public class digitallySigned {
    @Autowired
    RSAService rsaService;
    public byte[] signedEncode(block block, String privateKey){
        return rsaService.encode(JSON.toJSONString(block),privateKey);
    }
}
