package com.example.demo.controller;

import com.example.demo.entity.UserInfo;
import com.example.demo.result.Result;
import com.example.demo.result.ResultFactory;
import com.example.demo.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/")
public class LoginController {

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/login")
    public Result login(@RequestParam String key , @RequestBody UserInfo user, HttpSession session){
        int status = userInfoService.login(key,user);
        switch (status) {
            case 0:
                return ResultFactory.buildFailResult("用户名和密码不能为空");
            case 1:
                UserInfo userInfo = userInfoService.queryById(user.getAccount_id());
                session.setAttribute("user",userInfo);
                session.setMaxInactiveInterval(600);
                userInfoService.updateStatus("1",userInfo.getId());
                userInfo.setPassword(null);
                userInfo.setSalt(null);
                return ResultFactory.buildSuccessResult("登录成功",userInfo);
            case 2:
                return ResultFactory.buildFailResult("用户名不存在，请先注册");
            case 3:
                return ResultFactory.buildFailResult("用户名或密码错误");
            case 4:
                return ResultFactory.buildFailResult("验证码错误或验证码失效，请重新输入");
            case 5:
                return ResultFactory.buildFailResult("该用户已登录");
        }
        return ResultFactory.buildFailResult("未知错误");
    }

    @PostMapping("/register")
    public Result register(@RequestBody UserInfo user){
        int status =userInfoService.register(user);
        switch (status) {
            case 0:
                return ResultFactory.buildFailResult("用户名和密码不能为空");
            case 1:
                return ResultFactory.buildSuccessResult("注册成功","");
            case 2:
                return ResultFactory.buildFailResult("用户已存在");
            case 3:
                return ResultFactory.buildFailResult("该邮箱已注册账号");
            case 4:
                return ResultFactory.buildFailResult("验证码错误或验证码已过期");
        }
        return ResultFactory.buildFailResult("未知错误");
    }

    @PostMapping("/logout")
    public Result logout(@RequestBody UserInfo user,HttpSession session){
        if(session.getAttribute("user")==null){
            return ResultFactory.buildSuccessResult("您已自动退出登录");
        }
        UserInfo userinfo = (UserInfo) session.getAttribute("user");
        if(userinfo.getId() != user.getId()){
            return ResultFactory.buildFailResult("无此用户");
        }
//        session.removeAttribute("user");
        session.invalidate();
        return ResultFactory.buildSuccessResult("退出成功","");
    }

    @GetMapping("/auth")
    public Result auth(@RequestParam("id") Integer id,HttpSession session){
        if(session == null){
            return ResultFactory.buildFailResult("登录信息已过期，请重新登录");
        }
        UserInfo user = (UserInfo) session.getAttribute("user");
        if (user == null || user.getId() != id){
            return ResultFactory.buildFailResult("登录信息已过期，请重新登录");
        }
        return ResultFactory.buildSuccessResult("已登录",user);
    }
}
