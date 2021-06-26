package com.taohongxu.blockchain.service;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSON;
import com.taohongxu.blockchain.Entity.student;
import com.taohongxu.blockchain.socket.peerTopeer.tools.SHAEncryption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class excelService {
    @Value("${name}")
    String orgName;
    public boolean toExcel(List<student> students,String blockName){
        List<String> hashes = new ArrayList<>();
        try{
            for(student student : students){
                File file = new File("/root/blockchainData/DigitalCertificate/",orgName+"/"+student.getStu_num()+".png");
                String fileHash = SHAEncryption.SHAByHutool(JSON.toJSONString(file));
                hashes.add(fileHash);
            }

            if(hashes.size() != 0){
                File file = new File("/root/blockchainData/HashExcel/"+orgName+"/"+blockName+".xlsx");
                if(!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
                OutputStream outputStream = new FileOutputStream(file);
                ExcelWriter writer = new ExcelWriter(outputStream, ExcelTypeEnum.XLSX);

                Sheet sheet = new Sheet(1,0);
                sheet.setSheetName("sheet1");

                List<List<String>> lists = new ArrayList<>();

                Table table = new Table(1);
                lists.add(Arrays.asList("学号"));
                lists.add(Arrays.asList("姓名"));
                lists.add(Arrays.asList("Hash"));
                table.setHead(lists);

                List<List<String>> hashList = new ArrayList<>();
                for(int i = 0; i < students.size();i++){
                    hashList.add(Arrays.asList(students.get(i).getStu_num(),students.get(i).getStu_name(),hashes.get(i)));
                }

                writer.write0(hashList,sheet,table);
                writer.finish();
            }



        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

}
