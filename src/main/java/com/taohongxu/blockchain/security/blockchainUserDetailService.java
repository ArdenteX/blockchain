package com.taohongxu.blockchain.security;

import com.taohongxu.blockchain.Entity.DAO.blockchainUserDAO;
import com.taohongxu.blockchain.security.jwt.JWTUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class blockchainUserDetailService implements UserDetailsService {
    @Autowired
    blockchainUserDAO blockchainUserDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        blockChainUser bcu = blockchainUserDAO.findByUsername(username);
        if(bcu == null){
            throw new UsernameNotFoundException("blockChainUser" + username + "Not Found!");
        }
        return new JWTUser(bcu);
    }
}
