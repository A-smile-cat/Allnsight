package com.example.demo.controller;


import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.example.demo.entity.Datasets;
import com.example.demo.entity.UserInfo;
import com.example.demo.result.Result;
import com.example.demo.result.ResultFactory;
import com.example.demo.service.DatasetService;
import com.example.demo.utils.FilePathUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import javax.mail.Multipart;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@CrossOrigin(exposedHeaders = "Content-Disposition")
@Slf4j
@RestController
@RequestMapping("/")
public class DatasetController {
    @Autowired
    private DatasetService datasetService;

    private static final String filePath = Paths.get(System.getProperty("user.dir")).getParent() + "/SharedData/ModelFiles/";

    @GetMapping("/searchDataset")
    public Result searchDataset(@RequestParam(name = "keyword",defaultValue = "") String name, @RequestParam(name="status",defaultValue = "") String status, @RequestParam("user_id") int uid, HttpSession httpSession){
        if (httpSession == null || httpSession.getAttribute("user") == null) {
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo user =(UserInfo) httpSession.getAttribute("user");

        if(uid != user.getId()){
            return ResultFactory.buildFailResult("无权限");
        }

        List<Datasets> datasetsList = new ArrayList<>();
        if(name.isEmpty() && status.isEmpty()){
            datasetsList = datasetService.queryAll(uid);
        }else if(!name.isEmpty() && status.isEmpty()){
            datasetsList = datasetService.queryByName(uid,name);
        }else if(!status.isEmpty() && name.isEmpty()){
            datasetsList = datasetService.queryByStatus(uid,status);
        }else{
            datasetsList = datasetService.queryByNameAndStatus(uid,name,status);
        }

        if(datasetsList.isEmpty()){
            return ResultFactory.buildFailResult("无查询结果");
        }else{
            return ResultFactory.buildSuccessResult("查询成功",JSON.toJSONString(datasetsList));
        }
    }

    @PostMapping("/dataset")
    public Result add(@RequestBody Datasets datasets,HttpSession httpSession){
        if(httpSession == null || httpSession.getAttribute("user") == null) {
            return ResultFactory.buildFailResult("未登录");
        }

        // 从Session获取当前用户ID，并强制设置到数据对象
        UserInfo currentUser = (UserInfo) httpSession.getAttribute("user");
        datasets.setUser_id(currentUser.getId()); // 关键！确保数据归属

        //获取当前时间
        datasets.setCreated_at(new java.sql.Timestamp(System.currentTimeMillis()));
//        LocalDateTime now = LocalDateTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        datasets.setCreated_at(formatter.format(now));
        datasets.setStatus("new");
        datasetService.add(datasets);
        return ResultFactory.buildSuccessResult("添加成功");
    }

    @PutMapping("/dataset/{id}")
    public Result update(@PathVariable(value = "id")Integer id,@RequestBody Datasets datasets,HttpSession httpSession){
        if(httpSession == null || httpSession.getAttribute("user") == null) {
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo currentUser = (UserInfo) httpSession.getAttribute("user");
        int user_id = currentUser.getId();
//        datasets.setUser_id(user_id);
        if (!datasetService.isDatasetOwnedByUser(id, user_id)) {
            return ResultFactory.buildFailResult("无权修改他人数据");
        }
        datasets.setUser_id(user_id);
        datasets.setDataset_id(id);
        datasetService.update(datasets);
        return ResultFactory.buildSuccessResult("修改成功");
    }

    @PostMapping("/uploadDataset")
    public Result uploadDataset(@RequestParam("file") MultipartFile file,
                                @RequestParam int dataset_id,
                                @RequestParam int user_id,HttpSession httpSession){
        if(httpSession == null || httpSession.getAttribute("user") == null) {
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo currentUser = (UserInfo) httpSession.getAttribute("user");
        if(currentUser.getId() != user_id){
            return ResultFactory.buildFailResult("无权限");
        }
        user_id = currentUser.getId();
        if(datasetService.verfiyStatus(dataset_id,user_id)){
            synchronized (DatasetController.class){
                String flag = System.currentTimeMillis() + "";
                String filename = file.getOriginalFilename();
                String extension = FilenameUtils.getExtension(filename).toLowerCase();
                List<String> allowedExtensions = Arrays.asList("zip", "rar", "7z", "gz", "tar"); // 允许的压缩格式
                if (!allowedExtensions.contains(extension)) {
                    return ResultFactory.buildFailResult("仅支持上传压缩文件（.zip/.rar/.7z等）");
                }
                String filename2 = StringUtils.cleanPath(filename);
                if (filename2.contains("..")) {
                    return ResultFactory.buildFailResult("文件名非法");
                }
                try{
                    if(!FileUtil.isDirectory(filePath)){
                        FileUtil.mkdir(filePath);
                    }
                    datasetService.updateStatus(dataset_id,user_id,"uploading");
                    // 生成唯一文件名
                    String uniqueFilename = FilePathUtils.generateUniqueFilename(file.getOriginalFilename());
                    // 获取完整存储路径
                    String fullPath = filePath + uniqueFilename;
                    byte[] fileBytes = file.getBytes();
                    FileUtil.writeBytes(fileBytes,fullPath);
                    String fileHash = DigestUtils.sha256Hex(fileBytes);
                    datasetService.updateFile(dataset_id, user_id, "uploaded", fullPath, fileHash);
                    Thread.sleep(1L);
                }catch (Exception e){
                    datasetService.updateStatus(dataset_id,user_id,"upload_failed");
                    return ResultFactory.buildFailResult("上传失败");
                }
                return ResultFactory.buildSuccessResult("上传成功");
            }
        }
        else{
            return ResultFactory.buildFailResult("该数据集状态不允许上传文件");
        }

    }

    @GetMapping("/downloadDataset/{dataset_id}")
    public ResponseEntity<?> downloadDataset(@PathVariable(value = "dataset_id") Integer dataset_id, @RequestParam(value = "user_id")Integer user_id,HttpSession httpSession){
        if(httpSession == null || httpSession.getAttribute("user") == null){
            Map<String, String> error = Collections.unmodifiableMap(new HashMap<String, String>() {{
                put("code", "ERROR");
                put("message", "未登录");
            }});
            return ResponseEntity.status(404).body(error);
        }
        UserInfo currentUser = (UserInfo) httpSession.getAttribute("user");
        if(currentUser.getId() != user_id){
            Map<String, String> error = Collections.unmodifiableMap(new HashMap<String, String>() {{
                put("code", "ERROR");
                put("message", "无权限");
            }});
            return ResponseEntity.status(404).body(error);
        }
        user_id = currentUser.getId();

        Datasets datasets = datasetService.queryById(dataset_id,user_id);
        if( datasets.getFile_hash() == null  || datasets == null || datasets.getFile_hash().trim().equals("") ){
            return ResponseEntity.notFound().build();
        }else if (!datasetService.verfiyHash(dataset_id,user_id)){
            Map<String, String> error = Collections.unmodifiableMap(new HashMap<String, String>() {{
                put("code", "ERROR");
                put("message", "数据集文件损坏");
            }});
            return ResponseEntity.status(404).body(error);
        }else{
           try {
               File file = new File(datasets.getStorage_path());
               Path path = file.toPath();
               String  filename = path.getFileName().toString();
               // 使用 InputStreamResource 实现流式传输
               InputStreamResource resource = new InputStreamResource(Files.newInputStream(path));

               return ResponseEntity.ok()
                       .header("Content-Disposition", "attachment; filename=dataset_" + filename)
                       .header("Access-Control-Expose-Headers", "Content-Disposition")
                       .contentType(MediaType.APPLICATION_OCTET_STREAM)
                       .contentLength(file.length())
                       .body(resource);
           }
           catch (Exception e) {
               Map<String, String> error = Collections.unmodifiableMap(new HashMap<String, String>() {{
                   put("code", "UnKnown_ERROR");
                   put("message", "未知错误");
               }});
               return ResponseEntity.status(404).body(error);
           }
        }
    }

    @DeleteMapping("/deleteDataset/{dataset_id}")
    public Result deleteDataset(
            @PathVariable("dataset_id") int datasetId,
            @RequestParam("user_id") int userId,
            HttpSession session) {
        if(session == null || session.getAttribute("user") == null){
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo currentUser = (UserInfo) session.getAttribute("user");
        if(currentUser.getId() != userId){
            return ResultFactory.buildFailResult("无权限");
        }
        userId = currentUser.getId();
        Datasets datasets = datasetService.queryById(datasetId,userId);
        if(datasets == null){
            return ResultFactory.buildFailResult("数据集不存在");
        } else if(datasets.getStatus().equals("uploading")){
            return ResultFactory.buildFailResult("数据集正在上传中，请稍后再试");
        } else if(datasets.getStatus().equals("training")){
            return ResultFactory.buildFailResult("数据集正在训练中，请稍后再试");
        }

        String storagePath = datasets.getStorage_path();
        try{
            if(storagePath != null && !storagePath.trim().equals("")){
                FileUtil.del(storagePath);
            }
            datasetService.deleteById(datasetId,userId);
            return ResultFactory.buildSuccessResult("删除成功");
        }catch (Exception e){
            return ResultFactory.buildFailResult("删除失败");
        }
    }
}
