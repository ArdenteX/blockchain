package com.taohongxu.blockchain.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taohongxu.blockchain.security.CustomAuthenticationFilter;
import com.taohongxu.blockchain.security.blockchainUserDetailService;
import com.taohongxu.blockchain.security.jwt.JWTAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class securityConfig  extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public blockchainUserDetailService userDetailService(){
        return new blockchainUserDetailService();
    }


    @Override
    protected  void configure(AuthenticationManagerBuilder auth) throws Exception{
        //??????????????????
        auth
                .userDetailsService(userDetailService())
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.cors().and().csrf().disable()
                .authorizeRequests()
                    .antMatchers("/menu/**").authenticated()
                    .antMatchers("/api/auth/**").authenticated()
                    .antMatchers("/api/file/**").access("hasAnyRole('SCHOOL','ADMIN')")
                    .antMatchers("/api/blockChainUsers/**").hasRole("ADMIN")
                    .antMatchers("/api/schools/**").access("hasAnyRole('SCHOOL','ADMIN')")
                    .antMatchers("/api/companies/**").access("hasAnyRole('COMPANY','ADMIN')")
                    .anyRequest()
                    .permitAll()
                    .and()
                    .addFilter(new CustomAuthenticationFilter(authenticationManager()))
                    .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        response.setContentType("application/json;charset=utf-8");
                        PrintWriter out = response.getWriter();
                        Map<String,Object> map = new HashMap<>();
                        map.put("statue",401);
                        if (authException instanceof InsufficientAuthenticationException) {
                            map.put("msg","?????????????????????????????????!");
                        }
                        out.write(new ObjectMapper().writeValueAsString(map));
                        out.flush();
                        out.close();
                    }
                });

    }

//    @Bean
//    CustomAuthenticationFilter customAuthenticationFilter() throws Exception{
//        CustomAuthenticationFilter filter = new CustomAuthenticationFilter(authenticationManager());
//        filter.setAuthenticationManager(super.authenticationManagerBean());
//        filter.setFilterProcessesUrl("/jsonLogin");
//        filter.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
//            @Override
//            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//                Object principal = authentication.getPrincipal();
//                response.setContentType("application/json;charset=utf-8");
//                PrintWriter writer = response.getWriter();
//                response.setStatus(200);
//                Map<String,Object> map = new HashMap<>();
//                map.put("statue",200);
//                map.put("msg",principal);
//                ObjectMapper objectMapper = new ObjectMapper();
//                writer.write(objectMapper.writeValueAsString(map));
//                writer.flush();
//                writer.close();
//            }
//        });
//        filter.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
//            @Override
//            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
//                response.setContentType("application/json;charset=utf-8");
//                PrintWriter writer = response.getWriter();
//                response.setStatus(401);
//                Map<String,Object> map = new HashMap<>();
//                map.put("status",401);
//                if(exception instanceof LockedException){
//                    map.put("msg","?????????????????????????????????");
//                }
//                else if(exception instanceof BadCredentialsException){
//                    map.put("msg","?????????????????????????????????????????????");
//                }
//                else if(exception instanceof DisabledException){
//                    map.put("msg","?????????????????????????????????");
//                }
//                else if(exception instanceof AccountExpiredException){
//                    map.put("msg","??????????????????????????????");
//                }
//                else if(exception instanceof CredentialsExpiredException){
//                    map.put("msg","??????????????????????????????");
//                }
//                else {
//                    map.put("msg","????????????");
//                }
//                ObjectMapper objectMapper = new ObjectMapper();
//                writer.write(objectMapper.writeValueAsString(map));
//                writer.flush();
//                writer.close();
//            }
//
//        });
//        return filter;
//    }
}


/*
* {
                        @Override
                        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                            response.setContentType("application/json;charset=utf-8");
                            PrintWriter out = response.getWriter();
                            Map<String,Object> map = new HashMap<>();
                            map.put("statue",401);
                            if (authException instanceof InsufficientAuthenticationException) {
                                map.put("msg","?????????????????????????????????!");
                            }
                            out.write(new ObjectMapper().writeValueAsString(map));
                            out.flush();
                            out.close();
                        }
                    });*/
