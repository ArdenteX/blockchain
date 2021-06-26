package com.taohongxu.blockchain.socket.peerTopeer.tools;

import com.taohongxu.blockchain.Entity.treeNode;
import java.util.*;


public class merkleTree {
    //merkleTree树
    private List<treeNode> Tree;
    //根节点
    private treeNode root;

    public merkleTree(List<String> data){
        if(data == null || data.size() == 0){
            return;
        }

        Tree = new ArrayList<>();

        //创建叶子结点
        List<treeNode> leafList =  createLeafList(data);
        Tree.addAll(leafList);

        //创建父结点
        List<treeNode> parentList = createParentList(leafList);
        Tree.addAll(parentList);

        while(parentList.size() > 1){
            List<treeNode> temp = createParentList(parentList);
            Tree.addAll(temp);
            parentList = temp;
        }

        root = Tree.get(0);
    }

    private List<treeNode> createLeafList(List<String> data){
        List<treeNode> leafList = new ArrayList<>();

        if(data==null || data.size() == 0){
            return leafList;
        }

        for(String data1 : data){
            treeNode treeNode = new treeNode(data1);
            leafList.add(treeNode);
        }


        return leafList;
    }

    private treeNode createParentNode(treeNode left,treeNode right){
        treeNode treeNode = new treeNode();
        treeNode.setLeftChild(left);
        treeNode.setRightChild(right);

        //如果右结点无就用左节点的hash作为新节点的hash
        String hash = left.getHash();
        if(right != null){
            hash  = SHAEncryption.SHAByHutool(left.getHash() + right.getHash());
        }
        //data 和 hash 同值
        treeNode.setHash(hash);
        treeNode.setData(hash);

        if(right != null){
            treeNode.setName(left.getName() + "和" + right.getName()+"的父结点");
        }
        else{
            treeNode.setName(left.getName()+"的父结点");
        }

        return treeNode;
    }

    private List<treeNode> createParentList(List<treeNode> leafList){
        List<treeNode> parent = new ArrayList<>();

        if(leafList == null || leafList.size() == 0){
            return parent;
        }
        int length = leafList.size();
        for(int i = 1;i < length;i+=2){
            treeNode treeNode = createParentNode(leafList.get(i-1),leafList.get(i));
            parent.add(treeNode);
        }
        if(length % 2 != 0){
            treeNode treeNode = createParentNode(leafList.get(length-1),null);
            parent.add(treeNode);
        }

        return  parent;
    }

    //遍历树
    public void traverseTreeNode(){
        Collections.reverse(Tree);
        treeNode root = Tree.get(0);
        traverseTreeNode(root);
    }

    public String getTreeNodeHash(){
        Collections.reverse(Tree);
        return Tree.get(0).getHash();
    }

    public void traverseTreeNode(treeNode treeNode){
        System.out.println(treeNode.getName());
        if(treeNode.getLeftChild() != null){
            traverseTreeNode(treeNode.getLeftChild());
        }
        if(treeNode.getRightChild() != null){
            traverseTreeNode(treeNode.getRightChild());
        }
    }

    public List<treeNode> getTree() {
        return Tree;
    }

    public treeNode getRoot() {
        return root;
    }

    public void setRoot(treeNode root) {
        this.root = root;
    }

    public void setTree(List<treeNode> tree) {
        Tree = tree;
    }
}
