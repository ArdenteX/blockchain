package com.taohongxu.blockchain.Entity.blockEntity;

import com.taohongxu.blockchain.Entity.student;
import com.taohongxu.blockchain.socket.peerTopeer.tools.SHAEncryption;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.hateoas.RepresentationModel;


@Document(collection = "block")
public class block {
    //block头
    private com.taohongxu.blockchain.Entity.blockEntity.blockHead blockHead;
    //blockBody
    private com.taohongxu.blockchain.Entity.blockEntity.blockBody blockBody;
    //blockHash
    @Id
    private String hash;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public com.taohongxu.blockchain.Entity.blockEntity.blockBody getBlockBody() {
        return blockBody;
    }

    public com.taohongxu.blockchain.Entity.blockEntity.blockHead getBlockHead() {
        return blockHead;
    }

    public void setBlockBody(com.taohongxu.blockchain.Entity.blockEntity.blockBody blockBody) {
        this.blockBody = blockBody;
    }

    public void setBlockHead(com.taohongxu.blockchain.Entity.blockEntity.blockHead blockHead) {
        this.blockHead = blockHead;
    }
}
