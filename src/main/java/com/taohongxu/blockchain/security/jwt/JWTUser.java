package com.taohongxu.blockchain.security.jwt;

import com.taohongxu.blockchain.security.blockChainUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class JWTUser implements UserDetails {

    private static final long serialVersionUID = -5078261343220319556L;
    private String username;
    private String password;
    private String principal;
    private String email;
    private String organizationName;
    private Collection<? extends GrantedAuthority> authorities;

    public JWTUser(){}
    public JWTUser(blockChainUser user){
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.principal = user.getPrincipal();
        this.email = user.getEmail();
        this.organizationName = user.getOrganizationName();
        this.authorities = Collections.singleton(new SimpleGrantedAuthority(user.getRole()));
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString(){
        return "JwtUser{" +
                "username="+username+
                "password="+password+
                "authorities="+authorities+
                "principal="+principal+
                "organizationName="+organizationName+
                "email="+email+"}";
    }
}
