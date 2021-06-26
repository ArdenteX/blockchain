package com.taohongxu.blockchain.socket.peerTopeer.event;

import com.taohongxu.blockchain.Entity.blockEntity.block;
import org.springframework.context.ApplicationEvent;

public class addNextBlockEvent extends ApplicationEvent {
    private static final long serialVersionUID = 861458147981304000L;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param body the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public addNextBlockEvent(byte[] body) {
        super(body);
    }

    public byte[] getResource(){
        return (byte[]) source;
    }
}
