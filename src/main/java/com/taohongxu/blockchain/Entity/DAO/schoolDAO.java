package com.taohongxu.blockchain.Entity.DAO;

import com.taohongxu.blockchain.Entity.school;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface schoolDAO extends JpaRepository<school,String> {
    List<school> findSchoolBySchoolLevel(String schoolLevel);
    school findBySchoolName(String schoolName);
}
