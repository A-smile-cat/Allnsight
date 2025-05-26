package com.example.demo.entity;

import com.alibaba.fastjson.JSON;
import com.vladmihalcea.hibernate.type.json.internal.JsonTypeDescriptor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Transient;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private Integer id;
    private String account_id;
    private String username;
    private String password;
    private String salt;
    private String phone;
    private String email;
    private String region;
    private String address;
    private String gender;
    private Date birthday;
    private int role_id;
    private String status;
    @Transient
    private String EmailCode;
    @Transient
    private String captcha;

    @Transient
    private String oldPassword;
    @Transient
    private String newPassword;
    @Transient
    private String confirmPassword;
}
