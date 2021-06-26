package com.taohongxu.blockchain.concurrency;

import com.taohongxu.blockchain.ApplicationContextProvider;
import com.taohongxu.blockchain.Entity.student;
import com.taohongxu.blockchain.service.excelService;

import java.util.List;

public class excelCurrency implements Runnable {
    excelService excelService = ApplicationContextProvider.getBean(com.taohongxu.blockchain.service.excelService.class);

    List<student> students;
    String blockName;

    public excelCurrency(List<student> students,String blockName){
        this.blockName = blockName;
        this.students = students;
    }

    @Override
    public void run() {
        excelService.toExcel(students,blockName);
    }
}
