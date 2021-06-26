package com.taohongxu.blockchain.Entity.DAO;

import com.taohongxu.blockchain.Entity.blockEntity.user_Hash;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface user_HashDAO extends JpaRepository<user_Hash,String> {
    user_Hash findUser_HashByblockName(String block_name);
    user_Hash findFirstByTableId(long id);
    Page<user_Hash> findAllByBlockNameContains(Pageable pageable, String blockName);
}
