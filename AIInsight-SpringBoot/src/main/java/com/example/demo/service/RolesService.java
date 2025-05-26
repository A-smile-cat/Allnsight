package com.example.demo.service;

import com.example.demo.entity.Roles;
import com.example.demo.mapper.RolesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolesService {

    @Autowired
    private RolesMapper rolesMapper;

    public List<Roles> queryAll() {
        return rolesMapper.queryAll();
    }

    public Roles add(Roles roles) {
        rolesMapper.add(roles);
        return roles;
    }

    public void update(Roles roles) {
        rolesMapper.update(roles);
    }

    public void delete(int id) {
        rolesMapper.delete(id);
    }
}
