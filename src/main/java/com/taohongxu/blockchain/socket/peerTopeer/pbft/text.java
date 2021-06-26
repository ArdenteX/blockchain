package com.taohongxu.blockchain.socket.peerTopeer.pbft;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class text {
    @Id
    private String id;
    private String text_id;
    private List<String> list;

    public void setText_id(String text_id) {
        this.text_id = text_id;
    }

    public List<String> getList() {
        return list;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
