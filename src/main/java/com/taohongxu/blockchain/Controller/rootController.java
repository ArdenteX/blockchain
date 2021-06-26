package com.taohongxu.blockchain.Controller;

import com.taohongxu.blockchain.Config.securityConfig;
import com.taohongxu.blockchain.Entity.Assenble.userHashAssembleSupport;
import com.taohongxu.blockchain.Entity.DAO.blockchainUserDAO;
import com.taohongxu.blockchain.Entity.DAO.user_HashDAO;
import com.taohongxu.blockchain.Entity.JsonChange;
import com.taohongxu.blockchain.Entity.Resource.userHashResource;
import com.taohongxu.blockchain.Entity.blockEntity.block;
import com.taohongxu.blockchain.Entity.blockEntity.user_Hash;
import com.taohongxu.blockchain.Entity.mailDetail;
import com.taohongxu.blockchain.security.UserForm;
import com.taohongxu.blockchain.security.blockChainUser;
import com.taohongxu.blockchain.service.MongoDBService;
import com.taohongxu.blockchain.service.emailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

//
//@CrossOrigin
@RepositoryRestController
public class rootController{
    @Autowired
    blockchainUserDAO blockchainUserDAO;

    @Autowired
    securityConfig securityConfig;

    @Autowired
    user_HashDAO user_HashDAO;

    @PostMapping("blockChainUsers/register")
    @ResponseBody
    public String Register(@RequestBody JsonChange jc){
        String id = jc.getItem();
        String role = jc.getItem2();
        UserForm User = new UserForm();

        User.setUsername(id);
        User.setPassword("123456");
        User.setRole(role);

        blockChainUser blockChainUser = User.toUser(securityConfig.passwordEncoder());
        blockchainUserDAO.save(blockChainUser);
        return "success";
    }

    @GetMapping("blockChainUsers/recent")
    @ResponseBody
    public List<String> recent(){
        List<user_Hash> blocks = user_HashDAO.findAll();
        List<String> blockName = new ArrayList<>();
        for(int i = blocks.size()-1;i >= blocks.size()-4; i--){
            blockName.add(blocks.get(i).getBlockName());
        }
        return blockName;
    }

    @GetMapping("blockChainUsers/allBlock/{page}/{size}")
    @ResponseBody
    public Page<user_Hash> findAll(@PathVariable("page") Integer page, @PathVariable("size") Integer size){
        Pageable pageable = PageRequest.of(page-1,size);
        return user_HashDAO.findAll(pageable);
    }

    @PostMapping("blockChainUsers/findOne")
    @ResponseBody
    public user_Hash findOne(@RequestBody JsonChange jc){
        String blockName = jc.getItem();
        try{
            return user_HashDAO.findUser_HashByblockName(blockName);
        }catch (Exception e){
            return null;
        }

    }

}
