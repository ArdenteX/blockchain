package com.taohongxu.blockchain.socket.peerTopeer.Listener;

import com.alibaba.fastjson.JSON;
import com.taohongxu.blockchain.Entity.DAO.public_private_keyDAO;
import com.taohongxu.blockchain.Entity.socketEntity.ChannelPacket;
import com.taohongxu.blockchain.Entity.DAO.blockchainDAO;
import com.taohongxu.blockchain.Entity.blockEntity.block;
import com.taohongxu.blockchain.Entity.socketEntity.keyAndEncode;
import com.taohongxu.blockchain.service.MongoDBService;
import com.taohongxu.blockchain.service.RSAService;
import com.taohongxu.blockchain.service.digitallySigned;
import com.taohongxu.blockchain.socket.peerTopeer.blockPacket;
import com.taohongxu.blockchain.socket.peerTopeer.event.nextBlockEvent;
import com.taohongxu.blockchain.socket.peerTopeer.packetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;


@Component
public class nextBlockListener {

    Logger logger = LoggerFactory.getLogger(nextBlockListener.class);

    @Value("${GroupName}")
    private String GroupName;
    @Value("${publicKey}")
    private String publicKey;
    @Value("${privateKey}")
    private String privateKey;

    @Autowired
    blockchainDAO blockchainDAO;
    @Autowired
    RSAService rsaService;
    @Autowired
    digitallySigned digitallySigned;
    @Autowired
    public_private_keyDAO key;
    @Autowired
    MongoDBService mongoDBService;

    @EventListener(nextBlockEvent.class)
    public void nextBlock(nextBlockEvent event){
        ChannelPacket channelPacket = event.getResource();
        ChannelContext channelContext = channelPacket.getChannelContext();
        String hash = channelPacket.getHash().replace("\"","");;

        block lastBlock = mongoDBService.findLast();

        String lastBlockHash = lastBlock.getHash();
        logger.info("lastBlock Hash = " + lastBlockHash);
        logger.info(" Hash = " + hash);

        if(lastBlockHash.equals(hash)){
            blockPacket packet = new blockPacket();
            packet.setType(packetType.NO_NEXT_BLOCK);
            packet.setBody(JSON.toJSONString(hash).getBytes());
            Tio.send(channelContext,packet);
        }
        else{
            try{
                String blockPublicKey = lastBlock.getBlockHead().getPublicKey();

                block block = next(lastBlock,hash);

                blockPacket packet = new blockPacket();
                packet.setType(packetType.REQUEST_NEXT_BLOCK);

                //加密模块，RSA加密和数字签名
                keyAndEncode SignEncode = new keyAndEncode();
                byte[] sign = digitallySigned.signedEncode(block,privateKey);
                SignEncode.setPublicKey(publicKey);
                SignEncode.setSignedEncode(sign);
                byte[] encode = rsaService.encode(JSON.toJSONString(SignEncode),blockPublicKey);

                packet.setBody(encode);

                Tio.send(channelContext,packet);
            }catch (NullPointerException e){
                logger.info("查无区块");
            }
            catch (Exception e){
                logger.info("出现其他错误");
            }
        }
    }

    public block next(block lastBlock,String hash){
        String preHash = lastBlock.getBlockHead().getPreHash();

        if(lastBlock.getHash().equals("0")){
            if(preHash.equals(hash)){
                return lastBlock;
            }
            logger.info("创世块，遍历结束");
            return null;
        }

        if(preHash.equals(hash)){
            return lastBlock;
        }
        else{
            next(blockchainDAO.findByHash(preHash),hash);
        }
        return null;
    }
}
