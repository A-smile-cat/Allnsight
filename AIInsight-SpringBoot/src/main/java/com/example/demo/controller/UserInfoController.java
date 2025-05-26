package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.example.demo.entity.UserInfo;
import com.example.demo.result.Result;
import com.example.demo.result.ResultFactory;
import com.example.demo.service.RedisService;
import com.example.demo.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/")
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RedisService redisService;
    @GetMapping("/searchAll")
    @ResponseBody
    public Result queryAll(@RequestParam("keyword") String key,HttpSession session){
        if(session == null || session.getAttribute("user") == null){
            return ResultFactory.buildFailResult("请输入查询内容");
        }
        UserInfo user = (UserInfo) session.getAttribute("user");
        if(user.getRole_id() != 1){
            return ResultFactory.buildFailResult("无权限");
        }
        if(key.isEmpty()){
            List<UserInfo> userInfoList = userInfoService.queryAll();
            return ResultFactory.buildSuccessResult("查询全部用户信息",JSON.toJSONString(userInfoList));
        }else {
            List<UserInfo> user1 = userInfoService.queryByUsername2(key);
            List<UserInfo> user2 = userInfoService.queryByAccountId(key);
            List<UserInfo> userInfoList = new ArrayList<UserInfo>();
            userInfoList.addAll(user1);
            userInfoList.addAll(user2);
            if(!userInfoList.isEmpty()){
                return ResultFactory.buildSuccessResult("查询成功",JSON.toJSONString(userInfoList));
            }else{
                return ResultFactory.buildFailResult("无查询结果");
            }

        }

    }

//    @GetMapping("/{id}")
//    @ResponseBody
//    public String query(@PathVariable(value = "id")Integer accountId){
//        UserInfo userInfo = userInfoService.queryById(accountId);
//        List<UserInfo> userInfoList = new ArrayList<UserInfo>();
//        userInfoList.add(userInfo);
//        return JSON.toJSONString(userInfoList);
//    }

    @PostMapping("/user")
    @ResponseBody
    public Result add(@RequestBody UserInfo userInfo,HttpSession session){
        if(session == null || session.getAttribute("user") == null){
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo user = (UserInfo) session.getAttribute("user");
        if(user.getRole_id() != 1){
            return ResultFactory.buildFailResult("无权限");
        }

        userInfoService.add(userInfo);
        return ResultFactory.buildSuccessResult("添加成功","");
    }

    @PutMapping("/user/{id}")
    @ResponseBody
    public Result update(@PathVariable(value = "id")Integer id,@RequestBody UserInfo userInfo,HttpSession session){
        if(session == null || session.getAttribute("user") == null){
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo user = (UserInfo) session.getAttribute("user");
        if(user.getRole_id() != 1){
            return ResultFactory.buildFailResult("无权限");
        }

        userInfo.setId(id);
        System.out.println(userInfo.getPassword());
        if(!userInfo.getPassword().isEmpty()) {
            userInfoService.updatePassword(userInfo,userInfo.getPassword());
        }
        System.out.println(userInfo.getPassword());
        userInfoService.updateUser(userInfo,userInfo.getUsername(),userInfo.getPhone(),userInfo.getEmail(),userInfo.getRole_id());
        return ResultFactory.buildSuccessResult("用户信息修改成功","");
    }

    @PutMapping("/stopUser")
    public Result updateUserStatus(@RequestBody Map<String, String> requestData, HttpSession session) {
        if (session == null || session.getAttribute("user")==null) {
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo user = (UserInfo) session.getAttribute("user");
        if(user.getRole_id() != 1){
            return ResultFactory.buildFailResult("无权限");
        }
        Integer uid = (Integer) Integer.parseInt(requestData.get("id")) ;
        if (uid == null) {
            return ResultFactory.buildFailResult("用户id不能为空");
        }
        userInfoService.updateStatus("2",uid);
        return ResultFactory.buildSuccessResult("用户已禁用成功");
    }

    @PostMapping("/updateUserInfo")
    @ResponseBody
    public Result updateUserInfo(@RequestBody UserInfo userInfo, HttpSession session){

        if(session == null || session.getAttribute("user") == null){
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo user = (UserInfo) session.getAttribute("user");
        if(user.getRole_id() != 1){
            return ResultFactory.buildFailResult("无权限");
        }

        if(userInfo.getGender()==null){
            return ResultFactory.buildFailResult("性别不能为空");
        }else if(userInfo.getUsername()==null){
            return ResultFactory.buildFailResult("用户名不能为空");
        }

        userInfoService.update(userInfo);
        return ResultFactory.buildSuccessResult("修改成功","");

    }

    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestBody UserInfo userInfo,HttpSession session){
        if(session == null || session.getAttribute("user") == null){
            return ResultFactory.buildFailResult("请先登录");
        }
        UserInfo user = (UserInfo) session.getAttribute("user");
        if(user.getId() != userInfo.getId()){
            return ResultFactory.buildFailResult("无权限");
        }

        if(userInfoService.loginVerify(user.getAccount_id(),userInfo.getOldPassword())){
            if(userInfo.getNewPassword().equals(userInfo.getConfirmPassword())){

                userInfoService.updatePassword(user,userInfo.getNewPassword());
                return ResultFactory.buildSuccessResult("密码修改成功","");
            }else{
                return ResultFactory.buildFailResult("两次密码不一致");
            }
        }else {
            return ResultFactory.buildFailResult("旧密码错误");
        }
    }

    @PostMapping("/resetPassword")
    public Result resetPassword(@RequestBody UserInfo userInfo){
        UserInfo user = userInfoService.queryByEmail(userInfo.getEmail());
        if(user == null){
            return ResultFactory.buildFailResult("请先注册");
        }
        String cacheCode = (String) redisService.get("email:code:" + user.getEmail());
        String code = userInfo.getEmailCode();
        System.out.println(userInfo.getNewPassword());
        System.out.println(cacheCode);
        System.out.println(code);
        if(cacheCode == null || !cacheCode.equals(code)) {
            return ResultFactory.buildFailResult("验证码错误");
        }
        if(!userInfo.getNewPassword().equals(userInfo.getConfirmPassword())){
            return ResultFactory.buildFailResult("两次输入的密码不一致");
        }
        userInfoService.updatePassword(user, userInfo.getNewPassword());
        return ResultFactory.buildSuccessResult("重置密码成功","");

    }
}
