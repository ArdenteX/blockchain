package com.taohongxu.blockchain.Entity.blockEntity;


import lombok.Data;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class user_Hash extends RepresentationModel<user_Hash> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long tableId;
    private String blockName;
    private String Hash;

    public String getBlockName() {
        return blockName;
    }

    public void setHash(String hash) {
        Hash = hash;
    }

    public void setBlockName(String block_name) {
        this.blockName = block_name;
    }
}
