package com.taohongxu.blockchain.socket.peerTopeer.Client;

import com.alibaba.fastjson.JSON;
import com.taohongxu.blockchain.ApplicationContextProvider;
import com.taohongxu.blockchain.Entity.blockEntity.block;
import com.taohongxu.blockchain.Entity.socketEntity.Member;
import com.taohongxu.blockchain.Entity.socketEntity.MemberData;
import com.taohongxu.blockchain.Entity.socketEntity.blocksCheck;
import com.taohongxu.blockchain.service.MongoDBService;
import com.taohongxu.blockchain.socket.peerTopeer.event.NodeSizeEvent;
import com.taohongxu.blockchain.socket.peerTopeer.event.afterConnectedEvent;
import com.taohongxu.blockchain.socket.peerTopeer.blockPacket;
import com.taohongxu.blockchain.socket.peerTopeer.event.blockCheckEvent;
import com.taohongxu.blockchain.socket.peerTopeer.event.commitEvent;
import com.taohongxu.blockchain.socket.peerTopeer.networkConfig;
import com.taohongxu.blockchain.socket.peerTopeer.packetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.tio.client.*;
import org.tio.client.intf.ClientAioHandler;
import org.tio.client.intf.ClientAioListener;
import org.tio.core.ChannelContext;
import org.tio.core.Node;
import org.tio.core.Tio;
import org.tio.utils.lock.SetWithLock;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;


@Component
@DependsOn("blockChainServerStarter")
public class blockChainClientStarter{
    public static Logger logger = LoggerFactory.getLogger(blockChainClientStarter.class);
    public static Node serverNode = new Node(networkConfig.server,networkConfig.port);
    public static ClientAioHandler clientAioHandler = new blockChainClientAioHandler();
    public static ClientAioListener clientAioListener = new blockchainClientAioListener();
    private static ReconnConf reconnConf = new ReconnConf(5000L);
    public static ClientTioConfig clientTioConfig = new ClientTioConfig(clientAioHandler,clientAioListener,reconnConf);
    public static TioClient tioClient = null;
    public static ClientChannelContext clientChannelContext = null;
    @Autowired
    MongoDBService dao;
    @Autowired
    RestTemplate restTemplate;

    @Value("${managerUrl}")
    private String manageUrl;
    @Value("${appID}")
    private String appID;
    @Value("${name}")
    private String name;
    @Value("${ip}")
    private String ip;
    @Value("${GroupName}")
    private String GroupName;

    private Set<Node> nodes = new HashSet<>();
    private Map<String,Integer> NodeStatus = new HashMap<>();
    private boolean isReady = false;
    private boolean isSingle = false;

    @PostConstruct
    public static void start() throws Exception{
            logger.info("广东客户端正在启动");
            clientTioConfig.setHeartbeatTimeout(networkConfig.TIMEOUT);
            logger.info("客户端启动完毕");
            logger.info("客户端已连接到服务器");
    }

    /**
     * @Author ArdentXu
     * 2021/4/12*/
    @Scheduled(fixedRate = 300000)
    public void catchOtherServe(){
            logger.info("本机ip:{}",ip);
            try{
                MemberData memberData = restTemplate.getForEntity(manageUrl+"member?name="+name+"&appId="+appID+"&ip="+ip,MemberData.class).getBody();
                //合法的客户端
                logger.info("memberData = " + memberData);
                assert memberData != null;
                if(memberData.getCode().equals("0")){
                    List<Member> members = memberData.getMembers();
                    logger.info("共有{}个节点连接",members.size());
                    nodes.clear();

                    for(Member member : members){
                        Node node = new Node(member.getIp(),networkConfig.port);
                        nodes.add(node);
                    }
                    //绑定到指定节点
                    bindServeGroup(nodes);

                }
                else {
                    logger.error("客户端不合法");
                    System.exit(0);
                }
            }catch (Exception e){
                logger.info("未启动区块节点服务器！");
                System.exit(0);
            }
    }

    //绑定nodes里的server
    public void bindServeGroup(Set<Node> nodes){
        SetWithLock<ChannelContext> setWithLock = Tio.getAll(clientTioConfig);
        Lock lock2 = setWithLock.getLock().readLock();
        lock2.lock();
        try{
            Set<ChannelContext> set = setWithLock.getObj();
            Set<Node> connectedNodes = set.stream().map(ChannelContext::getServerNode).collect(Collectors.toSet());

            for(Node node : nodes){
                if(!connectedNodes.contains(node)){
                    connect(node);
                }
            }

            for(ChannelContext channelContext : set){
                Node serveNode = channelContext.getServerNode();
                if(!nodes.contains(serveNode)){
                    Tio.remove(channelContext,"主动关闭: "+serveNode.getIp());
                }
            }

        }finally {
            lock2.unlock();
        }

    }

    private void connect(Node serverNode){
        try{
            TioClient tioClient = new TioClient(clientTioConfig);
            logger.info("开始连接服务端");
            tioClient.asynConnect(serverNode);
        }catch (Exception e){
            logger.info("连接出错");
        }
    }

    @EventListener(commitEvent.class)
    public void commitListener(commitEvent event){
        blockPacket packet = event.getSource();
        Tio.sendToGroup(clientTioConfig,GroupName,packet);
    }

    @EventListener(afterConnectedEvent.class)
    public void onConnected(afterConnectedEvent event){
        ChannelContext channelContext = event.getSource();
        Node node = channelContext.getServerNode();
        if(channelContext.isClosed){
            logger.info(node.toString()+"连接失败");
            NodeStatus.put(node.getIp(),-1);
        }
        else {
            logger.info(node.toString()+"连接成功！");
            NodeStatus.put(node.getIp(),1);
            Tio.bindGroup(channelContext,GroupName);
            int NodeSize = Tio.getAll(clientTioConfig).size();
            logger.info("当前PBFT允许数量为： " +pbftAgreeCount());
            if(NodeSize >= pbftAgreeCount()){
                ApplicationContextProvider.publishEvent(new NodeSizeEvent(NodeSize));
                if(!isReady){
                    isReady = true;
                    onNodesReady();
                }

            }
        }
    }
    @Scheduled(fixedRate = 30000)
    public void heartBeat(){

        if(!isReady){
            return;
        }

        blockPacket blockPacket = new blockPacket();


            String hash = dao.findLast().getHash();
            //没有区块，证明是新生的节点，所以要请求同步
            if(hash != null){
                blockPacket.setBody(JSON.toJSONString(hash).getBytes());
                blockPacket.setType(packetType.NEXT_BLOCK);
                logger.info("开始向group中的节点寻求next block");
                Tio.sendToGroup(clientTioConfig,GroupName,blockPacket);
            }
            else {
                logger.info("新生联盟节点，向群组寻求创世块");
                blockPacket.setType(packetType.NEW_BLOCKCHAIN);
                Tio.sendToGroup(clientTioConfig,GroupName,blockPacket);
            }






    }

    @Scheduled(fixedRate = 150000)
    public void checkHeartBeat(){
        if(!isReady){
            return;
        }

        blocksCheck blocksCheck = new blocksCheck();
        List<block> blocks = dao.findAllDESC();

        if(blocks == null){
            logger.info("暂未有区块");
            return;
        }

        if(blocks.size() <= 1){
            logger.info("暂不符合验证规则");
            return;
        }

        blocksCheck.setBlocks(blocks);
        blocksCheck.setClientTioConfig(clientTioConfig);
        ApplicationContextProvider.publishEvent(new blockCheckEvent(blocksCheck));
    }


    /**
     * 2021/4/12
     * 这里还有待验证 (4/13验证成功)
     * 特别是这个dao的findLast就离谱居然能built成功 (4/13验证成功 修改了一下)
     * 还差编写一个handler就完事了，基本思想：接收到NEXT_BLOCK之后传出一个事件，服务器检索自己的最后一个区块的hash是否为
     * 所携带包的hash，如果不是则向上查找此hash，最后将此hash之后的下一个区块传递回来并带上 REQUEST_NEXT_BLOCK的msg，
     * 万一不止一个也只能一次一个地传，避免出错。然后在另一个情况下也就是hash相同则返回 NO_NEXT_BLOCK的msg来表达两者的区块链是一样的
     */

    public void onNodesReady(){
        blockPacket blockPacket = new blockPacket();
               //已验证
        try{
            String hash = dao.findLast().getHash();
            blockPacket.setBody(JSON.toJSONString(hash).getBytes());
            blockPacket.setType(packetType.NEXT_BLOCK);
            logger.info("开始向group中的节点寻求next block");
            Tio.sendToGroup(clientTioConfig,GroupName,blockPacket);
        }catch (Exception e){
            logger.info("暂时查无区块");
        }


    }

    public void addBlock(String name,block blocks) throws Exception{
        blockPacket blockPacket1 = new blockPacket();

        byte[] bytes = JSON.toJSONString(blocks).getBytes(blockPacket.CHARSET);
        blockPacket1.setBody(bytes);
        blockPacket1.setType(packetType.PBFT);
        if(blockPacket1.getBody()!= null){
            Tio.sendToGroup(clientTioConfig,GroupName,blockPacket1);
            logger.info("传入一个新的区块！");
        }
        else {
            throw  new Exception("共识失败！");
        }

    }

    public int pfbtSize(){
        int total = nodes.size();
        int pbft = (total-1)/3;
        if(pbft <= 0){
            pbft = 1;
        }
        if(isSingle){
            return 0;
        }
        return pbft;
    }

    public int pbftAgreeCount(){
        return pfbtSize()*2+1;
    }



}
