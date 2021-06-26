package com.taohongxu.blockchain.socket.peerTopeer.event;

import org.springframework.context.ApplicationEvent;
import org.tio.core.ChannelContext;

public class afterConnectedEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1093517220807263095L;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param channelContext the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public afterConnectedEvent(ChannelContext channelContext) {
        super(channelContext);
    }

    public ChannelContext getSource(){return (ChannelContext)source;}
}
