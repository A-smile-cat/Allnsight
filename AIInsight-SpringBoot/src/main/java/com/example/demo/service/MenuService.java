package com.example.demo.service;

import com.example.demo.entity.Menus;
import com.example.demo.entity.RoleMenu;
import com.example.demo.entity.UserInfo;
import com.example.demo.mapper.MenuMapper;
import com.example.demo.mapper.RoleMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuService {

    @Autowired
    RoleMenuService roleMenuService ;

    @Autowired
    MenuMapper menuMapper ;

    public List<Menus> getAllByParentId(int parentId){
        return menuMapper.findAllByParentId(parentId);
    }

    public List<Menus> getMenusByCurrentUser(UserInfo user){
        int role_id = user.getRole_id();
        List<Integer> menuIds = new ArrayList<>();
        List<RoleMenu> roleMenus = roleMenuService.findAllByRid(role_id);
        for (RoleMenu roleMenu : roleMenus) {
            menuIds.add(roleMenu.getMid());
        }

        List<Menus> menus = new ArrayList<>();
        List<Menus> allMenus = menuMapper.findAllById(menuIds);

        // 使用 HashSet 去重
        Set<Menus> uniqueMenus = new HashSet<>(allMenus);
        menus.addAll(uniqueMenus);

        // Adjust the structure of the menu.
        handleMenus(menus,menuIds);
        return menus;
    }



    public void handleMenus(List<Menus> menus,List<Integer> menuIds){
        for (Menus m : menus) {
            List<Menus> children = getAllByParentId(m.getId());
            Iterator<Menus> iterator = children.iterator();
            while (iterator.hasNext()) {
                Menus c = iterator.next();
                if (!menuIds.contains(c.getId())) {
                    iterator.remove();
                }
            }
            m.setChildren(children);
        }

        Iterator<Menus> menuIterator = menus.iterator();
        while (menuIterator.hasNext()) {
            Menus m = menuIterator.next();
            if (m.getParent_id() != 0) {
                menuIterator.remove();
            }
        }

        System.out.println(menus);

//        for (Menus m : menus) {
//            List<Menus> children = getAllByParentId(m.getId());
//            for(Menus c : children){
//                if ( !menuIds.contains(c.getId())) {
//                    children.remove(c);
//                }
//            }
//            m.setChildren(children);
//        }
//        System.out.println(menus);
//        for(Menus m : menus){
//            if(m.getParent_id() != 0){
//                menus.remove(m);
//            }
//        }
////        menus.removeIf(m -> m.getParent_id() != 0);
//        System.out.println(menus);
    }

    private List<Menus> getById(Integer id) {
       List<Menus> menus = menuMapper.findAllByParentId(id);
       return menus;
    }


    public List<Menus> getAllMenus() {
        return menuMapper.getAllMenus();
    }
}
