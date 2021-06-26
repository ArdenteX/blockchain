package com.taohongxu.blockchain.Entity.DAO;

import com.taohongxu.blockchain.security.blockChainUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface blockchainUserDAO extends MongoRepository<blockChainUser,String> {
    blockChainUser findByUsername(String username);
}
