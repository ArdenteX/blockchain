package com.taohongxu.blockchain.socket.peerTopeer.event;


import com.taohongxu.blockchain.Entity.blockEntity.block;
import org.springframework.context.ApplicationEvent;

public class addBlockEvent extends ApplicationEvent {
    private static final long serialVersionUID = 271018154089359365L;

    public addBlockEvent(block newBlock){
        super(newBlock);
    }
}
