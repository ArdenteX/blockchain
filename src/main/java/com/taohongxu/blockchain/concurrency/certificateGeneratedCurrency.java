package com.taohongxu.blockchain.concurrency;

import com.taohongxu.blockchain.ApplicationContextProvider;
import com.taohongxu.blockchain.Entity.student;
import com.taohongxu.blockchain.service.CertificateGeneratedService;
import com.taohongxu.blockchain.service.emailService;
import com.taohongxu.blockchain.service.excelService;
import lombok.SneakyThrows;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class certificateGeneratedCurrency{
    List<student> students;
    String blockName;
    String orgName;
    excelService service1 = ApplicationContextProvider.getBean(excelService.class);
    CertificateGeneratedService service = ApplicationContextProvider.getBean(CertificateGeneratedService.class);
    com.taohongxu.blockchain.service.emailService emailService = ApplicationContextProvider.getBean(com.taohongxu.blockchain.service.emailService.class);

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    public certificateGeneratedCurrency(List<student> students,String blockName,String orgName){
        this.students = students;
        this.blockName = blockName;
        this.orgName = orgName;
    }

    Thread thread1 = new Thread(new Runnable() {
        @SneakyThrows
        @Override
        public void run() {
            for (student student : students) {
                service.generated(student);
            }
        }
    });

    Thread thread2 = new Thread(new Runnable() {
        @Override
        public void run() {
            service1.toExcel(students,blockName);
        }
    });

    Thread thread3 = new Thread(new Runnable() {
        @Override
        public void run() {
            emailService.sendMailWithAttachment(new File("/root/blockchainData/HashExcel/"+orgName+"/"+blockName+".xlsx"));
        }
    });

    public void run() {
        executorService.submit(thread1);

        executorService.submit(thread2);

        executorService.submit(thread3);
    }




}
