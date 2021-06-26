package com.taohongxu.blockchain.socket.peerTopeer.event;

import com.taohongxu.blockchain.Entity.blockEntity.block;
import org.springframework.context.ApplicationEvent;

public class addTrueBlockEvent extends ApplicationEvent {
    private static final long serialVersionUID = 7796170911968313985L;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param block the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public addTrueBlockEvent(block block) {
        super(block);
    }

    @Override
    public block getSource() {
        return (block)source;
    }
}
