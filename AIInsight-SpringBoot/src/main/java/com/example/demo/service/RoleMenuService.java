package com.example.demo.service;

import com.example.demo.entity.RoleMenu;
import com.example.demo.mapper.RoleMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleMenuService {

    @Autowired
    private RoleMenuMapper roleMenuMapper;
    public List<RoleMenu> findAllByRid(int rid) {
        return roleMenuMapper.findAllByRid(rid);
    }

    public int add(int mid,int rid) {
        RoleMenu roleMenu = new RoleMenu();
        roleMenu.setMid(mid);
        roleMenu.setRid(rid);
        return roleMenuMapper.add(roleMenu);
    }

    public int add(int[] menus,int rid){
        int i = 0;
        for (int mid : menus){
            add(mid,rid);
            i++;
        }
        return i;
    }

    public void delete(int rid) {
         roleMenuMapper.deleteByRid(rid);
    }

    public int update(int[] menus,int rid){
        roleMenuMapper.deleteByRid(rid);
        int i=0;
        for (int mid : menus){
            add(mid,rid);
            i++;
        }
        return i;
    }
}
