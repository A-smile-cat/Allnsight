package com.example.demo.service;

import com.example.demo.mapper.UserInfoMapper;
import com.example.demo.entity.UserInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.tomcat.jni.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import com.example.demo.service.RedisService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.swing.*;
import java.util.List;
import java.util.Locale;

@Service
public class UserInfoService {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private RedisService redisService;

    public void add(UserInfo userInfo) {
        String password = userInfo.getPassword();
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int times = 2;
        String encodedPassword = new SimpleHash("md5", password, salt, times).toString();

        userInfo.setSalt(salt);
        userInfo.setPassword(encodedPassword);
        userInfo.setStatus("0");
        userInfoMapper.add(userInfo);
        //System.out.println("生成的 id 值：" + userInfo.getId());
        userInfoMapper.updateAccountId(userInfo);
    }

    public void delete(Integer id) {
        userInfoMapper.delete(id);
    }

    public void updateUser(UserInfo userInfo,String username,String phone,String email,int role_id) {
        userInfo.setUsername(username);
        userInfo.setPhone(phone);
        userInfo.setEmail(email);
        userInfo.setRole_id(role_id);
        System.out.println(userInfo.getPassword());
        userInfoMapper.updateUser(userInfo);
    }

    public void updateStatus(String status, Integer id) {
        userInfoMapper.updateStatus(status,id);
    }

    public UserInfo queryById(String account_id) {
        UserInfo user = null;
        UserInfo tema = userInfoMapper.queryById(account_id);
        UserInfo temb = userInfoMapper.queryByEmail(account_id);
        if(tema!=null){
            user = tema;
        }
        if(temb!=null){
            user = temb;
        }
        return user;
    }


    public boolean loginVerify(String account_id,String password){
        UserInfo user = userInfoMapper.loginVerify(account_id,password);
        if(user==null){
            return false;
        }
        else if(!user.getPassword().equals(new SimpleHash("md5", password, user.getSalt(), 2).toString())){
            return false;
        }else{
            return true;
        }
    }

    public int statusVerify(Integer id){
        return userInfoMapper.statusVerify(id);
    }

    public UserInfo queryByUsername(String s) {
        return userInfoMapper.queryByUserName(s);
    }

    public UserInfo queryByEmail(String s) {
        return userInfoMapper.queryByEmail(s);
    }

    public List<UserInfo> queryAll() {
        return userInfoMapper.queryAll();
    }

    public boolean isExist(String s){
        return queryByUsername(s) != null || queryByEmail(s) != null;
    }

    public int sendEmail(UserInfo user){
        String username = user.getUsername();
        String email = user.getEmail();
        
        boolean existUserName = isExist(username);
        boolean existEmail = isExist(email);
        if (existUserName) {
            return 2;
        }
        if (existEmail){
            return 3;
        }
        return 1;
    }

    public int register(UserInfo user){
        String username = user.getUsername();
        String email = user.getEmail();
        String password = user.getPassword();
        String code = user.getEmailCode();

        String cacheCode = (String) redisService.get("email:code:" + user.getEmail());
        if(cacheCode == null || !cacheCode.equals(code)) {
            return 4;
        }
        
        // 验证通过后删除验证码
        redisService.delete("email:code:" + user.getEmail());

        //转义处理，防止XSS攻击
        username = HtmlUtils.htmlEscape(username);
        user.setUsername(username);

        email = HtmlUtils.htmlEscape(email);
        user.setEmail(email);

        if (username.equals("") || password.equals("")) {
            return 0;
        }

        boolean existUserName = isExist(username);
        boolean existEmail = isExist(email);
        if (existUserName) {
            return 2;
        }
        if (existEmail){
            return 3;
        }

        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int times = 2;
        String encodedPassword = new SimpleHash("md5", password, salt, times).toString();

        user.setSalt(salt);
        user.setPassword(encodedPassword);

        add(user);

        return 1;
    }

    public int login(String key,UserInfo user){
        String aid= user.getAccount_id();
        String password = user.getPassword();
        String code = user.getCaptcha();

        String cacheCode = (String) redisService.get("key:code:" + key);

        if(cacheCode == null || !cacheCode.toUpperCase().equals(code)) {
            return 4;
        }
        
        // 验证通过后删除验证码
        redisService.delete("key:code:" + key);

        //转义处理，防止XSS攻击
        aid = HtmlUtils.htmlEscape(aid);
        password = HtmlUtils.htmlEscape(password);


        if (aid.equals("") || password.equals("")) {
            return 0;
        }else if(queryById(aid)==null){
            return 2;
        }else if(!loginVerify(aid,password)){
            return 3;
        }else if(statusVerify(queryById(aid).getId())==1){
            return 5;
        }

        return 1;
    }

    public List<UserInfo> queryByAccountId(String key) {
        return userInfoMapper.queryByAccountId(key);
    }

    public List<UserInfo> queryByUsername2(String key) {
        return userInfoMapper.queryByUsername2(key);
    }

    public UserInfo queryById2(Integer id) {
        return userInfoMapper.queryById2(id);
    }

    public void updatePassword(UserInfo userInfo,String password) {
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int times = 2;
        String encodedPassword = new SimpleHash("md5", password, salt, times).toString();

        userInfo.setSalt(salt);
        userInfo.setPassword(encodedPassword);
        userInfoMapper.updatePassword(userInfo);
    }

    public void update(UserInfo userInfo) {
        userInfoMapper.update(userInfo);
    }

    public void updateStatus(UserInfo userInfo,String status) {
        userInfo.setStatus(status);
        userInfoMapper.updateStatus(userInfo);
    }
}
