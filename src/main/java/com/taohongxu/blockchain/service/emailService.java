package com.taohongxu.blockchain.service;


import com.taohongxu.blockchain.Controller.event.applyForEvent;
import com.taohongxu.blockchain.Entity.mailDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * @Author ArdentXu
 * 2021/3/25
 * 发送邮箱的service*/
@Service
public class emailService {

    @Value("${localEmail}")
    String localEmail;

    @Autowired
    JavaMailSender javaMailSender;
    /**
     * 可以设置为事件
     * 当企业请求查询，由服务端发送邮件给企业邮箱告知公钥*/
    @EventListener(value = applyForEvent.class)
    public void sentMail(mailDetail mailDetail){
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(mailDetail.getFrom());
        msg.setTo(mailDetail.getTo());
        msg.setSubject(mailDetail.getSubject());
        msg.setText(mailDetail.getContext());
        javaMailSender.send(msg);
    }

    public boolean sendMailWithAttachment(File file,String privateKey){
        mailDetail mailDetail = detail(privateKey);
        try{
            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg,true);
            helper.setFrom(mailDetail.getFrom());
            helper.setTo(mailDetail.getTo());
            helper.setSubject(mailDetail.getSubject());
            helper.setText(mailDetail.getContext());
            helper.addAttachment(file.getName(),file);
            javaMailSender.send(msg);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private mailDetail detail(String privateKey){
        mailDetail mailDetail = new mailDetail();
        mailDetail.setFrom("2574753055@qq.com");
        mailDetail.setTo(localEmail);
        mailDetail.setContext("上链成功！区块私钥为："+privateKey+",请妥善保管密钥。 对应的电子哈希值在附录中");
        mailDetail.setSubject("上链成功");
        return mailDetail;
    }

}
