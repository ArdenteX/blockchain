package com.taohongxu.blockchain.Entity;


import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.File;
import java.util.Date;

@Document
public class student  {
    //学号
    @Id
    private String stu_num;
    //姓名
    private String stu_name;
    //性别
    private String sex;
    //所属学校
    private String school;
    //个人证件照
    private String imagePath = "";
    //生日日期
    private String birthday;
    //专业
    private String major;
    //邮箱
    private String email;
    //学位类型（工学、理学）
    private String degreeType;
    //学历（学士、硕士、博士）
    private String education;

    public student(){}

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


    public void setSchool(String school) {
        this.school = school;
    }

    public void setStu_name(String stu_name) {
        this.stu_name = stu_name;
    }

    public void setStu_num(String stu_num) {
        this.stu_num = stu_num;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getSchool() {
        return school;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getStu_name() {
        return stu_name;
    }

    public String getMajor() {
        return major;
    }

    public String getStu_num() {
        return stu_num;
    }

    public String getDegreeType() {
        return degreeType;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEducation() {
        return education;
    }

    public String getEmail() {
        return email;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setDegreeType(String degreeType) {
        this.degreeType = degreeType;
    }

    public void setEducation(String education) {
        this.education = education;
    }
}
