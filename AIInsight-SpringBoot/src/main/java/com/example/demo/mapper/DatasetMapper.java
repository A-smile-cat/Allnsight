package com.example.demo.mapper;


import com.example.demo.entity.Datasets;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface DatasetMapper {

    List<Datasets> queryAll(int uid);
    Datasets queryById(int dataset_id,int uid);
    List<Datasets> queryByName(int uid,String name);
    List<Datasets> queryByStatus(int uid,String status);
    List<Datasets> queryByNameAndStatus(int uid,String name, String status);

    void add(Datasets datasets);
    void update(Datasets datasets);

    void updateStatus(int dataset_id,int uid, String status);

    void updateFile(int dataset_id,int uid, String status, String storage_path,  String file_hash);

    void deleteById(int dataset_id, int uid);
}
