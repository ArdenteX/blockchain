package com.taohongxu.blockchain.service;


import cn.hutool.crypto.asymmetric.RSA;
import com.alibaba.fastjson.JSON;
import com.taohongxu.blockchain.Entity.DAO.blockchainDAO;
import com.taohongxu.blockchain.Entity.DAO.public_private_keyDAO;
import com.taohongxu.blockchain.Entity.DAO.user_HashDAO;
import com.taohongxu.blockchain.Entity.*;
import com.taohongxu.blockchain.Entity.blockEntity.*;
import com.taohongxu.blockchain.socket.peerTopeer.Client.blockChainClientStarter;
import com.taohongxu.blockchain.socket.peerTopeer.tools.SHAEncryption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ArdentXu
 *
 * 区块链的操作模块
 *
 * */

@Service
public class BlockchainService {

   private blockchainCache blockchainCache;
   private user_HashDAO user_hashDAO;
   private MongoDBService mongoDBService;
   private public_private_keyDAO public_private_keyDAO;
   private blockChainClientStarter bcs;
   private blockchainDAO blockchainDAO;
   public BlockchainService(){}
   private Logger logger = LoggerFactory.getLogger(BlockchainService.class);

    @Autowired
   public BlockchainService(blockchainCache blockchainCache,user_HashDAO user_hashDAO,
                            MongoDBService mongoDBService,
                            public_private_keyDAO public_private_keyDAO,blockchainDAO blockchainDAO,blockChainClientStarter bcs){
       this.blockchainCache = blockchainCache;
       this.user_hashDAO = user_hashDAO;
       this.mongoDBService = mongoDBService;
       this.public_private_keyDAO = public_private_keyDAO;
       this.blockchainDAO = blockchainDAO;
       this.bcs = bcs;
   }

   //创世块
   public initPacket genesisBlock(List<student> students){
        RSA rsa = new RSA();
        //创建blockhead的hashList
       List<String> studentHash = new ArrayList<>();
       for(student student : students){
           studentHash.add(JSON.toJSONString(student));
       }


       block genesisBlock = new block();
       blockHead blockHead = new blockHead();
       blockBody blockBody = new blockBody();
       public_private_key ppk = new public_private_key();

       blockHead.setVersion("1.0");
       blockHead.setNumber(1);
       blockHead.setPreHash("0");
       blockHead.setHashList(studentHash);
       blockHead.setTimeStamp(System.currentTimeMillis());
       blockHead.setMerkleTreeRootHash(blockHead.getMerkleTreeRootHash());
       blockHead.setPublicKey(rsa.getPublicKeyBase64());

//       ppk.setPrivateKey(rsa.getPrivateKeyBase64());
//       ppk.setPublicKey(blockHead.getPublicKey());
//       public_private_keyDAO.save(ppk);

       blockBody.setStudents(students);
       logger.info("body = " + blockBody);
       logger.info("bodyJson = " + JSON.toJSON(blockBody));

       genesisBlock.setBlockBody(blockBody);
       genesisBlock.setBlockHead(blockHead);
       genesisBlock.setHash(SHAEncryption.SHAByHutool(blockHead.toString()+blockBody.toString()));

       initPacket init = new initPacket();
       init.setBlock(genesisBlock);
       init.setPrivateKey(rsa.getPrivateKeyBase64());


       return init;
   }

   /*添加块
   * 1.共识机制（p2p）
   * 2.生成新区块
   * 3.验证并加入区块链
   * 4.入键值对数据库*/
   public initPacket creatBlock(List<student> students){
       List<String> studentHash = new ArrayList<>();
       for(student student : students){
           studentHash.add(JSON.toJSONString(student));
       }
       RSA rsa = new RSA();
       block block = new block();
       blockHead blockHead = new blockHead();
       blockBody blockBody = new blockBody();
       public_private_key ppk = new public_private_key();

       //硬编码版本
       blockHead.setVersion("1.0");
       //生成新区块的公钥
       blockHead.setPublicKey(rsa.getPublicKeyBase64());
       //生成时间戳
       blockHead.setTimeStamp(System.currentTimeMillis());
       //上一个块的hash值
       blockHead.setPreHash(mongoDBService.findLast().getHash());
       //输入的内容的hash值
       blockHead.setHashList(studentHash);
       //merkleTree的根节点
       blockHead.setMerkleTreeRootHash(blockHead.getMerkleTreeRootHash());
//      有安全风险
//       ppk.setPrivateKey(rsa.getPrivateKeyBase64());
//       ppk.setPublicKey(blockHead.getPublicKey());
//       public_private_keyDAO.save(ppk);

       blockBody.setStudents(students);
       block.setBlockHead(blockHead);
       block.setBlockBody(blockBody);

       //hash值
       block.setHash(SHAEncryption.SHAByHutool(blockHead.toString()+blockBody.toString()));

       initPacket init = new initPacket();
       init.setBlock(block);
       init.setPrivateKey(rsa.getPrivateKeyBase64());
       return init;

   }

   public blockState blockType(){
       //创世块
       if(user_hashDAO.findFirstByTableId(1) == null){
           return blockState.GENESIS_BLOCK;
       }

       return blockState.NEW_BLOCK;
   }

   public boolean isLegal(block newBlock){
       if(!mongoDBService.findLast().getHash().equals(newBlock.getBlockHead().getPreHash())){
           return false;
       }
       return SHAEncryption.SHAByHutool(newBlock.getBlockHead().toString()+newBlock.getBlockBody().toString()).equals(newBlock.getHash());
   }

   /*
   * 添加区块：
   * 判断是创世块还是newBlock，用switch+enum来实现
   * 因为有传入区块名，区块名与hash绑定所以可以顺便吧其存入对象中
   * 在生成区块的方法中加入公私钥的绑定*/
   public initPacket addBlock(String name,List<student> students) throws Exception{

       blockState bs  = blockType();
       switch(bs){
           case GENESIS_BLOCK:
               initPacket packet = genesisBlock(students);
               block genesisBlock = packet.getBlock();
               user_Hash user_hash = new user_Hash();
               user_hash.setBlockName(name);
               user_hash.setHash(genesisBlock.getHash());

               blockchainCache.getBlocks().add(genesisBlock);
               user_hashDAO.save(user_hash);
               bcs.addBlock(name,genesisBlock);

               logger.info("创世块加入！" + blockchainCache.getBlocks().get(0).getHash());
               packet.setCreateStatue("true");
               return packet;
           case NEW_BLOCK:
               initPacket packet1 = creatBlock(students);
               block newBlock = packet1.getBlock();
               user_Hash user_hash1 = new user_Hash();
               user_hash1.setBlockName(name);
               user_hash1.setHash(newBlock.getHash());

               if(isLegal(newBlock)){
                   logger.info("有新的区块加入！");
                   blockchainCache.getBlocks().add(newBlock);

                   user_hashDAO.save(user_hash1);
                   bcs.addBlock(name,newBlock);
                   packet1.setCreateStatue("true");
                   return packet1;
               }
               else{
                   packet1.setCreateStatue("false");
                   return packet1;
               }

           case ERROR: break;

       }
       return null;
   }



}
