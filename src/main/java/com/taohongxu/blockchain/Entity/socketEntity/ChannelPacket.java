package com.taohongxu.blockchain.Entity.socketEntity;

import com.taohongxu.blockchain.socket.peerTopeer.blockPacket;
import lombok.Data;
import org.tio.client.ClientTioConfig;
import org.tio.core.ChannelContext;

import java.util.List;

@Data
public class ChannelPacket {
    private ChannelContext channelContext;
    private String hash;
}
