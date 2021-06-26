package com.taohongxu.blockchain.service;

import cn.hutool.Hutool;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;

import java.util.Arrays;


@Service
public class RSAService {
    private RSA rsa;

    //使用公钥加密
    public byte[] encode(String data,String publicKey){
        rsa = new RSA(null,publicKey);
        return rsa.encrypt(StrUtil.bytes(data,CharsetUtil.UTF_8) ,KeyType.PublicKey);
    }

    //私钥解密
    public byte[] decode(byte[] encrypt,String privateKey){
        rsa = new RSA(privateKey,null);
        return rsa.decrypt(encrypt,KeyType.PrivateKey);
    }

    //已有私钥和密文,解密
    public byte[] decode(String privateKey, String data){
        rsa = new RSA(privateKey,null);
        byte[] bytes = HexUtil.decodeHex(data);
        return rsa.decrypt(bytes,KeyType.PrivateKey);
    }

    //私钥加密
    public byte[] encodePrivate(String data,String privateKey){
        rsa = new RSA(privateKey,null);
        return rsa.encrypt(StrUtil.bytes(data,CharsetUtil.UTF_8),KeyType.PrivateKey);
    }

    //公钥解密
    public byte[] decodePublic(byte[] encodePrivate,String publicKey){
        rsa = new RSA(null,publicKey);
        return rsa.decrypt(encodePrivate,KeyType.PublicKey);
    }

    public static void main(String[] args){
        RSA rsa = new RSA();
        String pub = rsa.getPublicKeyBase64();
        String privateKey = rsa.getPrivateKeyBase64();
        RSAService rs= new RSAService();

        System.out.println(pub);
        System.out.println(privateKey);

    }

}
