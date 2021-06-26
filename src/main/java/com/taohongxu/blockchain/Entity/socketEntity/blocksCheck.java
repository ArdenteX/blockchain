package com.taohongxu.blockchain.Entity.socketEntity;


import com.taohongxu.blockchain.Entity.blockEntity.block;
import lombok.Data;
import org.tio.client.ClientTioConfig;

import java.util.List;

@Data
public class blocksCheck {
    private ClientTioConfig clientTioConfig;
    private List<block> blocks;
}
