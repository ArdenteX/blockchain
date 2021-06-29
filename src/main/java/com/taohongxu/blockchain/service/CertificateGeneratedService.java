package com.taohongxu.blockchain.service;

import com.alibaba.fastjson.JSON;
import com.spire.doc.Document;
import com.spire.doc.Section;
import com.spire.doc.documents.*;
import com.spire.doc.fields.DocPicture;
import com.taohongxu.blockchain.Entity.DAO.schoolDAO;
import com.taohongxu.blockchain.Entity.DAO.studentCertificateDAO;
import com.taohongxu.blockchain.Entity.school;
import com.taohongxu.blockchain.Entity.student;
import com.taohongxu.blockchain.socket.peerTopeer.tools.SHAEncryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @Author ArdentXu
 * 2021/3/23
 * 电子认证证书生成*/
@Service
public class CertificateGeneratedService{
    @Autowired
    schoolDAO schoolDAO;

    @Autowired
    studentCertificateDAO studentCertificateDAO;

    @Value("${name}")
    String name;

    public int generated(student student) throws IOException {
        String changeEnglish = "";
        if(student.getEducation().equals("硕士")){
            changeEnglish = "master";
        }
        if(student.getEducation().equals("学士")){
            changeEnglish = "Bachelor";
        }
        if(student.getEducation().equals("博士")){
            changeEnglish = "doctor";
        }

        //这里需要在前端硬编码三个类型的学历
        Document document = new Document("/Users/xuhongtao/涛仔/"+changeEnglish+".docx");
        school school = schoolDAO.findBySchoolName(student.getSchool());
        Calendar calendar = Calendar.getInstance();
        String[] str = student.getBirthday().split("-");

        BookmarksNavigator bookmarksNavigator = new BookmarksNavigator(document);
        //寻找书签名字后将其替换为第二句里的东西
        bookmarksNavigator.moveToBookmark("stuName",false,true);
        bookmarksNavigator.insertText(student.getStu_name());

        bookmarksNavigator.moveToBookmark("sex",false,true);
        bookmarksNavigator.insertText(student.getSex());

        bookmarksNavigator.moveToBookmark("year",false,true);
        bookmarksNavigator.insertText(str[0]);

        bookmarksNavigator.moveToBookmark("month",false,true);
        bookmarksNavigator.insertText(str[1]);

        bookmarksNavigator.moveToBookmark("day",false,true);
        bookmarksNavigator.insertText(str[2]);

        bookmarksNavigator.moveToBookmark("subject",false,true);
        bookmarksNavigator.insertText(student.getMajor());

        bookmarksNavigator.moveToBookmark("degree",false,true);
        bookmarksNavigator.insertText(student.getDegreeType());

        bookmarksNavigator.moveToBookmark("schoolName",false,true);
        bookmarksNavigator.insertText(student.getSchool());

//        bookmarksNavigator.moveToBookmark("headmaster");
//        bookmarksNavigator.replaceBookmarkContent(school.getChartMan(),true);

        bookmarksNavigator.moveToBookmark("genYear",false,true);
        bookmarksNavigator.insertText(""+calendar.get(Calendar.YEAR));

        bookmarksNavigator.moveToBookmark("genMonth",false,true);
        bookmarksNavigator.insertText(""+(calendar.get(Calendar.MONTH)+1));

        bookmarksNavigator.moveToBookmark("genDay",false,true);
        bookmarksNavigator.insertText(""+calendar.get(Calendar.DAY_OF_MONTH));

        //替换书签图片

        Paragraph par = new Paragraph(document);

        DocPicture picture = par.appendPicture(student.getImagePath());
        picture.setWidth(200f);
        picture.setHeight(200f);
        picture.setTextWrappingStyle(TextWrappingStyle.Through);

        bookmarksNavigator.moveToBookmark("photo",true,false);
        bookmarksNavigator.insertParagraph(par);

        BufferedImage bufferedImage = document.saveToImages(0, ImageType.Bitmap);
        File file = new File("/Users/xuhongtao/涛仔/blockchainData/DigitalCertificate/",name+"/"+student.getStu_num()+".png");


        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }

        ImageIO.write(bufferedImage,"PNG",file);
        return 1;
    }
}
