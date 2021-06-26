package com.taohongxu.blockchain.Entity.Assenble;

import com.taohongxu.blockchain.Controller.schoolController;
import com.taohongxu.blockchain.Entity.Resource.userHashResource;
import com.taohongxu.blockchain.Entity.blockEntity.user_Hash;
import com.taohongxu.blockchain.Entity.student;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class userHashAssembleSupport extends RepresentationModelAssemblerSupport<user_Hash, userHashResource> {
    public userHashAssembleSupport(){
        super(schoolController.class, userHashResource.class);
    }

    @Override
    protected userHashResource instantiateModel(user_Hash uh){
        return new userHashResource(uh);
    }

    @Override
    public userHashResource toModel(user_Hash entity) {
        return createModelWithId(entity.getHash(),entity);
    }
}
