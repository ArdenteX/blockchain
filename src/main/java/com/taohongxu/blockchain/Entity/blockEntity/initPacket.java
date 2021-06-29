package com.taohongxu.blockchain.Entity.blockEntity;

import lombok.Data;

@Data
public class initPacket {
    private block block;
    private String privateKey;
    private String createStatue;
}
