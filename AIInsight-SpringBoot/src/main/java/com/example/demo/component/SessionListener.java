package com.example.demo.component;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.example.demo.entity.UserInfo;
import com.example.demo.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Session监听器 - 监听Session的创建与销毁事件
 */
@Component // 注册为Spring组件
public class SessionListener implements HttpSessionListener {

    @Autowired
    private UserInfoService userInfoService;
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        // Session创建时触发
//        System.out.println("Session创建: " + se.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        // Session销毁时触发（过期或主动失效）
//        System.out.println("Session销毁: " + se.getSession().getId());

        // 从Session中获取用户ID
        UserInfo user = (UserInfo) se.getSession().getAttribute("user");
        if (user != null) {
            int userId = user.getId();
            UserInfo userInfo = userInfoService.queryById(user.getAccount_id());
            userInfoService.updateStatus("0",userId);
            // 调用Service更新数据库中用户的登录状态
            // userService.logout(userId); // 实际项目中调用服务层方法
//            System.out.println("用户 " + userId + " 已自动退出登录");
        }
    }
}
