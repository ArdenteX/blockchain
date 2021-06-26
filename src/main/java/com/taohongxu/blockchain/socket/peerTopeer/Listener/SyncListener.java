package com.taohongxu.blockchain.socket.peerTopeer.Listener;

import com.taohongxu.blockchain.Entity.DAO.blockchainDAO;
import com.taohongxu.blockchain.Entity.blockEntity.block;
import com.taohongxu.blockchain.socket.peerTopeer.event.SyncEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SyncListener {
    Logger logger = LoggerFactory.getLogger(SyncListener.class);

    @Autowired
    blockchainDAO blockchainDAO;

    @EventListener(SyncEvent.class)
    public void listener(SyncEvent event){
        block genBlock = event.getSource();

        if(blockchainDAO.findByHash(genBlock.getHash()) == null){
            logger.info("已获取创世块，同步开始");
            blockchainDAO.save(genBlock);
        }
        else {
            logger.info("已存在创世块");
            return;
        }
    }
}
