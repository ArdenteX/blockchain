package com.taohongxu.blockchain.Entity;


import com.taohongxu.blockchain.socket.peerTopeer.tools.SHAEncryption;


public class treeNode {
    private  treeNode leftChild;
    private  treeNode rightChild;
    private  String data;
    private  String hash;
    private  String name;
    public treeNode(){}
    public treeNode(String data){
        this.data = data;
        this.hash = SHAEncryption.SHAByHutool(data);
        this.name = "结点:" + data;
    }

    public String getData() {
        return data;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHash() {
        return hash;
    }

    public String getName() {
        return name;
    }

    public treeNode getLeftChild() {
        return leftChild;
    }

    public treeNode getRightChild() {
        return rightChild;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setLeftChild(treeNode leftChild) {
        this.leftChild = leftChild;
    }

    public void setRightChild(treeNode rightChild) {
        this.rightChild = rightChild;
    }
}
