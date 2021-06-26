package com.taohongxu.blockchain.socket.peerTopeer.Client;

import com.taohongxu.blockchain.ApplicationContextProvider;
import com.taohongxu.blockchain.socket.peerTopeer.event.afterConnectedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.TioClient;
import org.tio.client.intf.ClientAioListener;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.core.intf.Packet;

public class blockchainClientAioListener implements ClientAioListener {
    Logger logger = LoggerFactory.getLogger(blockchainClientAioListener.class);

    @Override
    public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect) throws Exception {
        ApplicationContextProvider.publishEvent(new afterConnectedEvent(channelContext));

    }

    @Override
    public void onAfterDecoded(ChannelContext channelContext, Packet packet, int packetSize) throws Exception {

    }

    @Override
    public void onAfterReceivedBytes(ChannelContext channelContext, int receivedBytes) throws Exception {

    }

    @Override
    public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess) throws Exception {

    }

    @Override
    public void onAfterHandled(ChannelContext channelContext, Packet packet, long cost) throws Exception {

    }

    @Override
    public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) throws Exception {
        logger.info("与服务端断开连接");
        Tio.unbindGroup(channelContext);
    }
}
