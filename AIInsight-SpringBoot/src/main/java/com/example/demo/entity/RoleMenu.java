package com.example.demo.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

import javax.persistence.criteria.CriteriaBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleMenu {
    private Integer id;
    private Integer rid;
    private Integer mid;

    @Transient
    private int[] menus;
    @Transient
    private Integer role_id;
    @Transient
    private String role_name;
    @Transient
    private String description;
}
