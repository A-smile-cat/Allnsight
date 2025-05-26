package com.example.demo.mapper;

import com.example.demo.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Repository
@Mapper
public interface UserInfoMapper {
    void add(UserInfo userInfo);
    void delete(Integer id);
    void update(UserInfo userInfo);
    void updateAccountId(UserInfo userInfo);
    void updateUser(UserInfo userInfo);
    void updateStatus(UserInfo userInfo);
    void updatePassword(UserInfo userInfo);
    UserInfo queryById(String account_id);
    List<UserInfo> queryByAccountId(String account_id);
    List<UserInfo> queryAll();
    UserInfo loginVerify(String account_id,String password);

    UserInfo queryByUserName(String s);

    List<UserInfo> queryByUsername2(String username);

    UserInfo queryByEmail(String s);

    void updateStatus(@Param("status") String status, @Param("id") Integer id);

    int statusVerify(Integer id);

    UserInfo queryById2(Integer id);
}
