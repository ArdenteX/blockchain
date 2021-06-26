package com.taohongxu.blockchain.socket.peerTopeer.event;

import com.taohongxu.blockchain.Entity.blockEntity.block;
import com.taohongxu.blockchain.Entity.socketEntity.blocksCheck;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class blockCheckEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1196083606161275967L;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param blocks the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public blockCheckEvent(blocksCheck blocks) {
        super(blocks);
    }

    @Override
    public blocksCheck getSource() {
        return (blocksCheck)source;
    }
}
