package com.taohongxu.blockchain.socket.peerTopeer;

import org.tio.core.intf.Packet;


public class blockPacket extends Packet{
    //获取这个uid的方法：implement一个Serialization后把鼠标放在类上有提示可以生成一个uid，生成后可以把implement删掉
    private  final long serialVersionUID = 625391899751436884L;
    public static int HEADER_LENGTH = 5;
    public static String CHARSET = "UTF-8";
    public byte type;
    private byte[] body;

    public int getHEADER_LENGTH() {
        return HEADER_LENGTH;
    }

    public byte[] getBody() {
        return body;
    }

    public long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getCHARSET() {
        return CHARSET;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }
}
