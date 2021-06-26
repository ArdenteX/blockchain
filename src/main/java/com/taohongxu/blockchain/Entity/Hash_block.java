package com.taohongxu.blockchain.Entity;

import lombok.Data;

@Data
public class Hash_block {
    private String Hash;
    private com.taohongxu.blockchain.Entity.blockEntity.block block;
}
