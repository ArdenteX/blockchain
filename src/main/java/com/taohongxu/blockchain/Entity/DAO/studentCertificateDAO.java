package com.taohongxu.blockchain.Entity.DAO;

import com.taohongxu.blockchain.Entity.studentCertificate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface studentCertificateDAO extends MongoRepository<studentCertificate,String> {
    studentCertificate findByStuNameAndStuNumAndStuSchool(String name,String school,String num);
}
