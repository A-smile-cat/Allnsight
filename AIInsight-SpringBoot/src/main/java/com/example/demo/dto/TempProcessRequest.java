package com.example.demo.dto;

import javax.persistence.criteria.CriteriaBuilder;

public class TempProcessRequest {
    private String image;      // Base64编码的图片数据
//    private Integer model_id;
    private Integer model;      // 选择的模型名称


    // 构造器
    public TempProcessRequest() {}

    public TempProcessRequest(String image, Integer model_id) {
        this.image = image;
        this.model = model_id;
    }

    // Getter和Setter
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

//    public Integer getModel_id() {
//        return model_id;
//    }
//
//    public void setModel_id(Integer model_id) {
//        this.model_id = model_id;
//    }

    public Integer getModel() {
        return model;
    }

    public void setModel(Integer model) {
        this.model = model;
    }

}