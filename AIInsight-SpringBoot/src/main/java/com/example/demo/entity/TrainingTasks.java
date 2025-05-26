package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingTasks {
    private Integer task_id;
    private String task_name;
    private String task_des;
    private String task_type;
    private Integer user_id;
    private Integer dataset_id;
    private String status;
    private Integer model_id;
    private Integer epochs;
    private Integer batch_size;
    private float learning_rate;
    private float accuracy;
    private float loss;
    private Timestamp created_at;

    @Transient
    private String time;
    @Transient
    private String modelName;
    @Transient
    private String datasetName;

}
