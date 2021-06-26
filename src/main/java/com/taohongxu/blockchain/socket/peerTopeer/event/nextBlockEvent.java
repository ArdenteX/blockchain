package com.taohongxu.blockchain.socket.peerTopeer.event;

import com.taohongxu.blockchain.Entity.socketEntity.ChannelPacket;
import org.springframework.context.ApplicationEvent;

public class nextBlockEvent extends ApplicationEvent {
    private static final long serialVersionUID = 5585409608421800451L;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param packet the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public nextBlockEvent(ChannelPacket packet) {
        super(packet);
    }

    public ChannelPacket getResource(){
        return (ChannelPacket) source;
    }
}
