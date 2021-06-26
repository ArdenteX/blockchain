package com.taohongxu.blockchain.Entity;


import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class school{
    private static final long serialVersionUID = -6012932336242789657L;
    @Id
    //高校编号
    private String schoolId;
    //高校名字
    private String schoolName;
    //高校批次
    private String schoolLevel;
    //高校地址
    private String schoolPlace;
    //高校邮箱
    private String email;
    //高校联系电话
    private String tel;
    //校长
    private String chartMan;

}
