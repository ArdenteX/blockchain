package com.taohongxu.blockchain.Entity.DAO;

import com.taohongxu.blockchain.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface roleDAO extends JpaRepository<Role,Integer> {
}
