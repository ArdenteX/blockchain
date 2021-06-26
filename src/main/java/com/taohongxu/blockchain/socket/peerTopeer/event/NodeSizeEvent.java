package com.taohongxu.blockchain.socket.peerTopeer.event;

import org.springframework.context.ApplicationEvent;

public class NodeSizeEvent extends ApplicationEvent {
    private static final long serialVersionUID = -4467763449355251695L;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param NodeSize the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public NodeSizeEvent(int NodeSize) {
        super(NodeSize);
    }

    @Override
    public Integer getSource() {
        return (int) source;
    }
}
