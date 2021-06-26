package com.taohongxu.blockchain.Entity;


import lombok.Data;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collation = "public_private_key")
public class public_private_key {
    /**
     * @Author ArdentXu
     * 2021/3/22
     * 公私钥存储
     * */
    @Id
    private String publicKey;
    private String privateKey;
}
