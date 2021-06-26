package com.taohongxu.blockchain.Entity.Resource;

import com.taohongxu.blockchain.Entity.blockEntity.user_Hash;
import com.taohongxu.blockchain.Entity.student;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

public class userHashResource extends RepresentationModel<user_Hash> {
    @Getter
    private final String blockName;
    //姓名
    @Getter
    private final String Hash;

    public userHashResource(user_Hash s){
        this.blockName = s.getBlockName();
        this.Hash = s.getHash();

    }

}
