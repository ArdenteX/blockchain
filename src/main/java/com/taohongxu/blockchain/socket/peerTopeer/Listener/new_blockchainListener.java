package com.taohongxu.blockchain.socket.peerTopeer.Listener;

import com.alibaba.fastjson.JSON;
import com.taohongxu.blockchain.Entity.DAO.blockchainDAO;
import com.taohongxu.blockchain.Entity.blockEntity.block;
import com.taohongxu.blockchain.Entity.socketEntity.ChannelPacket;
import com.taohongxu.blockchain.socket.peerTopeer.blockPacket;
import com.taohongxu.blockchain.socket.peerTopeer.event.new_BlockchainEvent;
import com.taohongxu.blockchain.socket.peerTopeer.packetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.tio.core.Tio;

import java.util.List;


@Component
public class new_blockchainListener {
    @Autowired
    blockchainDAO blockchainDAO;

    @EventListener(new_BlockchainEvent.class)
    public void listener(new_BlockchainEvent event){
        blockPacket blockPacket = new blockPacket();
        ChannelPacket packet = event.getSource();
        Sort sort = Sort.by(Sort.Direction.ASC,"blockHead.timeStamp");
        block block = blockchainDAO.findAll(sort).get(0);

        if(block == null){
            blockPacket.setType(packetType.NO_GEN_BLOCK);
            Tio.send(packet.getChannelContext(),blockPacket);
            return;
        }

        blockPacket.setType(packetType.SYNC_BLOCKCHAIN);
        blockPacket.setBody(JSON.toJSONString(block).getBytes());

        Tio.send(packet.getChannelContext(),blockPacket);
    }
}
