package com.taohongxu.blockchain.concurrency;

import com.taohongxu.blockchain.ApplicationContextProvider;
import com.taohongxu.blockchain.service.emailService;

import java.io.File;

public class mailCurrency implements Runnable {
    emailService emailService = ApplicationContextProvider.getBean(com.taohongxu.blockchain.service.emailService.class);
    File file;


    public mailCurrency(File file){
        this.file = file;
    }

    @Override
    public void run() {
        emailService.sendMailWithAttachment(file);
    }
}
