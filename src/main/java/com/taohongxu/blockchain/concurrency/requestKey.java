package com.taohongxu.blockchain.concurrency;

import com.taohongxu.blockchain.Entity.DAO.blockchainDAO;
import com.taohongxu.blockchain.Entity.DAO.user_HashDAO;
import com.taohongxu.blockchain.Entity.mailDetail;
import com.taohongxu.blockchain.service.emailService;

public class requestKey implements Runnable {

    private final String toMail;
    private final String name;
    private final blockchainDAO mongoDBService;
    private final user_HashDAO user_hashDAO;
    private final emailService emailService;

    public requestKey(String toMail,String name,blockchainDAO mongoDBService,user_HashDAO user_hashDAO,emailService emailService){
        this.toMail = toMail;
        this.name = name;
        this.mongoDBService = mongoDBService;
        this.user_hashDAO = user_hashDAO;
        this.emailService = emailService;
    }

    @Override
    public void run() {
        mailDetail mailDetail = new mailDetail();
        mailDetail.setFrom("2574753055@qq.com");
        mailDetail.setTo(toMail);
        mailDetail.setSubject("申请区块："+name+"公钥回执");
        mailDetail.setContext("我们收到了您的申请信息，区块名为： "+ name+"公钥： "+mongoDBService.findByHash(user_hashDAO.findUser_HashByblockName(name).getHash()).getBlockHead().getPublicKey());
        emailService.sentMail(mailDetail);
    }
}
