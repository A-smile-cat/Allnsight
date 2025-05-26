package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Menus {
    private Integer id;
    private String name;
    private String path;
    private String component;
    private String name_des;
    private Integer parent_id;
    private String icon_cls;

    @Transient
    private List<Menus> children;

}
