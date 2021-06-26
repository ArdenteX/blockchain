package com.taohongxu.blockchain.Entity.socketEntity;

import lombok.Data;

@Data
public class Member {
    private String appId;
    private String name;
    //可以让客户端自动获取
    private String ip;
    private String createTime;
    private String updateTime;

    public String toString(){
        return "appId="+appId+
                "name="+name+
                "ip="+ip+
                "createTime="+createTime+
                "updateTime="+updateTime;
    }
}
