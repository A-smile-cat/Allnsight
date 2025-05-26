package com.example.demo.service;

import com.example.demo.entity.Datasets;
import com.example.demo.mapper.DatasetMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DatasetService {

    @Autowired
    private DatasetMapper datasetMapper;


    public List<Datasets> queryAll(int uid) {
        List<Datasets> datasetsList = datasetMapper.queryAll(uid);
        for (Datasets datasets : datasetsList) {
            Timestamp timestamp = datasets.getCreated_at();
            // 1. 将 Timestamp 转换为 LocalDateTime
            LocalDateTime localDateTime = timestamp.toLocalDateTime();
            // 2. 定义日期时间格式（例如：yyyy-MM-dd HH:mm:ss）
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // 3. 格式化 LocalDateTime 为字符串
            String formattedDate = localDateTime.format(formatter);
            datasets.setTime(formattedDate);
        }
        return datasetsList;
    }

    public List<Datasets> queryByName(int uid,String name) {
        List<Datasets> datasetsList = datasetMapper.queryByName(uid,name);
        for (Datasets datasets : datasetsList) {
            Timestamp timestamp = datasets.getCreated_at();
            LocalDateTime localDateTime = timestamp.toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = localDateTime.format(formatter);
            datasets.setTime(formattedDate);
        }
        return datasetsList;
    }

    public List<Datasets> queryByStatus(int uid,String status) {
        List<Datasets> datasetsList = datasetMapper.queryByStatus(uid,status);
        for (Datasets datasets : datasetsList) {
            Timestamp timestamp = datasets.getCreated_at();
            LocalDateTime localDateTime = timestamp.toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = localDateTime.format(formatter);
            datasets.setTime(formattedDate);
        }
        return datasetsList;
    }

    public List<Datasets> queryByNameAndStatus(int uid,String name, String status) {
        List<Datasets> datasetsList = datasetMapper.queryByNameAndStatus(uid,name,status);
        for (Datasets datasets : datasetsList) {
            Timestamp timestamp = datasets.getCreated_at();
            LocalDateTime localDateTime = timestamp.toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = localDateTime.format(formatter);
            datasets.setTime(formattedDate);
        }
        return datasetsList;
    }

    public Datasets queryById(int dataset_id,int uid) {
        Datasets datasets = datasetMapper.queryById(dataset_id,uid);
        Timestamp timestamp = datasets.getCreated_at();
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = localDateTime.format(formatter);
        datasets.setTime(formattedDate);
        return datasets;
    }

    public void add(Datasets datasets) {
        datasetMapper.add(datasets);
    }

    public void update(Datasets datasets) {
        datasetMapper.update(datasets);
    }

    public void updateStatus(int dataset_id,int user_id, String status) {
        datasetMapper.updateStatus(dataset_id,user_id, status);
    }
    public void updateFile(int dataset_id, int user_id,String status, String storage_path,String filehash) {
        datasetMapper.updateFile(dataset_id,user_id, status, storage_path,filehash);
    }

    public boolean verfiyStatus(int dataset_id, int user_id) {
        Datasets dataset = datasetMapper.queryById(dataset_id,  user_id);
        if ( dataset!=null &&( dataset.getStatus().equals("new") || dataset.getStatus().equals("upload_failed"))) {
            return true;
        }
        return false;
    }

    public boolean verfiyHash(int dataset_id, int user_id){
        Datasets dataset = datasetMapper.queryById(dataset_id,  user_id);
        if (dataset == null || dataset.getFile_hash() ==null){
            return false;
        }else{
            String filePath = dataset.getStorage_path();
            File file = new File(filePath);
            if (!file.exists()) {
                return false;
            }
            try(FileInputStream fis = new FileInputStream(file)){
                // 计算文件的 SHA-256 哈希值
                String actualHash = DigestUtils.sha256Hex(fis);
                // 比较哈希值
                String expectedHash = dataset.getFile_hash();
                boolean isMatch = expectedHash.equalsIgnoreCase(actualHash);

                return isMatch;
            }catch (Exception e){
                return false;
            }
        }
    }

    public void deleteById(int datasetId, int userId) {
        datasetMapper.deleteById(datasetId, userId);
    }

    public boolean isDatasetOwnedByUser(Integer id, int user_id) {
        Datasets dataset = datasetMapper.queryById(id, user_id);
        return dataset != null;
    }
}
