package com.example.demo.mapper;

import com.example.demo.entity.Menus;
import com.example.demo.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Mapper
public interface MenuMapper {

    List<Menus> findAllById(List<Integer> menuIds);

    List<Menus> findAllByParentId(int parentId);

    List<Menus> getAllMenus();
}
