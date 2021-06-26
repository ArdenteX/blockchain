package com.taohongxu.blockchain.socket.peerTopeer.event;

import com.taohongxu.blockchain.socket.peerTopeer.blockPacket;
import org.springframework.context.ApplicationEvent;

public class commitEvent extends ApplicationEvent {
    private static final long serialVersionUID = 6003244618639317852L;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param packet the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public commitEvent(blockPacket packet) {
        super(packet);
    }

    @Override
    public blockPacket getSource() {
        return (blockPacket) source;
    }
}
