package com.taohongxu.blockchain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taohongxu.blockchain.Entity.DAO.RoleMenuDAO;
import com.taohongxu.blockchain.Entity.DAO.blockchainDAO;
import com.taohongxu.blockchain.Entity.DAO.public_private_keyDAO;
import com.taohongxu.blockchain.Entity.DAO.user_HashDAO;
import com.taohongxu.blockchain.Entity.blockEntity.block;
import com.taohongxu.blockchain.Entity.blockEntity.user_Hash;
import com.taohongxu.blockchain.concurrency.mailCurrency;
import com.taohongxu.blockchain.security.menu.Menu;
import com.taohongxu.blockchain.security.menu.RoleMenu;
import com.taohongxu.blockchain.security.menu.children;
import com.taohongxu.blockchain.service.MongoDBService;
import com.taohongxu.blockchain.service.emailService;
import com.taohongxu.blockchain.socket.peerTopeer.tools.SHAEncryption;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest
class BlockchainApplicationTests {
    @Autowired
    public_private_keyDAO kkpd;

    @Autowired
    WebApplicationContext applicationContext;

    @Autowired
    emailService emailService;

    @Autowired
    MongoDBService mongoDBService;

    @Autowired
    RoleMenuDAO menuDAO;

    @Autowired
    blockchainDAO blockchainDAO;

    @Autowired
    user_HashDAO  user_hashDAO;

    MockMvc mockMvc;

    Logger logger = LoggerFactory.getLogger(BlockchainApplicationTests.class);
    @Before
    public void before(){

    }

    @Test
    void contextLoads(){
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new mailCurrency(new File("D:\\HashExcel\\"+"111"+".xlsx")));

    }

    @Test
    void test1() throws Exception{

    }

}
