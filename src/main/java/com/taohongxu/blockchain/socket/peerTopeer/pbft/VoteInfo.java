package com.taohongxu.blockchain.socket.peerTopeer.pbft;

import java.util.List;

public class VoteInfo {
    //待写入的区块内容的merkle树根节点哈希值
    private String Hash;
    //带写入的区块
    private List<String> VoteList;
    //状态码
    private int code;

    public String getHash() {
        return Hash;
    }

    public List<String> getVoteList() {
        return VoteList;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setHash(String hash) {
        Hash = hash;
    }

    public void setVoteList(List<String> voteList) {
        VoteList = voteList;
    }
}
