package com.example.demo.service;

import cn.hutool.core.io.FileUtil;
import com.example.demo.entity.Datasets;
import com.example.demo.entity.Model;
import com.example.demo.mapper.ModelMapper;
import com.example.demo.result.Result;
import com.example.demo.result.ResultFactory;
import com.example.demo.utils.FilePathUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModelService {
    @Autowired
    private ModelMapper modelMapper;

    public int add(String model_name, String model_des, String type,int user_id, String fullPath, String fileHash, String source, Timestamp currentTime){
        Model model = new Model();
        model.setModel_name(model_name);
        model.setModel_des(model_des);
        model.setType(type);
        model.setUser_id(user_id);
        model.setStorage_path(fullPath);
        model.setFile_hash(fileHash);
        model.setSource(source);
        model.setCreated_at(currentTime);
        return  modelMapper.add(model);
    }

    public int add(String model_name,String type,int user_id,String source){
        return modelMapper.preAdd(model_name, type, user_id, source);
    }

    public List<Model> query(int uid, String name, String type, String status) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("type", type);
        params.put("status", status);
        params.put("uid", uid);

        List<Model> modelList = modelMapper.query(params);
        for (Model model : modelList ) {
            Timestamp timestamp = model.getCreated_at();
            LocalDateTime localDateTime = timestamp.toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = localDateTime.format(formatter);
            model.setTime(formattedDate);
        }
        return modelList;
    }

    public Model queryById(int model_id, int user_id){
        return modelMapper.queryById(model_id, user_id);
    }

    public Model queryById(int model_id){
        return modelMapper.queryByMId(model_id);
    }
    public int update(Model model) {
        return modelMapper.update(model);
    }

    public Result deleteById(int model_id, int user_id) {
        Model model = queryById(model_id,user_id);
        if(model == null){
            return ResultFactory.buildFailResult("模型不存在");
        }

        String storagePath = model.getStorage_path();
        try{
            if(storagePath != null && !storagePath.trim().equals("")){
                FileUtil.del(storagePath);
            }
            modelMapper.deleteById(model_id, user_id);
            return ResultFactory.buildSuccessResult("删除成功");
        }catch (Exception e){
            return ResultFactory.buildFailResult("删除失败");
        }
    }

    public boolean verfiyHash(Integer dataset_id, Integer user_id) {
        Model model = modelMapper.queryById(dataset_id,  user_id);
        if (model == null || model.getFile_hash() ==null){
            return false;
        }else{
            String filePath = model.getStorage_path();
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("文件不存在");
                return false;
            }
            try(FileInputStream fis = new FileInputStream(file)){
                // 计算文件的 SHA-256 哈希值
                String actualHash = DigestUtils.sha256Hex(fis);
                // 比较哈希值
                String expectedHash = model.getFile_hash();
                boolean isMatch = expectedHash.equalsIgnoreCase(actualHash);

                return isMatch;
            }catch (Exception e){
                return false;
            }
        }
    }

    public int queryByName(String name){
        return modelMapper.queryByName(name);
    }
}
