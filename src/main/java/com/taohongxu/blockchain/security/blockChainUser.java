package com.taohongxu.blockchain.security;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Document(collection = "blockChainUser")
@Data
public class blockChainUser{
    private static final long serialVersionUID = -4595387592069042041L;
    @Id //机构id
    private String username;
    private String password;
    private String principal;
    private String email;
    private String organizationName;
    private String role;

    public blockChainUser(){}

    public blockChainUser(String username,String password,String principal,String email,String organizationName,String role){
        this.username = username;
        this.password = password;
        this.role = role;
        this.principal = principal;
        this.organizationName = organizationName;
        this.email = email;
    }


    public String toString(){
        return "blockchainUser{" +
                "username="+username+
                "password="+password+
                "role="+role+
                "principal="+principal+
                "organizationName="+organizationName+
                "email="+email+"}";
    }

}
