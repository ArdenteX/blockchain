package com.taohongxu.blockchain.Entity.socketEntity;

import lombok.Data;

@Data
public class keyAndEncode {
    private byte[] signedEncode;
    private String publicKey;
}
