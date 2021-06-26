package com.taohongxu.blockchain.socket.peerTopeer.Listener;


import com.alibaba.fastjson.JSON;
import com.taohongxu.blockchain.Entity.blockEntity.block;
import com.taohongxu.blockchain.Entity.socketEntity.blocksCheck;
import com.taohongxu.blockchain.socket.peerTopeer.blockPacket;
import com.taohongxu.blockchain.socket.peerTopeer.event.blockCheckEvent;
import com.taohongxu.blockchain.socket.peerTopeer.packetType;
import com.taohongxu.blockchain.socket.peerTopeer.tools.SHAEncryption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.tio.core.Tio;

import java.util.List;

@Component
public class blockCheckListener {

    @Value("${GroupName}")
    String GroupName;

    Logger logger = LoggerFactory.getLogger(blockCheckListener.class);

    @EventListener(blockCheckEvent.class)
    public void checkListener(blockCheckEvent event){
        blocksCheck check = event.getSource();
        List<block> blocks = check.getBlocks();

        logger.info("开始验证本区块完整性");

        for(int i = 0; i < blocks.size()-1;i++){
            String preHash = blocks.get(i).getBlockHead().getPreHash();
            if(preHash.equals("0")){
                logger.info("验证完毕：区块完整");
                break;
            }

            String hash = SHAEncryption.SHAByHutool(blocks.get(i+1).getBlockHead().toString()+blocks.get(i+1).getBlockBody().toString());

            logger.info("preHash: "+preHash );
            logger.info("Hash: " + hash);


            if(!preHash.equals(hash)){
                logger.info("验证出错：定位出错位置");

                blockPacket packet = new blockPacket();
                packet.setType(packetType.GET_BLOCK);
                packet.setBody(JSON.toJSONString(preHash).getBytes());

                logger.info("正在群发信息：请求正确区块");
                Tio.sendToGroup(check.getClientTioConfig(),GroupName,packet);
            }

        }
        logger.info("验证结束");
    }
}
