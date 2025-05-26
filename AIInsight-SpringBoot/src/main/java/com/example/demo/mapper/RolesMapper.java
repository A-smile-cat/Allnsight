package com.example.demo.mapper;

import com.example.demo.entity.Roles;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface RolesMapper {

    List<Roles> queryAll();

    @Options(useGeneratedKeys = true, keyProperty = "id")
    int add(Roles role);

    int update(Roles roles);

    int delete(int id);
}
