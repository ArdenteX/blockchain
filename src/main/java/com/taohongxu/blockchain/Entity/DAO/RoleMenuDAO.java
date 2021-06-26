package com.taohongxu.blockchain.Entity.DAO;

import com.taohongxu.blockchain.security.menu.RoleMenu;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RoleMenuDAO extends MongoRepository<RoleMenu,Integer> {
    RoleMenu findByRole(String role);
}
