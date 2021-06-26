package com.taohongxu.blockchain.Entity.blockEntity;


import com.alibaba.fastjson.JSON;
import com.taohongxu.blockchain.Entity.student;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
public class blockBody  {
    //存储student实体类
    private List<student> students;

    public void setStudents(List<student> students) {
        this.students = students;
    }

    public List<student> getStudents() {
        return students;
    }

    public String toString(){
        return "blockBody{"
                    +"students = "+ JSON.toJSONString(students)+ "}" ;
    }
}
