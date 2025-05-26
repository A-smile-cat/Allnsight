package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.example.demo.entity.Menus;
import com.example.demo.entity.UserInfo;
import com.example.demo.result.Result;
import com.example.demo.result.ResultFactory;
import com.example.demo.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/")
public class MenuController {

    @Autowired
    MenuService menuService;

    @PostMapping("/admin/menu")
    private Result menu(@RequestBody UserInfo userinfo,HttpSession session) {
        UserInfo user = (UserInfo) session.getAttribute("user");
        if (user == null) {
            return ResultFactory.buildFailResult("未登录");
        }
        if (user.getId() != userinfo.getId()) {
            return ResultFactory.buildFailResult("无权限");
        }
        return ResultFactory.buildSuccessResult(menuService.getMenusByCurrentUser(user));
    }

    @GetMapping("/getAllMenus")
    private Result getAllMenus(HttpSession session){
        if(session.getAttribute("user") == null){
            return ResultFactory.buildFailResult("未登录");
        }
        List<Menus> menusList = menuService.getAllMenus();
        return ResultFactory.buildSuccessResult(JSON.toJSONString(menusList));
    }

}
