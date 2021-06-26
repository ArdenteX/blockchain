package com.taohongxu.blockchain.security;

import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Data
public class UserForm {
    private String username;
    private String password;
    private String principal;
    private String email;
    private String organizationName;
    private String role;

    public blockChainUser toUser(PasswordEncoder passwordEncoder){
        return new blockChainUser(username, passwordEncoder.encode(password),principal,email,organizationName,role);
    }
}
