package com.taohongxu.blockchain.socket.peerTopeer.Listener;

import com.alibaba.fastjson.JSON;
import com.taohongxu.blockchain.Entity.DAO.blockchainDAO;
import com.taohongxu.blockchain.Entity.blockEntity.block;
import com.taohongxu.blockchain.Entity.socketEntity.ChannelPacket;
import com.taohongxu.blockchain.service.MongoDBService;
import com.taohongxu.blockchain.socket.peerTopeer.blockPacket;
import com.taohongxu.blockchain.socket.peerTopeer.event.trueBlockEvent;
import com.taohongxu.blockchain.socket.peerTopeer.packetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;


@Component
public class trueBlockListener {
    @Autowired
    blockchainDAO dao;

    @EventListener(trueBlockEvent.class)
    public void listener(trueBlockEvent event){
        ChannelPacket packet = event.getSource();
        ChannelContext context = packet.getChannelContext();
        String preHash = packet.getHash();

        block nextBlock = dao.findByHash(preHash);
        block block = dao.findByHash(nextBlock.getBlockHead().getPreHash());

        if(nextBlock.getBlockHead().getPreHash().equals(block.getHash())){
            blockPacket blockPacket = new blockPacket();
            blockPacket.setType(packetType.TRUE_BLOCK);
            blockPacket.setBody(JSON.toJSONString(block).getBytes());

            Tio.send(context,blockPacket);
        }
        //如果不相同就不往外传，因为心跳包会自己检测到本区块的错误
        else{
            return;
        }

    }
}
