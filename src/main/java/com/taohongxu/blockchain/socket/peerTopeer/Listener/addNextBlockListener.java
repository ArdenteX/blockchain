package com.taohongxu.blockchain.socket.peerTopeer.Listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taohongxu.blockchain.Entity.DAO.blockchainDAO;
import com.taohongxu.blockchain.Entity.blockEntity.block;
import com.taohongxu.blockchain.Entity.socketEntity.keyAndEncode;
import com.taohongxu.blockchain.service.MongoDBService;
import com.taohongxu.blockchain.service.RSAService;
import com.taohongxu.blockchain.socket.peerTopeer.blockPacket;
import com.taohongxu.blockchain.socket.peerTopeer.event.addNextBlockEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;


/**
 * @Author ArdentXu
 * 2021/4/13
 * 这个listener负责对传递的信息进行解码然后直接加入区块链*/
@Component
public class addNextBlockListener {
    Logger logger = LoggerFactory.getLogger(addTrueBlockListener.class);

    @Autowired
    MongoDBService dao;

    @Autowired
    blockchainDAO blockchainDAO;

    @Autowired
    RSAService rsaService;

    @EventListener(addNextBlockEvent.class)
    public void addNextBlock(addNextBlockEvent event) throws UnsupportedEncodingException {
        String publicKey = dao.findLast().getBlockHead().getPublicKey();

        byte[] body = event.getResource();

        byte[] decode = rsaService.decode(body,publicKey);
        String str = new String(decode, blockPacket.CHARSET);
        JSONObject jsonObject = JSON.parseObject(str);
        keyAndEncode keyAndEncode = JSON.toJavaObject(jsonObject,keyAndEncode.class);

        String NodePublicKey = keyAndEncode.getPublicKey();
        byte[] sign = rsaService.decode(keyAndEncode.getSignedEncode(),NodePublicKey);
        String signStr = new String(sign,blockPacket.CHARSET);
        JSONObject jsonObject1 = JSON.parseObject(signStr);
        block nextBlock = JSON.toJavaObject(jsonObject1,block.class);

        if(blockchainDAO.findByHash(nextBlock.getHash()) == null){
            blockchainDAO.save(nextBlock);
        }
        else{
            logger.info("传入区块已在本节点区块链中存在");
            return;
        }


    }
}
