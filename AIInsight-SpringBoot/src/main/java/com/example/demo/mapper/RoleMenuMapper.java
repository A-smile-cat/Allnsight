package com.example.demo.mapper;

import com.example.demo.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Mapper
public interface RoleMenuMapper {
    List<RoleMenu> findAllByRid(int rid);
    int add(RoleMenu roleMenu);
    void deleteByRid(int rid);
}
