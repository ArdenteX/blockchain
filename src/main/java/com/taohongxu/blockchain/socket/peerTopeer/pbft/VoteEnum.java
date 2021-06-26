package com.taohongxu.blockchain.socket.peerTopeer.pbft;


public enum VoteEnum {
    PREPREPARE("自己创建节点",100),PREPARE("节点收到请求生成新节点，进入准备状态，并广播给所有节点",200)
    ,COMMIT("进入commit状态",500);
    private String msg;
    private int code;

    public static VoteEnum find(int code){
        for(VoteEnum e : VoteEnum.values()){
            if(code == e.code){
                return e;
            }
        }
        return null;
    }

    VoteEnum(String msg,int code){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
