package com.taohongxu.blockchain.security;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data

/**
 * @parm id自增
 * @parm name 角色名
 * @parm nameZH 中文角色名*/
public class Role {
   @Id
   private int id;
   private String name;
   private String nameZH;
}
