package com.taohongxu.blockchain.socket.peerTopeer.Server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.taohongxu.blockchain.ApplicationContextProvider;
import com.taohongxu.blockchain.Entity.socketEntity.ChannelPacket;
import com.taohongxu.blockchain.Entity.blockEntity.block;
import com.taohongxu.blockchain.socket.peerTopeer.Client.blockChainClientStarter;
import com.taohongxu.blockchain.socket.peerTopeer.event.*;
import com.taohongxu.blockchain.socket.peerTopeer.packetType;
import com.taohongxu.blockchain.socket.peerTopeer.pbft.VoteEnum;
import com.taohongxu.blockchain.socket.peerTopeer.pbft.VoteInfo;
import com.taohongxu.blockchain.socket.peerTopeer.blockPacket;
import com.taohongxu.blockchain.socket.peerTopeer.tools.merkleTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.tio.core.ChannelContext;
import org.tio.core.Node;
import org.tio.core.Tio;
import org.tio.core.TioConfig;
import org.tio.core.exception.TioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioHandler;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Set;

import static com.taohongxu.blockchain.service.JSONService.voteList;

public class blockChainServerAioHandler implements ServerAioHandler  {
    @Value("${name}")
    String appName;
    private final Logger logger = LoggerFactory.getLogger(blockChainServerAioHandler.class);
    private JSONObject jo;
    private int commitCount = 0;
    private int NodeSize;
    //private final blockChainClientStarter starter = ApplicationContextProvider.getBean(blockChainClientStarter.class);
    /**
     * 服务端的解码
     * */
    @Override
    public blockPacket decode(ByteBuffer byteBuffer, int i, int i1, int readableLength, ChannelContext channelContext) throws TioDecodeException {

        if(readableLength < blockPacket.HEADER_LENGTH){
            return null;
        }

        byte type = byteBuffer.get();

        int bodyLength = byteBuffer.getInt();

        if(bodyLength < 0){
            throw new TioDecodeException("bodyLength["+bodyLength+"] is not right,remote" + channelContext.getClientNode());
        }

        int needLength = blockPacket.HEADER_LENGTH + bodyLength;

        int isDataEnough = readableLength - needLength;
        if(isDataEnough < 0){
            return null;
        }
        else {
            blockPacket bp = new blockPacket();
            bp.setType(type);
            logger.info("服务端： 解析中的body长度为： "+bodyLength);
            if(bodyLength > 0){
                byte[] dst = new byte[bodyLength];
                byteBuffer.get(dst);
                bp.setBody(dst);
            }
            return bp;
        }

    }


    /**
     * 将packet编码成可以发送的byte[]
     * 1.消息结构 = 消息头+消息体
     */
    @Override
    public ByteBuffer encode(Packet packet, TioConfig tioConfig, ChannelContext channelContext) {
        blockPacket blockPacket = (blockPacket)packet;
        byte[] body = blockPacket.getBody();
        int bodyLength = 0;
        if(body != null) {
            bodyLength = body.length;
        }
        logger.info("服务端： 封装中的body长度为： "+bodyLength);
        int allLen = blockPacket.getHEADER_LENGTH() + bodyLength;
        ByteBuffer buffer = ByteBuffer.allocate(allLen);

        //设置字节序
        buffer.order(tioConfig.getByteOrder());

        //消息类型
        buffer.put(blockPacket.getType());

        //写入消息头，消息头的内容就是消息体的长度
        buffer.putInt(bodyLength);

        //写入消息体
        if(body != null){
            buffer.put(body);
        }
        return buffer;

    }

    @Override
    public void handler(Packet packet, ChannelContext channelContext) throws Exception {
        blockPacket blockPacket = (blockPacket) packet;
        byte[] body = blockPacket.getBody();
        byte msg = blockPacket.getType();
        //logger.info("服务端：handler中的body: "+body.length);
        if(body != null){
            String str = new String(body, com.taohongxu.blockchain.socket.peerTopeer.blockPacket.CHARSET);
            logger.info("广东服务端收到信息: " + str);
            //如果全都没得那就意味着是普通的信息而已，在区块链系统中这种应该很少。。

//            blockPacket request = new blockPacket();
//            request.setBody(("广东服务端收到了你的信息： "+str).getBytes(com.taohongxu.blockchain.socket.peerTopeer.blockPacket.CHARSET));

            if(msg == packetType.NEXT_BLOCK){
                logger.info("收到请求: next_block");
                ChannelPacket packet1 = new ChannelPacket();
                packet1.setChannelContext(channelContext);
                packet1.setHash(str);
                ApplicationContextProvider.publishEvent(new nextBlockEvent(packet1));
            }




            if(msg == packetType.GET_BLOCK){
                logger.info("收到请求：获取完整区块");
                ChannelPacket channelPacket = new ChannelPacket();
                channelPacket.setChannelContext(channelContext);
                channelPacket.setHash(str);
                ApplicationContextProvider.publishEvent(new trueBlockEvent(channelPacket));
            }



            if(msg == packetType.NEW_BLOCKCHAIN){
                logger.info("收到请求：新生联盟节点请求创世块");
                ChannelPacket packet1 = new ChannelPacket();
                packet1.setChannelContext(channelContext);
                ApplicationContextProvider.publishEvent(new new_BlockchainEvent(packet1));
            }






//            Tio.send(channelContext,request);
            //如果收到的是json数据，则说明进入了pbft状态
            if(msg == packetType.PBFT){
                JSONObject jsonObject = JSON.parseObject(str);
                if(!jsonObject.containsKey("code")){
                    blockPacket blockPacket1 = new blockPacket();
                    blockPacket1.setBody(str.getBytes(com.taohongxu.blockchain.socket.peerTopeer.blockPacket.CHARSET));
                    blockPacket1.setType(packetType.PBFT);
                    this.jo = jsonObject;
                    Tio.send(channelContext,blockPacket1);
                    logger.info("服务器: json数据包中不包含code！");
                    return;
                }
                logger.info("ServerHandle中的jsonObject: "+jsonObject + ", jo为：" +jo);
                int code = jsonObject.getIntValue("code");

                if(code == VoteEnum.PREPREPARE.getCode()){
                    VoteInfo voteInfo = JSON.parseObject(str,VoteInfo.class);
                    if(!voteInfo.getHash().equals(new merkleTree(voteInfo.getVoteList()).getTreeNodeHash())){
                        logger.info("服务器: 服务端收到不合法json数据");
                        return;
                    }

                    List<String> students = voteList(jsonObject);

                    blockPacket bp = new blockPacket();
                    VoteInfo vi = createVoteInfo(VoteEnum.PREPARE,students);
                    bp.setBody(JSON.toJSONString(vi).getBytes(com.taohongxu.blockchain.socket.peerTopeer.blockPacket.CHARSET));
                    bp.setType(packetType.PBFT);
                    Tio.send(channelContext,bp);
                    logger.info("服务器: 向客户端发送了一条PBFT消息" + JSON.toJSONString(vi));

                }
                if(code == VoteEnum.COMMIT.getCode()){
                    VoteInfo voteInfo1 = JSON.parseObject(str,VoteInfo.class);
                    if(!voteInfo1.getHash().equals(new merkleTree(voteInfo1.getVoteList()).getTreeNodeHash())){
                        logger.info("错误的json化数据");
                        return;
                    }
                    commitCount++;
                    if(getConnectedNodeCount() >= getLeastNodeCount()) {
                        block newBlock = JSON.toJavaObject(jo, block.class);
                        logger.info("newBlock = " + newBlock.getBlockHead().getPublicKey());
                        /*
                         * 已修正
                         * */
                        ApplicationContextProvider.publishEvent(new addBlockEvent(newBlock));

                        blockPacket resPacket = new blockPacket();
                        resPacket.setBody(("服务端入库啦！").getBytes(com.taohongxu.blockchain.socket.peerTopeer.blockPacket.CHARSET));
                        commitCount = 0;
                        Tio.send(channelContext, resPacket);
                    }
                }
            }


        }
        return;
    }
    //升级版，之前是直接在方法内置一个List，这个升级完的方法是直接将要上联的数据传入。
    //动态感杠杠的！！
    private VoteInfo createVoteInfo(VoteEnum ve,List<String> students){
        VoteInfo vi = new VoteInfo();
        vi.setCode(ve.getCode());
        vi.setHash(new merkleTree(students).getTreeNodeHash());
        vi.setVoteList(students);
        return vi;
    }

    //每次commit之后会加一
    private int getConnectedNodeCount(){
        return commitCount;
    }
    //2f+1
    private int getLeastNodeCount(){
        return NodeSize;
    }

    @EventListener(NodeSizeEvent.class)
    public void NodeSizeListener(NodeSizeEvent event){
        this.NodeSize = event.getSource();
    }

}
//starter.pbftAgreeCount()