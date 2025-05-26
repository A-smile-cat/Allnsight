package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.sql.Timestamp;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Model {
    private int model_id;
    private String model_name;
    private String model_des;
    private String type;
    private int user_id;
    private String storage_path;
    private String file_hash;
    private String source;
    private Timestamp created_at;

    @Transient
    private String time;
}
