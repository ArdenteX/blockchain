package com.taohongxu.blockchain.Entity.DAO;

import com.taohongxu.blockchain.Entity.public_private_key;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface public_private_keyDAO extends MongoRepository<public_private_key,String> {
    public_private_key findByPublicKey(String publicKey);
}
