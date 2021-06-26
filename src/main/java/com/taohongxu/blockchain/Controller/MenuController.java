package com.taohongxu.blockchain.Controller;

import com.alibaba.fastjson.JSON;
import com.taohongxu.blockchain.security.blockChainUser;
import com.taohongxu.blockchain.security.menu.Menu;
import com.taohongxu.blockchain.security.menu.MenuService;
import com.taohongxu.blockchain.security.menu.RoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {
    @Autowired
    MenuService service;

    @GetMapping("/getMenu/{user}")
    @ResponseBody
    public List<Menu> getMenu(@PathVariable("user") String role){
        return service.toMenu(role);
    }
}
