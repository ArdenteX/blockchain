package com.taohongxu.blockchain.socket.peerTopeer.event;

import com.taohongxu.blockchain.Entity.socketEntity.ChannelPacket;
import org.springframework.context.ApplicationEvent;

public class new_BlockchainEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1453457683318942461L;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param ChannelPacket the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public new_BlockchainEvent(ChannelPacket ChannelPacket) {
        super(ChannelPacket);
    }

    @Override
    public ChannelPacket getSource() {
        return (ChannelPacket) source;
    }
}
