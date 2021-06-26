package com.taohongxu.blockchain.socket.peerTopeer.Listener;

import com.taohongxu.blockchain.Entity.DAO.blockchainDAO;
import com.taohongxu.blockchain.Entity.blockEntity.block;
import com.taohongxu.blockchain.service.MongoDBService;
import com.taohongxu.blockchain.socket.peerTopeer.event.addTrueBlockEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
public class addTrueBlockListener {
    Logger logger = LoggerFactory.getLogger(addTrueBlockListener.class);

    @Autowired
    blockchainDAO blockchainDAO;

    @Autowired
    MongoDBService mongoDBService;

    @EventListener(addTrueBlockEvent.class)
    public void listener(addTrueBlockEvent event){
        block block = event.getSource();
        block preBlock = blockchainDAO.findByHash(block.getBlockHead().getPreHash());
        block falseBlock = mongoDBService.findByPreHash(preBlock.getHash());

        blockchainDAO.delete(falseBlock);
        blockchainDAO.save(block);
        logger.info("区块链修正完成");
    }
}
