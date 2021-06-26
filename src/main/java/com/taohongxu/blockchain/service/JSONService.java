package com.taohongxu.blockchain.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.taohongxu.blockchain.Entity.blockEntity.block;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JSONService {

    public static List<String> students(JSONObject json){
        JSONObject json2 = JSON.parseObject(json.toJSONString());
        String blockBody1 = json2.getString("blockBody");
        JSONObject json1 = JSON.parseObject(blockBody1);
        JSONArray j1 = json1.getJSONArray("students");
        String str = JSON.toJSONString(j1, SerializerFeature.WriteClassName);
        return JSON.parseArray(str,String.class);
    }

    public static List<String> voteList(JSONObject json){
        JSONArray jsonArray = json.getJSONArray("voteList");
        String stu = JSONObject.toJSONString(jsonArray, SerializerFeature.WriteClassName);
        return JSONArray.parseArray(stu,String.class);
    }

    public static block JSONtoBlock(JSON json){
        return JSON.toJavaObject(json,block.class);
    }

}
