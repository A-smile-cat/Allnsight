package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import com.wf.captcha.utils.CaptchaUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.example.demo.service.RedisService;

@CrossOrigin
@RestController
@RequestMapping
public class CaptureController {
    @Autowired
    private RedisService redisService;

    @RequestMapping("/captcha")
    public void captcha(@RequestParam String key, HttpServletRequest request, HttpServletResponse response) throws Exception{
        //指定验证码的长宽及字符的个数
        SpecCaptcha captcha = new SpecCaptcha(65,33,4) ;
        //指定验证码的类型
        captcha.setCharType(Captcha.TYPE_DEFAULT);
        //首先应该把验证码在后台保存一份，但是不能保存在session中，可以存在Redis，也可以存在后台的某个Mapper里面
        System.out.println(captcha.text());
        redisService.set("key:code:"+key, captcha.text(),120);
        //输出验证码
        CaptchaUtil.out(captcha,request,response);
    }

}
