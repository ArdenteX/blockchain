package com.taohongxu.blockchain.service;


import com.taohongxu.blockchain.Entity.blockEntity.block;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@Component
public class blockchainCache {
    private List<block> blocks = new CopyOnWriteArrayList<>();

    public List<block> getBlocks() {
        return blocks;
    }

}

