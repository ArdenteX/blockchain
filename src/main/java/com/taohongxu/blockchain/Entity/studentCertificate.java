package com.taohongxu.blockchain.Entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.File;

/**
 * 证书*/
@Document(collation = "Certificate")
@Data
public class studentCertificate {
    @Id
    private String stuNum;
    private String stuName;
    private String stuSchool;
    private File certificate;
}
