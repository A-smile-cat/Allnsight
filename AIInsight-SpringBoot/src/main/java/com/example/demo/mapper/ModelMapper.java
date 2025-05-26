package com.example.demo.mapper;

import com.example.demo.entity.Model;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface ModelMapper {

    int add(Model model);

    int preAdd(String model_name,String type,int user_id,String source);

    List<Model> query(Map<String, Object> params);

    int update(Model model);

    void deleteById(int model_id, int user_id);

    Model queryById(int model_id, int user_id);

    int queryByName(String model_name);

    Model queryByMId(int model_id);
}
