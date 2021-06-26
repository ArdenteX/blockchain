package com.taohongxu.blockchain.security.jwt;

import com.taohongxu.blockchain.security.blockChainUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtils {
    public static final String TOKEN_HEADER="Authorization";
    public static final String TOKEN_PREFIX="Bearer ";
    public static final String SUBJECT="congge";
    public static final long EXPIRITION = 1000*24*60*60*7;
    public static final String APPSECRET_KEY = "congge_secret";
    private static final String ROLE_CLAIMS = "rol";

    public static String GenerateJsonWebToken(blockChainUser user){
        if(user.getUsername() == null || user.getPassword() == null){
            return null;
        }
        Map<String,Object> map = new HashMap<>();
        map.put(ROLE_CLAIMS,"rol");
        String token = Jwts
                .builder()
                .setSubject(SUBJECT)
                .setClaims(map)
                .claim("username",user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRITION))
                .signWith(SignatureAlgorithm.HS256,APPSECRET_KEY).compact();    //订立
        return token;
    }

    public static String createToken(String username,String role){
        Map<String, Object> map = new HashMap<>();
        map.put(ROLE_CLAIMS,role);
        String token = Jwts
                .builder()
                .setSubject(username)
                .setClaims(map)
                .claim("username",username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRITION))
                .signWith(SignatureAlgorithm.HS256,APPSECRET_KEY).compact();    //订立
        return token;
    }

    public static Claims checkToken(String token){
        try{
            final Claims claims = Jwts.parser().setSigningKey(APPSECRET_KEY).parseClaimsJws(token).getBody();
            return claims;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getUsername(String token){
        Claims claims = Jwts.parser().setSigningKey(APPSECRET_KEY).parseClaimsJws(token).getBody();
        return claims.get("username").toString();
    }

    public static String getUserRole(String token){
        Claims claims = Jwts.parser().setSigningKey(APPSECRET_KEY).parseClaimsJws(token).getBody();
        return claims.get("rol").toString();
    }

    public static boolean isExpiration(String token){
        Claims claims = Jwts.parser().setSigningKey(APPSECRET_KEY).parseClaimsJws(token).getBody();
        return claims.getExpiration().before(new Date());
    }

}
