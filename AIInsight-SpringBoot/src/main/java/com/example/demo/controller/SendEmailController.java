package com.example.demo.controller;


import com.example.demo.result.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import com.example.demo.result.ResultFactory;
import com.example.demo.service.RedisService;
import com.example.demo.entity.*;
import com.example.demo.service.*;
import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/")
public class SendEmailController {

    @Autowired
    private UserInfoService userInfoService;

    @Resource
    private JavaMailSender javaMailSender;

    @Autowired
    private RedisService redisService;

    //读取yml文件中username的值并赋值给form
    @Value("${spring.mail.username}")
    private String from;

    @RequestMapping("/sendEmail1")
    public Result sendSimpleMail1(@RequestBody UserInfo user) {

        String emailReceiver = user.getEmail();

        int status = userInfoService.sendEmail(user);

        if(status == 1){
            // 构建一个邮件对象
            SimpleMailMessage message = new SimpleMailMessage();
            // 设置邮件发送者
            message.setFrom(from);
            // 设置邮件接收者
            message.setTo(emailReceiver);
            // 设置邮件的主题
            message.setSubject("系统验证码");
            // 设置邮件的正文
            //code自行设置，不做实现
            String code = String.format("%06d", (int)(Math.random() * 1000000));
            // 将验证码存入Redis，有效期5分钟
            redisService.set("email:code:"+emailReceiver, code,300);
            String text = "您的验证码为：" + code + ",请勿泄露给他人。";
            message.setText(text);
            // 发送邮件
            try {
                javaMailSender.send(message);
                return ResultFactory.buildSuccessResult("","");
            } catch (MailException e) {
                e.printStackTrace();
            }
            return ResultFactory.buildFailResult("验证码发送失败，请稍后重试");
        }else if(status == 2){
            return ResultFactory.buildFailResult("该用户名已存在，请更换用户名");
        }else if(status == 3){
            return ResultFactory.buildFailResult("该邮箱已注册账号，请更换邮箱或找回密码");
        }else{
            return ResultFactory.buildFailResult("未知错误");
        }

    }

    @RequestMapping("/sendEmail2")
    public Result sendSimpleMail2(@RequestBody UserInfo user) {

        String emailReceiver = user.getEmail();

        int status = userInfoService.sendEmail(user);

        if(status == 2 || status == 3){
            // 构建一个邮件对象
            SimpleMailMessage message = new SimpleMailMessage();
            // 设置邮件发送者
            message.setFrom(from);
            // 设置邮件接收者
            message.setTo(emailReceiver);
            // 设置邮件的主题
            message.setSubject("系统验证码");
            // 设置邮件的正文
            //code自行设置，不做实现
            String code = String.format("%06d", (int)(Math.random() * 1000000));
            // 将验证码存入Redis，有效期5分钟
            redisService.set("email:code:"+emailReceiver, code,300);
            String text = "您的验证码为：" + code + ",请勿泄露给他人。";
            message.setText(text);
            // 发送邮件
            try {
                javaMailSender.send(message);
                return ResultFactory.buildSuccessResult("","");
            } catch (MailException e) {
                e.printStackTrace();
            }
            return ResultFactory.buildFailResult("验证码发送失败，请稍后重试");
        }else if(status == 1){
            return ResultFactory.buildFailResult("该邮箱还未注册！");
        }
        else{
            return ResultFactory.buildFailResult("未知错误");
        }

    }
}

