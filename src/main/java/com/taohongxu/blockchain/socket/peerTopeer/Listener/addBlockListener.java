package com.taohongxu.blockchain.socket.peerTopeer.Listener;

import com.taohongxu.blockchain.Entity.DAO.blockchainDAO;
import com.taohongxu.blockchain.Entity.blockEntity.block;
import com.taohongxu.blockchain.service.MongoDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.taohongxu.blockchain.socket.peerTopeer.event.addBlockEvent;

@Component
public class addBlockListener{
    @Autowired
    blockchainDAO mongoDBService;

    @EventListener(value = addBlockEvent.class)
    public void addBlock(addBlockEvent addBlockEvent){
        block newBlock = (block) addBlockEvent.getSource();
        mongoDBService.save(newBlock);
    }

}
