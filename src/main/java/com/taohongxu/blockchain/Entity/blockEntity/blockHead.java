package com.taohongxu.blockchain.Entity.blockEntity;


import com.taohongxu.blockchain.socket.peerTopeer.tools.merkleTree;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.List;

@Data
public class blockHead {
    //版本
    private String version;
    //时间戳
    private long timeStamp;
    //上一块的hash
    private String preHash;
    //merkle树根节点哈希值
    private String merkleTreeRootHash;
    //区块公钥
    private String publicKey;
    //区块的序号
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int number;
    //存储该区块中的每一个上传学生模块的hash
    private List<String> hashList;

    public String toString(){
        return "blockHead{ "+
                "version = " + version+
                "timeStamp = " + timeStamp+
                "preHash = " + preHash +
                "merkleTreeRootHash = " + merkleTreeRootHash+
                "publicKey = " + publicKey+
                "number = " + number+
                "hashList = " + hashList+
                " }";
    }

    public String getMerkleTreeRootHash() {
        return new merkleTree(hashList).getTreeNodeHash();
    }

    public int getNumber() {
        return number;
    }

    public List<String> getHashList() {
        return hashList;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getPreHash() {
        return preHash;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getVersion() {
        return version;
    }

    public void setHashList(List<String> hashList) {
        this.hashList = hashList;
    }

    public void setMerkleTreeRootHash(String merkleTreeRootHash) {
        this.merkleTreeRootHash = merkleTreeRootHash;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setPreHash(String preHash) {
        this.preHash = preHash;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
