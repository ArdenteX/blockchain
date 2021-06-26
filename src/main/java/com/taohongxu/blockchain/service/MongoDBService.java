package com.taohongxu.blockchain.service;

import com.taohongxu.blockchain.Entity.DAO.blockchainDAO;
import com.taohongxu.blockchain.Entity.blockEntity.block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public class MongoDBService {
    private static final Logger logger = LoggerFactory.getLogger(MongoDBService.class);

    MongoTemplate mongoTemplate;
    blockchainDAO blockchainDAO;
    @Autowired
    public MongoDBService(MongoTemplate mongoTemplate,blockchainDAO blockchainDAO){
        this.mongoTemplate = mongoTemplate;
        this.blockchainDAO = blockchainDAO;
        logger.info("mongoTemplate = " + mongoTemplate);
    }
    public MongoDBService(){}


    public block findLast(){
        Sort sort = Sort.by(Sort.Direction.DESC,"blockHead.timeStamp");
        List<block> blocks = blockchainDAO.findAll(sort);
        try{
            return blocks.get(0);
        }catch (Exception e){
            return null;
        }

    }

    public List<block> findAllDESC(){
        Sort sort = Sort.by(Sort.Direction.DESC,"blockHead.timeStamp");
        return blockchainDAO.findAll(sort);
    }

    //待验证
    public block findByPreHash(String preHash){
        Query query = new Query(Criteria.where("blockHead.preHash").is(preHash));
        return mongoTemplate.findOne(query,block.class);
    }

    public Long count(Query query, Class<block> bClass, String collectionName){
        return mongoTemplate.count(query,bClass,collectionName);
    }




}
