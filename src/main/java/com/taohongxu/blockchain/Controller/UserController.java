package com.taohongxu.blockchain.Controller;


import com.taohongxu.blockchain.Config.securityConfig;
import com.taohongxu.blockchain.Entity.DAO.blockchainUserDAO;
import com.taohongxu.blockchain.Entity.JsonChange;
import com.taohongxu.blockchain.security.UserForm;
import com.taohongxu.blockchain.security.blockChainUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final blockchainUserDAO blockchainUserDAO;

    @Autowired
    public UserController(blockchainUserDAO blockchainUserDAO){
        this.blockchainUserDAO = blockchainUserDAO;
    }
    @Autowired
    securityConfig securityConfig;


    @PatchMapping("/change")
    @ResponseBody
    public String change(@RequestBody blockChainUser companyUser){


        try{
            blockChainUser user = blockchainUserDAO.findByUsername(companyUser.getUsername());
            if(!companyUser.getPassword().equals("")){
                user.setPassword(securityConfig.passwordEncoder().encode(companyUser.getPassword()));
            }
            if(!companyUser.getEmail().equals("")){
                user.setEmail(companyUser.getEmail());
            }
            if(!companyUser.getOrganizationName().equals("")){
                user.setOrganizationName(companyUser.getOrganizationName());
            }
            if(!companyUser.getPrincipal().equals("")){
                user.setPrincipal(companyUser.getPrincipal());
            }
            blockchainUserDAO.save(user);
        }catch (NullPointerException e){
            return "nullError";
        }

        return "修改成功！";
    }

    @GetMapping("/getMsg/{username}")
    @ResponseBody
    public JsonChange getMsg(@PathVariable("username") String username){
        JsonChange json = new JsonChange();

        blockChainUser user = blockchainUserDAO.findByUsername(username);
        json.setItem(user.getOrganizationName());
        json.setItem(user.getEmail());
        return json;
    }

}
