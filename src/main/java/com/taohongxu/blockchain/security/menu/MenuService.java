package com.taohongxu.blockchain.security.menu;

import com.taohongxu.blockchain.Entity.DAO.RoleMenuDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {
    @Autowired
    RoleMenuDAO dao;

    public List<Menu> toMenu(String role) {
        if (role.equals("ROLE_SCHOOL") || role.equals("ROLE_COMPANY") || role.equals("ROLE_ADMIN") ) {
            return dao.findByRole(role).getMenus();
        }
        return null;
    }
}
