package com.taohongxu.blockchain.Controller;


import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSON;
import com.taohongxu.blockchain.Config.securityConfig;
import com.taohongxu.blockchain.Entity.DAO.blockchainDAO;
import com.taohongxu.blockchain.Entity.DAO.blockchainUserDAO;
import com.taohongxu.blockchain.Entity.DAO.user_HashDAO;
import com.taohongxu.blockchain.Entity.JsonChange;
import com.taohongxu.blockchain.Entity.blockEntity.block;
import com.taohongxu.blockchain.Entity.blockEntity.initPacket;
import com.taohongxu.blockchain.Entity.blockEntity.user_Hash;
import com.taohongxu.blockchain.Entity.student;
import com.taohongxu.blockchain.security.UserForm;
import com.taohongxu.blockchain.security.blockChainUser;
import com.taohongxu.blockchain.service.*;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;


/**
 *school模块的具体操作
 * 录入
 * 查询本校学生信息*/


//@CrossOrigin
@RepositoryRestController
public class schoolController {
    @Autowired
    private BlockchainService blockchainService;

    @Autowired
    private blockchainDAO blockchainDAO;

    @Autowired
    private MongoDBService mongoDBService;

    @Autowired
    private user_HashDAO user_hashDAO;

    @Autowired
    private RSAService rsaService;

    @Autowired
    private blockchainUserDAO schoolUserDAO;

    @Autowired
    private CertificateGeneratedService certificateGeneratedService;

    @Autowired
    private excelService service;

    @Autowired
    emailService emailService;

    @Autowired
    securityConfig securityConfig;

    @Value("${name}")
    String orgName;

    String blockName;

    Logger logger = LoggerFactory.getLogger(schoolController.class);


    List<student> students = new CopyOnWriteArrayList<>();
    int ImageCount = 0;
    /**
     * @Author Ardent
     * 录入
     * 2021/3/21
     * @param student 传入的学生类
     * */

    //保存
    @PostMapping("schools/save")
    @ResponseBody
    public String save(@RequestBody student student){
        if(student == null){
            return "未成功提交表单！";
        }

        students.add(student);
        logger.info("students = " + students);
        return "Success";
    }

    //传入图片
    @PostMapping("schools/save/photo")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile upImage){
        logger.info("upImage = " + upImage);

        if(upImage == null){
            return "没有传入照片";
        }
        try{

            logger.info("images.get(0) = " +upImage.getContentType());
            if(upImage.getContentType() == null){
                return "上传失败！";
            }

            String suffix = upImage.getContentType().toLowerCase();
            suffix = suffix.substring(suffix.lastIndexOf("/")+1);

            logger.info("suffix = " + suffix);

            if(suffix.equals("jpg") || suffix.equals("png") || suffix.equals("jpeg") || suffix.equals("gif")){

                String fileName = UUID.randomUUID().toString().replaceAll("-","")+ "." + suffix;
                String filePath = "/Users/xuhongtao/涛仔/blockchainData/photo";
                File file = new File(filePath,fileName);

                if(!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
                upImage.transferTo(file);
                try{
                    students.get(ImageCount).setImagePath(filePath+"/"+fileName);
                    ImageCount++;
                    return "保存照片成功!";
                }catch (Exception e){
                    return "请先保存信息后上传照片！";
                }



            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return "Success！";
    }

    //上链
    @PostMapping("schools/save/upLink")
    @ResponseBody
    public String upLink(@RequestBody JsonChange item) throws Exception {
        boolean isReady = false;
        //name现在为毕业时间
        String name = item.getItem();

        blockName = orgName+"-" +name;

        List<user_Hash> user_hashes = user_hashDAO.findAll();
        for(user_Hash hash : user_hashes){
            if (hash.getBlockName().equals(blockName)){
                return "blockNameRepeat";
            }
        }

        logger.info("blockName = " + blockName);
        logger.info("students = " + students);

        if(ImageCount == 0){
            return "photosNull";
        }

        if(students.size() == 0){
            return "studentsNull";
        }

        if(ImageCount != students.size()){
            return "UnequalLength";
        }



        //生成电子认证证书
        initPacket packet = blockchainService.addBlock(blockName,students);

        if(packet.getCreateStatue().equals("true")){
            try{

                CountDownLatch c1 = new CountDownLatch(1);
                CountDownLatch c2 = new CountDownLatch(1);

                Thread thread1 = new Thread(new Runnable() {
                    @Override
                    public void run(){
                        try{
                            logger.info("进入生成证书阶段");

                            int countCer = 0;
                            for (student student : students) {
                                countCer += certificateGeneratedService.generated(student);
                            }
                            if(countCer == students.size()){
                                c1.countDown();
                            }

                            logger.info("count = " + countCer);
                            logger.info("studentsSize = "+students.size());

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                });
                Thread thread2 = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        logger.info("进入生成电子哈希值阶段");
                        try{
                            c1.await();
                            if(service.toExcel(students,blockName)){
                                c2.countDown();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                });

                Thread thread3 = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try{
                            logger.info("邮件发送");
                            c2.await();
                            if(emailService.sendMailWithAttachment(new File("/Users/xuhongtao/涛仔/blockchainData/HashExcel/"+orgName+"/"+blockName+".xlsx"),packet.getPrivateKey())){
                                logger.info("上链完成");
                                students.clear();
                                ImageCount = 0;

                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                });

                thread1.start();

                thread2.start();

                thread3.start();

                isReady = true;

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(isReady){
            return "Success！";
        }

        else return "添加失败";

    }



    //多级分页查找
    @GetMapping("schools/findAll/{bn}/{page}/{size}")
    @ResponseBody
    public PageImpl<student> findAllStudentByBlockName(@PathVariable("page")Integer page
            ,@PathVariable("size")Integer size,@PathVariable("bn") String blockName ){
            Query query = new Query();
            Pageable pageable = PageRequest.of(page-1,size);
            query.with(pageable);
            Long count = mongoDBService.count(query, block.class,"blockchain");
            List<student> PageStudents = blockchainDAO.findByHash(user_hashDAO.findUser_HashByblockName(blockName).getHash()).getBlockBody().getStudents();
            return (PageImpl<student>) PageableExecutionUtils.getPage(PageStudents,pageable,()->count);
        }

    //全局变量api，前端post一个要查找的blockName之后传入成为全局变量
    @PostMapping("schools/findWhat")
    @ResponseBody
    public String findWhat(@RequestBody JsonChange jc){
        //验证该区块是否存在此区块链
        List<user_Hash> uh = user_hashDAO.findAll();
        for(user_Hash user : uh){
            if(user.getBlockName().equals(jc.getItem())){
                return "ok";
            }
        }
        return "error";
    }

    //查找之前需要验证密钥，先取出全局变量blockName的block之后进行封装后根据前端post回来的privateKey解码，成功即可操作！
    @PostMapping("schools/findWhat/check/{blockName}")
    @ResponseBody
    public String check(@RequestBody JsonChange item,@PathVariable("blockName") String blockName){
        String privateKey = item.getItem();
        block block = blockchainDAO.findByHash(user_hashDAO.findUser_HashByblockName(blockName).getHash());
        String ciphertext = JSON.toJSONString(block);
        byte[] encrypt = rsaService.encode(ciphertext,block.getBlockHead().getPublicKey());

        try{
            rsaService.decode(encrypt,privateKey);
            return "success";
        }catch (Exception e){
            return "error";
        }

    }

    @PutMapping("schools/changePasswd")
    @ResponseBody
    public String change(@RequestBody blockChainUser schoolUser){
        schoolUserDAO.save(schoolUser);
        return "修改成功！";
    }

    @GetMapping("schools/BlockByOrgName/{page}/{size}")
    @ResponseBody
    public Page<user_Hash> findAll(@PathVariable("page") Integer page, @PathVariable("size") Integer size){
        Pageable pageable = PageRequest.of(page-1,size);
        return user_hashDAO.findAllByBlockNameContains(pageable,orgName);
    }

    @PostMapping("schools/register")
    @ResponseBody
    public String Register(@RequestBody JsonChange jc){
        String id = jc.getItem();
        UserForm User = new UserForm();

        User.setUsername(id);
        User.setPassword("123456");
        User.setRole("ROLE_COMPANY");

        blockChainUser blockChainUser = User.toUser(securityConfig.passwordEncoder());
        schoolUserDAO.save(blockChainUser);
        return "success";
    }


}
