package com.example.demo.controller;


import com.alibaba.fastjson.JSON;
import com.example.demo.entity.RoleMenu;
import com.example.demo.entity.Roles;
import com.example.demo.entity.UserInfo;
import com.example.demo.result.Result;
import com.example.demo.result.ResultFactory;
import com.example.demo.service.RoleMenuService;
import com.example.demo.service.RolesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/")
public class RolesController {

    @Autowired
    private RolesService roleService;
    @Autowired
    private RoleMenuService roleMenuService;

    @GetMapping("/getRoles")
    private Result getAllRoles(HttpSession session){
        if(session == null || session.getAttribute("user") == null){
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo user = (UserInfo) session.getAttribute("user");
        if(user.getRole_id() != 1){
            return ResultFactory.buildFailResult("无权限");
        }
        List<Roles> rolesList  = roleService.queryAll();
        return ResultFactory.buildSuccessResult("查询成功", JSON.toJSONString(rolesList));
    }

    @GetMapping("/getRoleList")
    private Result getRoleList(HttpSession session){
        if(session == null || session.getAttribute("user") == null){
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo user = (UserInfo) session.getAttribute("user");
        if(user.getRole_id() != 1){
            return ResultFactory.buildFailResult("无权限");
        }
        List<Roles> rolesList  = roleService.queryAll();
        return ResultFactory.buildSuccessResult(JSON.toJSONString(rolesList));
    }

    @GetMapping("/getRoleMenu")
    private Result getRoleMenu(@RequestParam("role_id") int roleId,HttpSession session){
        if(session == null || session.getAttribute("user") == null){
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo user = (UserInfo) session.getAttribute("user");
        if(user.getRole_id() != 1){
            return ResultFactory.buildFailResult("无权限");
        }
        List<RoleMenu> roleMenuList  = roleMenuService.findAllByRid(roleId);
        int[] menu = new int[roleMenuList.size()];
        int i = 0;
        for (RoleMenu roleMenu : roleMenuList){
            menu[i] = roleMenu.getMid();
            i++;
        }
        return ResultFactory.buildSuccessResult(JSON.toJSONString(menu));
    }



}
