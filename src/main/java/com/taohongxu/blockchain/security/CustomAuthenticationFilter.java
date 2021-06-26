package com.taohongxu.blockchain.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taohongxu.blockchain.security.jwt.JWTUser;
import com.taohongxu.blockchain.security.jwt.JWTUtils;
import com.taohongxu.blockchain.security.menu.MenuService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
        this.setFilterProcessesUrl("/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
            ObjectMapper objectMapper = new ObjectMapper();
            String username = null;
            String password = null;
            try{
                Map<String,String> map = objectMapper.readValue(request.getInputStream(),Map.class);
                username = map.get("username");
                password = map.get("password");
            }catch (Exception e){
                e.printStackTrace();
            }
            if(username == null){
                username = "";
            }

            if(password == null){
                password = "";
            }

            username = username.trim();
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username,password);
            setDetails(request,authRequest);
            return authenticationManager.authenticate(authRequest);

    }
    @Override
    protected void  successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,Authentication authResult) throws IOException {
        JWTUser jwtUser = (JWTUser) authResult.getPrincipal();
        String role = "";
        Collection<? extends GrantedAuthority> authorities = jwtUser.getAuthorities();
        for(GrantedAuthority authority : authorities){
            role = authority.getAuthority();
        }
        String token = JWTUtils.createToken(jwtUser.getUsername(),role);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        String tokenStr = JWTUtils.TOKEN_PREFIX + token;
        response.setHeader("Access-Control-Expose-Headers","token");
        response.setHeader("token",tokenStr);
        response.setStatus(200);
        Map<String,Object> map = new HashMap<>();
        map.put("status",200);
        map.put("msg",jwtUser);
        //map.put("menu", MenuService.toMenu(role));
        ObjectMapper objectMapper = new ObjectMapper();
        writer.write(objectMapper.writeValueAsString(map));
        writer.flush();
        writer.close();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException{
        response.getWriter().write("authentication failed, reason: "+failed.getMessage());
    }
}

