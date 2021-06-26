package com.taohongxu.blockchain.Entity.socketEntity;

import lombok.Data;

import java.util.List;

@Data
public class MemberData {
    private String code;
    private String msg;
    private List<Member> members;
}
