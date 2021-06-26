package com.taohongxu.blockchain.socket.peerTopeer;

public interface packetType {
    byte NEXT_BLOCK = 1;
    byte NO_NEXT_BLOCK = 0;
    byte REQUEST_NEXT_BLOCK = 2;
    byte GET_BLOCK = 3;
    byte TRUE_BLOCK = 4;
    byte NEW_BLOCKCHAIN = 5;
    byte SYNC_BLOCKCHAIN = -5;
    byte NO_GEN_BLOCK = -4;
    byte PBFT = -3;
}
