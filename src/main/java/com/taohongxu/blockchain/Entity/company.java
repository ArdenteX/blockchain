package com.taohongxu.blockchain.Entity;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class company{

    //企业名
    private String companyName;
    //企业编码
    @Id
    private String companyId;
    //企业邮箱
    private String companyEmail;
    //企业联系电话
    private String companyTel;
    //企业地址
    private String companyPlace;

}
