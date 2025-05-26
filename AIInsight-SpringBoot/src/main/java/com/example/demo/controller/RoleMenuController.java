package com.example.demo.controller;

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

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/")
public class RoleMenuController {

    @Autowired
    private RoleMenuService roleMenuService;
    @Autowired
    private RolesService rolesService;

    @PostMapping("/addRoleMenu")
    public Result addRoleMenu(@RequestBody RoleMenu roleMenu, HttpSession session){
        if(session == null || session.getAttribute("user") == null){
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo user = (UserInfo) session.getAttribute("user");
        if(user.getRole_id() != 1){
            return ResultFactory.buildFailResult("无权限");
        }

        Roles roles = new Roles();

        roles.setRole_name(roleMenu.getRole_name());
        roles.setDescription(roleMenu.getDescription());

        Roles roles1 = rolesService.add(roles);

        int f2 = roleMenuService.add(roleMenu.getMenus(),roles1.getRole_id());

        if(roleMenu.getMenus().length == f2 ){
            return ResultFactory.buildSuccessResult("角色添加成功");
        }else{
            return ResultFactory.buildFailResult("角色添加失败");
        }
    }

    @PutMapping("/updateRoleMenu")
    public Result updateRoleMenu(@RequestBody RoleMenu roleMenu,HttpSession session){
        if(session == null || session.getAttribute("user") == null){
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo user = (UserInfo) session.getAttribute("user");
        if(user.getRole_id() != 1){
            return ResultFactory.buildFailResult("无权限");
        }

        Roles roles = new Roles();
        roles.setRole_name(roleMenu.getRole_name());
        roles.setDescription(roleMenu.getDescription());
        roles.setRole_id(roleMenu.getRole_id());
        rolesService.update(roles);
        int f = roleMenuService.update(roleMenu.getMenus(),roleMenu.getRole_id());
        if(f == roleMenu.getMenus().length){
            return ResultFactory.buildSuccessResult("角色修改成功");
        }else{
            return ResultFactory.buildFailResult("角色修改失败");
        }
    }

    @DeleteMapping("/deleteRoleMenu/{id}")
    public Result deleteRoleMenu(@PathVariable("id") int id,HttpSession session){
        if(session == null || session.getAttribute("user") == null){
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo user = (UserInfo) session.getAttribute("user");
        if(user.getRole_id() != 1){
            return ResultFactory.buildFailResult("无权限");
        }

        roleMenuService.delete(id);
        rolesService.delete(id);
        return ResultFactory.buildSuccessResult("删除成功");
    }
}
