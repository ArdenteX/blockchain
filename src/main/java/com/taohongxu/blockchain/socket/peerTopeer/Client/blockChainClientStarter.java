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

    MongoDBService dao;
    RestTemplate restTemplate;

    @Autowired
    public blockChainClientStarter(MongoDBService dao,RestTemplate restTemplate){
        this.dao = dao;
        this.restTemplate = restTemplate;
    }

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
            logger.info("???????????????????????????");
            clientTioConfig.setHeartbeatTimeout(networkConfig.TIMEOUT);
            logger.info("?????????????????????");
            logger.info("??????????????????????????????");
    }

    /**
     * @Author ArdentXu
     * 2021/4/12*/
    @Scheduled(fixedRate = 300000)
    public void catchOtherServe(){
            logger.info("??????ip:{}",ip);
            logger.info("manager url:" + manageUrl+"member?name="+name+"&appId="+appID+"&ip="+ip);
            try{
                MemberData memberData = restTemplate.getForEntity(manageUrl+"member?name="+name+"&appId="+appID+"&ip="+ip,MemberData.class).getBody();
                //??????????????????
                logger.info("memberData = " + memberData);
                assert memberData != null;
                if(memberData.getCode().equals("0")){
                    List<Member> members = memberData.getMembers();
                    logger.info("??????{}???????????????",members.size());
                    nodes.clear();

                    for(Member member : members){
                        Node node = new Node(member.getIp(),networkConfig.port);
                        nodes.add(node);
                    }
                    //?????????????????????
                    bindServeGroup(nodes);

                }
                else {
                    logger.error("??????????????????");
                    System.exit(0);
                }
            }catch (Exception e){
                logger.info("?????????????????????????????????");
                e.printStackTrace();
                System.exit(0);
            }
    }

    //??????nodes??????server
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
                    Tio.remove(channelContext,"????????????: "+serveNode.getIp());
                }
            }

        }finally {
            lock2.unlock();
        }

    }

    private void connect(Node serverNode){
        try{
            TioClient tioClient = new TioClient(clientTioConfig);
            logger.info("?????????????????????");
            tioClient.asynConnect(serverNode);
        }catch (Exception e){
            logger.info("????????????");
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
            logger.info(node.toString()+"????????????");
            NodeStatus.put(node.getIp(),-1);
        }
        else {
            logger.info(node.toString()+"???????????????");
            NodeStatus.put(node.getIp(),1);
            Tio.bindGroup(channelContext,GroupName);
            int NodeSize = Tio.getAll(clientTioConfig).size();
            logger.info("??????PBFT?????????????????? " +pbftAgreeCount());
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



        try{
            String hash = dao.findLast().getHash();
            //???????????????????????????????????????????????????????????????
            blockPacket.setBody(JSON.toJSONString(hash).getBytes());
            blockPacket.setType(packetType.NEXT_BLOCK);
            logger.info("?????????group??????????????????next block");
            Tio.sendToGroup(clientTioConfig,GroupName,blockPacket);
        }catch (Exception e){
            logger.info("?????????????????????????????????????????????");
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
            logger.info("???????????????");
            return;
        }

        if(blocks.size() <= 1){
            logger.info("????????????????????????");
            return;
        }

        blocksCheck.setBlocks(blocks);
        blocksCheck.setClientTioConfig(clientTioConfig);
        ApplicationContextProvider.publishEvent(new blockCheckEvent(blocksCheck));
    }


    /**
     * 2021/4/12
     * ????????????????????? (4/13????????????)
     * ???????????????dao???findLast??????????????????built?????? (4/13???????????? ???????????????)
     * ??????????????????handler???????????????????????????????????????NEXT_BLOCK????????????????????????????????????????????????????????????????????????hash?????????
     * ???????????????hash?????????????????????????????????hash???????????????hash????????????????????????????????????????????? REQUEST_NEXT_BLOCK???msg???
     * ???????????????????????????????????????????????????????????????????????????????????????????????????hash??????????????? NO_NEXT_BLOCK???msg???????????????????????????????????????
     */

    public void onNodesReady(){
        blockPacket blockPacket = new blockPacket();
               //?????????
        try{
            String hash = dao.findLast().getHash();
            blockPacket.setBody(JSON.toJSONString(hash).getBytes());
            blockPacket.setType(packetType.NEXT_BLOCK);
            logger.info("?????????group??????????????????next block");
            Tio.sendToGroup(clientTioConfig,GroupName,blockPacket);
        }catch (Exception e){
            logger.info("??????????????????");
        }


    }

    public void addBlock(String name,block blocks) throws Exception{
        blockPacket blockPacket1 = new blockPacket();

        byte[] bytes = JSON.toJSONString(blocks).getBytes(blockPacket.CHARSET);
        blockPacket1.setBody(bytes);
        blockPacket1.setType(packetType.PBFT);
        if(blockPacket1.getBody()!= null){
            Tio.sendToGroup(clientTioConfig,GroupName,blockPacket1);
            logger.info("???????????????????????????");
        }
        else {
            throw  new Exception("???????????????");
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
        return 1;
    }

    //?????????????????????????????????
//    public int pbftAgreeCount(){
//        return pfbtSize()*2+1;
//    }



}
