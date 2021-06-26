package com.taohongxu.blockchain.Entity.DAO;

import com.taohongxu.blockchain.Entity.blockEntity.block;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface blockchainDAO extends MongoRepository<block,String>{
    block findByHash(String Hash);
}
