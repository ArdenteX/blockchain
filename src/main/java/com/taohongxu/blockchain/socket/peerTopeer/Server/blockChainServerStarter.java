package com.taohongxu.blockchain.socket.peerTopeer.Server;

import com.taohongxu.blockchain.socket.peerTopeer.networkConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.tio.core.Tio;
import org.tio.server.ServerTioConfig;
import org.tio.server.TioServer;
import org.tio.server.intf.ServerAioHandler;
import org.tio.server.intf.ServerAioListener;

import javax.annotation.PostConstruct;


/**
 * 由上往下：
 * 1.TioServer需要ServerTioConfig
 * 2.ServerTioConfig需要ServerAioHandler和ServerAioListener
 * 3.start需要ip和port
 */

@Component
//@Order(1)
public class blockChainServerStarter {
    private static final Logger logger = LoggerFactory.getLogger(blockChainServerStarter.class);
    public static ServerAioHandler serverAioHandler = new blockChainServerAioHandler();
    public static ServerAioListener serverAioListener = null;
    public static ServerTioConfig serverTioConfig = new ServerTioConfig("tio-server",serverAioHandler,serverAioListener);
    public static TioServer tioServer = new TioServer(serverTioConfig);
    public static String serverIp = networkConfig.server;
    public static int port = networkConfig.port;

    @PostConstruct
    public static void start(){
        try{
            logger.info("广东服务器即将启动");
            serverTioConfig.setHeartbeatTimeout(networkConfig.TIMEOUT);
            tioServer.start(serverIp,port);
            logger.info("服务器已启动");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
