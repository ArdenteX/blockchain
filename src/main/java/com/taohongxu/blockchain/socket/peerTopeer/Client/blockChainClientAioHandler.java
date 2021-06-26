package com.taohongxu.blockchain.socket.peerTopeer.Client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taohongxu.blockchain.ApplicationContextProvider;
import com.taohongxu.blockchain.Entity.blockEntity.block;
import com.taohongxu.blockchain.socket.peerTopeer.event.SyncEvent;
import com.taohongxu.blockchain.socket.peerTopeer.event.addNextBlockEvent;
import com.taohongxu.blockchain.socket.peerTopeer.event.addTrueBlockEvent;
import com.taohongxu.blockchain.socket.peerTopeer.event.commitEvent;
import com.taohongxu.blockchain.socket.peerTopeer.packetType;
import com.taohongxu.blockchain.socket.peerTopeer.pbft.VoteEnum;
import com.taohongxu.blockchain.socket.peerTopeer.pbft.VoteInfo;
import com.taohongxu.blockchain.socket.peerTopeer.blockPacket;
import com.taohongxu.blockchain.service.JSONService;
import com.taohongxu.blockchain.socket.peerTopeer.tools.merkleTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.intf.ClientAioHandler;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.core.TioConfig;
import org.tio.core.exception.TioDecodeException;
import org.tio.core.intf.Packet;

import java.nio.ByteBuffer;
import java.util.List;

import static com.taohongxu.blockchain.service.JSONService.students;
import static com.taohongxu.blockchain.socket.peerTopeer.packetType.PBFT;

public class blockChainClientAioHandler implements ClientAioHandler {
    private Logger logger = LoggerFactory.getLogger(blockChainClientAioHandler.class);
    private static blockPacket heartbeatPacket = new blockPacket();
    @Override
    public Packet heartbeatPacket(ChannelContext channelContext) {
        return heartbeatPacket;
    }

    /*为了服务setBody()*/
    @Override
    public blockPacket decode(ByteBuffer byteBuffer, int i, int i1, int readableLength, ChannelContext channelContext) throws TioDecodeException {

        if(readableLength < blockPacket.HEADER_LENGTH){
            return null;
        }
        byte type = byteBuffer.get();

        int bodyLength = byteBuffer.getInt();

        if(bodyLength < 0) {
            return null;
        }

        int needLength = blockPacket.HEADER_LENGTH + bodyLength;

        int isEnoughLength = readableLength - needLength;
        if(isEnoughLength < 0){
            throw new TioDecodeException("bodyLength["+bodyLength+"] is not right,remote" + channelContext.getClientNode());
        }
        else {
            blockPacket bp = new blockPacket();
            bp.setType(type);
            if(bodyLength > 0){
                byte[] dst = new byte[bodyLength];
                byteBuffer.get(dst);
                bp.setBody(dst);
            }
            return bp;
        }

    }

    /**
     * 消息头：type+bodyLength*/
    @Override
    public ByteBuffer encode(Packet packet, TioConfig tioConfig, ChannelContext channelContext) {
        blockPacket blockPacket = (blockPacket)packet;
       byte[] body = blockPacket.getBody();
       int bodyLength = 0;
       if(body != null){
           bodyLength = body.length;
       }
       int allen = bodyLength + blockPacket.getHEADER_LENGTH();

       ByteBuffer buffer = ByteBuffer.allocate(allen);
       buffer.order(tioConfig.getByteOrder());

       buffer.put(blockPacket.getType());

       buffer.putInt(bodyLength);
       if(body != null){
           buffer.put(body);
       }
       return buffer;
    }

    @Override
    public void handler(Packet packet, ChannelContext channelContext) throws Exception {
//        List<String> list = new ArrayList<>();
        blockPacket bp = (blockPacket)packet;
        byte msg = bp.getType();
        byte[] body = bp.getBody();
        if(body != null){
            String str = new String(body,blockPacket.CHARSET);
            logger.info("广东客户端收到信息: "+str);

            if("广东服务端入库啦！".equals(str)){
                return;
            }

            if(msg == packetType.NO_NEXT_BLOCK){
                logger.info("群组服务端没有新区块");
                return;
            }

            if(msg == packetType.NO_GEN_BLOCK){
                logger.info("群组中没有创世块");
                return;
            }

            if(msg == packetType.REQUEST_NEXT_BLOCK){
                ApplicationContextProvider.publishEvent(new addNextBlockEvent(body));
            }

            if(msg == packetType.TRUE_BLOCK){
                JSONObject jsonObject = JSON.parseObject(str);
                block trueBlock = JSONObject.toJavaObject(jsonObject,block.class);
                ApplicationContextProvider.publishEvent(new addTrueBlockEvent(trueBlock));
            }

            if(msg == packetType.SYNC_BLOCKCHAIN){
                logger.info("已收到创世块");
                JSONObject jsonObject = JSON.parseObject(str);
                block genBlock = JSON.toJavaObject(jsonObject,block.class);
                ApplicationContextProvider.publishEvent(new SyncEvent(genBlock));

            }


//            if(!str.startsWith("{")){
////                VoteInfo vi = createVoteInfo(VoteEnum.PREPREPARE);
//                blockPacket sent = new blockPacket();
//                sent.setBody(str.getBytes(blockPacket.CHARSET));
//                //Tio.send(channelContext,sent);
//                logger.info("客户端发送到服务端的信息: " + JSON.toJSONString(str));
//                return;
//            }


            if(msg == PBFT){
                //如果是json化数据，则证明进入了pbft投票阶段
                JSONObject json = JSON.parseObject(str);

                if(!json.containsKey("code")){
                    logger.info("客户端收到了没有code的json数据包！！");
                    List<String> students = students(json);

                    VoteInfo vi = createVoteInfo(VoteEnum.PREPREPARE,students);
                    blockPacket sent = new blockPacket();
                    sent.setBody(JSON.toJSONString(vi).getBytes(blockPacket.CHARSET));
                    sent.setType(PBFT);
                    Tio.send(channelContext,sent);
                    return;
                }

                int code = json.getIntValue("code");
                if(code == VoteEnum.PREPARE.getCode()){
                    VoteInfo voteInfo = JSON.parseObject(str,VoteInfo.class);
                    if(!voteInfo.getHash().equals(new merkleTree(voteInfo.getVoteList()).getTreeNodeHash())){
                        logger.info("收到了非json化数据！！");
                        return;
                    }

//                JSONArray jsonArray = json.getJSONArray("voteList");
//                String stu = JSONObject.toJSONString(jsonArray, SerializerFeature.WriteClassName);

                    List<String> students = JSONService.voteList(json);
                    VoteInfo vi = createVoteInfo(VoteEnum.COMMIT,students);

                    blockPacket reBp = new blockPacket();
                    reBp.setBody(JSON.toJSONString(vi).getBytes(blockPacket.CHARSET));
                    reBp.setType(PBFT);

                    //发送事件让starter接收并群发信息
                    ApplicationContextProvider.publishEvent(new commitEvent(reBp));

                    logger.info("客户端发送到服务端的信息: " + JSON.toJSONString(vi));
                }
            }

        }
        return;
    }
    private VoteInfo createVoteInfo(VoteEnum ve,List<String> students){
        VoteInfo vi = new VoteInfo();
        vi.setCode(ve.getCode());
        vi.setHash(new merkleTree(students).getTreeNodeHash());
        vi.setVoteList(students);
        return vi;
    }
}
