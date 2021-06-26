package com.taohongxu.blockchain.security.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String tokenHeader = request.getHeader(JWTUtils.TOKEN_HEADER);
        if(tokenHeader == null || !tokenHeader.startsWith(JWTUtils.TOKEN_PREFIX)){
            chain.doFilter(request,response);
        }

        SecurityContextHolder.getContext().setAuthentication(getAuthentication(tokenHeader));
        super.doFilter(request,response,chain);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String tokenHeader){
        String token = tokenHeader.replace(JWTUtils.TOKEN_PREFIX,"");
        String username = JWTUtils.getUsername(token);
        String role = JWTUtils.getUserRole(token);
        if(username != null){
            return new UsernamePasswordAuthenticationToken(username,null, Collections.singleton(new SimpleGrantedAuthority(role)));
        }
        return null;
    }
}
