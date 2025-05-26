package com.example.demo.service;

import com.example.demo.entity.Model;
import com.example.demo.entity.TrainingTasks;
import com.example.demo.mapper.TrainingTasksMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TrainingTasksService {
    @Autowired
    private TrainingTasksMapper trainingTasksMapper;

    @Autowired
    private ModelService modelService;

    @Autowired
    private DatasetService datasetService;

    public int add(TrainingTasks trainingTasks,String modelName)
    {
        modelService.add(modelName,trainingTasks.getTask_type(),trainingTasks.getUser_id(),"train");
        int model_id = modelService.queryByName(modelName);
        trainingTasks.setModel_id(model_id);
        trainingTasks.setCreated_at(Timestamp.valueOf(LocalDateTime.now()));
        trainingTasks.setStatus("queued");
        return trainingTasksMapper.add(trainingTasks);
    }

    public List<TrainingTasks> query(int uid, String name, String type, String status) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("type", type);
        params.put("status", status);
        params.put("uid", uid);

        List<TrainingTasks> trainingTasksList = trainingTasksMapper.query(params);
        for (TrainingTasks task : trainingTasksList ) {
            Timestamp timestamp = task.getCreated_at();
            LocalDateTime localDateTime = timestamp.toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = localDateTime.format(formatter);
            task.setTime(formattedDate);

            task.setModelName(modelService.queryById(task.getModel_id(), uid).getModel_name());
            task.setDatasetName(datasetService.queryById(task.getDataset_id(), uid).getName());
        }
        return trainingTasksList;
    }

    public int update(TrainingTasks trainingTasks){
        return trainingTasksMapper.update(trainingTasks);
    }

    public TrainingTasks queryById(int task_id, int user_id) {
        return trainingTasksMapper.queryById(task_id, user_id);
    }

    public int deleteById(int task_id, int user_id) {
        return trainingTasksMapper.deleteById(task_id, user_id);
    }
}
