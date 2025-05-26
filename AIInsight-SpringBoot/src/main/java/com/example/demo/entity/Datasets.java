package com.example.demo.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.Transient;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Datasets {
    private Integer dataset_id;
    private Integer user_id;
    private String name;
    private String description;
    private String storage_path;
    private String status;
    private String file_hash;
    private Timestamp created_at;

    @Transient
    private String username;
    @Transient
    private String time;
}
