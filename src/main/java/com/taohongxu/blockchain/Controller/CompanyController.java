package com.taohongxu.blockchain.Controller;

import com.alibaba.fastjson.JSON;
import com.taohongxu.blockchain.Entity.DAO.blockchainDAO;
import com.taohongxu.blockchain.Entity.DAO.blockchainUserDAO;
import com.taohongxu.blockchain.Entity.DAO.public_private_keyDAO;
import com.taohongxu.blockchain.Entity.DAO.user_HashDAO;
import com.taohongxu.blockchain.Entity.*;
import com.taohongxu.blockchain.Entity.blockEntity.block;
import com.taohongxu.blockchain.Entity.blockEntity.user_Hash;
import com.taohongxu.blockchain.concurrency.requestKey;
import com.taohongxu.blockchain.security.blockChainUser;
import com.taohongxu.blockchain.service.BlockchainService;
import com.taohongxu.blockchain.service.RSAService;
import com.taohongxu.blockchain.service.emailService;
import com.taohongxu.blockchain.socket.peerTopeer.tools.SHAEncryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author ArdentXu
 * 2021/3/24
 * 企业的controller
 * 接受授权的企业会得到对应区块的公钥
 * 采用私钥加密公钥解密的方式*/

//@CrossOrigin
@RepositoryRestController
public class CompanyController {
    private final emailService emailService;
    private final blockchainDAO mongoDBService;
    private RSAService rsaService;
    private public_private_keyDAO public_private_keyDAO;
    private final user_HashDAO user_hashDAO;
    private final blockchainUserDAO companyUserDAO;

    @Autowired
    public CompanyController(blockchainDAO mongoDBService,RSAService rsaService
            ,user_HashDAO user_hashDAO,blockchainUserDAO companyUserDAO,emailService emailService,public_private_keyDAO public_private_keyDAO){

        this.rsaService = rsaService;
        this.mongoDBService = mongoDBService;
        this.user_hashDAO = user_hashDAO;
        this.companyUserDAO = companyUserDAO;
        this.emailService = emailService;
        this.public_private_keyDAO = public_private_keyDAO;
    }

    @Value("${name}")
    String orgName;

    //设置全局变量
    @PostMapping("companies/findWhat")
    @ResponseBody
    public String findWhat(@RequestBody JsonChange jc){
        List<user_Hash> uh = user_hashDAO.findAll();
        for(user_Hash user : uh){
            if(user.getBlockName().equals(jc.getItem())){
                return "ok";
            }
        }
        return "error";
    }

    //验证
    @PostMapping("companies/findWhat/check/{name}")
    @ResponseBody
    public String check(@RequestBody JsonChange item,@PathVariable("name") String name){
        String publicKey = item.getItem();
        //获取区块
        block block = mongoDBService.findByHash(user_hashDAO.findUser_HashByblockName(name).getHash());
        if(block == null){
            return "notFoundBlock";
        }

        //获取公钥
        String publicKey1 = block.getBlockHead().getPublicKey();
        if(publicKey.equals(publicKey1)){
            return "ok";
        }
        else{
            return "error";
        }
    }

    //查找某一个人
    @PostMapping(value = "companies/findWhat/check/findOne/{name}")
    @ResponseBody
    public student findOne(@RequestBody JsonChange jc,@PathVariable("name") String name){
        String stuNum = jc.getItem();
        block block = mongoDBService.findByHash(user_hashDAO.findUser_HashByblockName(name).getHash());
        List<student> students = block.getBlockBody().getStudents();
        for(student s : students){
            if(s.getStu_num().equals(stuNum)){
                return s;
            }
        }
        return null;
    }

    //验证证书
    @PostMapping("companies/findWhat/check/CheckCertificate/{stu_num}/{name}")
    @ResponseBody
    public String checkCertificate(@RequestBody JsonChange jc,@PathVariable("stu_num") String stu_num,@PathVariable("name") String name){   //这里部署之前需要改一下，name为区块名
        String cHash = jc.getItem();
        if(cHash == null){
            return "error";
        }
        String path = "/Users/xuhongtao/涛仔/blockchainData/DigitalCertificate/"+orgName+"/"+stu_num+".png";
        File orgFile = new File(path);

        if(!orgFile.exists()){
            return "notFound";
        }
        String fileHash = SHAEncryption.SHAByHutool(JSON.toJSONString(orgFile));

        if(fileHash.equals(cHash)){
            return "success";
        }
        return "Invalid";
    }

    @PatchMapping("companies/changePasswd")
    @ResponseBody
    public String change(@RequestBody blockChainUser companyUser){


        try{
            blockChainUser user = companyUserDAO.findByUsername(companyUser.getUsername());
            if(companyUser.getPassword().equals("")){
                user.setPassword(companyUser.getPassword());
            }
            if(companyUser.getEmail().equals("")){
                user.setEmail(companyUser.getEmail());
            }
            if(companyUser.getOrganizationName().equals("")){
                user.setOrganizationName(companyUser.getOrganizationName());
            }
            if(companyUser.getPrincipal().equals("")){
                user.setPrincipal(companyUser.getPrincipal());
            }
            companyUserDAO.save(user);
        }catch (NullPointerException e){
            return "nullError";
        }

        return "修改成功！";
    }

    //请求公钥
    @GetMapping("companies/requestPublicKey/{name}/{toMail}")
    @ResponseBody
    public String rp(@PathVariable("toMail") String toMail,@PathVariable("name") String name){

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new requestKey(toMail,name,mongoDBService,user_hashDAO,emailService));

        return "success";
    }

    @GetMapping("companies/BlockByOrgName/{page}/{size}")
    @ResponseBody
    public Page<user_Hash> findAll(@PathVariable("page") Integer page, @PathVariable("size") Integer size){
        Pageable pageable = PageRequest.of(page-1,size);
        return user_hashDAO.findAllByBlockNameContains(pageable,orgName);
    }

}
