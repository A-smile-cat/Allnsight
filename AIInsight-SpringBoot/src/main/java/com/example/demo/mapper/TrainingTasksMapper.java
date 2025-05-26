package com.example.demo.mapper;

import com.example.demo.entity.TrainingTasks;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface TrainingTasksMapper {

    int add(TrainingTasks trainingTasks);

    List<TrainingTasks> query(Map<String, Object> params);

    int update(TrainingTasks trainingTasks);

    TrainingTasks queryById(int task_id, int user_id);

    int deleteById(int task_id, int user_id);
}
