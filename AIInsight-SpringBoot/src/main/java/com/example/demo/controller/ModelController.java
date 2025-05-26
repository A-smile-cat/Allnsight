package com.example.demo.controller;


import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.example.demo.entity.Datasets;
import com.example.demo.entity.Model;
import com.example.demo.entity.UserInfo;
import com.example.demo.result.Result;
import com.example.demo.result.ResultFactory;
import com.example.demo.service.ModelService;
import com.example.demo.utils.FilePathUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/")
public class ModelController {
    @Autowired
    private ModelService modelService;

    private static final String filePath = Paths.get(System.getProperty("user.dir")).getParent() + "/SharedData/ModelFiles/";
    @PostMapping("/uploadModel")
    public Result uploadModel(@RequestParam("file") MultipartFile file,
                              @RequestParam int user_id,
                              @RequestParam String modelName,
                              @RequestParam(defaultValue = "") String modelDescription,
                              @RequestParam String modelType,
                              HttpSession session){
        if (session == null || session.getAttribute("user") == null) {
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo user = (UserInfo) session.getAttribute("user");
        if(user.getId() != user_id){
            return ResultFactory.buildFailResult("无权限");
        }
            synchronized (ModelController.class){
                try{
                    if(!FileUtil.isDirectory(filePath)){
                        FileUtil.mkdir(filePath);
                    }
                    // 生成唯一文件名
                    String uniqueFilename = FilePathUtils.generateUniqueFilename(file.getOriginalFilename());
                    // 获取完整存储路径
                    String fullPath = filePath + uniqueFilename;

                    byte[] fileBytes = file.getBytes();
                    FileUtil.writeBytes(fileBytes,fullPath);
                    String fileHash = DigestUtils.sha256Hex(fileBytes);
                    Timestamp currentTime = new java.sql.Timestamp(System.currentTimeMillis());

                    int f = modelService.add(modelName,modelDescription,modelType,user_id, fullPath, fileHash,"upload",currentTime);
                    Thread.sleep(1L);
                    if(f==1){
                        return ResultFactory.buildSuccessResult("上传成功");
                    }else{
                        return ResultFactory.buildFailResult("上传失败");
                    }

                }catch (Exception e){
                    return ResultFactory.buildFailResult("上传失败");
                }

            }
    }

    @GetMapping("/searchModel")
    public Result searchModel(@RequestParam(name = "modelName",defaultValue = "") String name,@RequestParam(name="type",defaultValue = "") String type,@RequestParam(name="status",defaultValue = "") String status,@RequestParam("user_id") int uid,HttpSession session){
        if(session == null || session.getAttribute("user") == null){
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo user = (UserInfo) session.getAttribute("user");
        if(user.getId() != uid){
            return ResultFactory.buildFailResult("无访问权限");
        }

        List<Model> modelFileList = new ArrayList<>();

        modelFileList = modelService.query(uid,name,type,status);

        if(modelFileList.isEmpty()){
            return ResultFactory.buildSuccessResult("无查询结果",JSON.toJSONString(modelFileList));
        }else{
            return ResultFactory.buildSuccessResult("查询成功", JSON.toJSONString(modelFileList));
        }
    }

    @PutMapping("/model/{id}")
    public Result updateModel(@PathVariable(value = "id")Integer id,@RequestBody Model model,HttpSession session) {
        if(session == null || session.getAttribute("user") == null){
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo user = (UserInfo) session.getAttribute("user");
        if(user.getId() != model.getUser_id()){
            return ResultFactory.buildFailResult("无权限");
        }

        model.setModel_id(id);
        int f = modelService.update(model);
        if (f == 1) {
            return ResultFactory.buildSuccessResult("修改成功");
        } else {
            return ResultFactory.buildFailResult("修改失败");
        }
    }

    @DeleteMapping("/deleteModel/{model_id}")
    public Result deleteModel(
            @PathVariable("model_id") int modelId,
            @RequestParam("user_id") int userId,
            HttpSession session) {
        if(session == null || session.getAttribute("user") == null){
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo user = (UserInfo) session.getAttribute("user");
        if(user.getId() != userId){
            return ResultFactory.buildFailResult("无权限");
        }

        return modelService.deleteById(modelId,userId) ;
    }

    @GetMapping("/downloadModel/{model_id}")
    public ResponseEntity<?> downloadModel(@PathVariable(value = "model_id") Integer model_id, @RequestParam(value = "user_id")Integer user_id,HttpSession session){
        if (session == null || session.getAttribute("user") == null) {
            return ResponseEntity.badRequest().build();
        }
        UserInfo user = (UserInfo) session.getAttribute("user");
        if(user.getId() != user_id){
            return ResponseEntity.badRequest().build();
        }

        Model model = modelService.queryById(model_id,user_id);
        if( model.getFile_hash() == null  || model == null || model.getFile_hash().trim().equals("") ){
            return ResponseEntity.notFound().build();
        }else if (!modelService.verfiyHash(model_id,user_id)){
            Map<String, String> error = Collections.unmodifiableMap(new HashMap<String, String>() {{
                put("code", "ERROR");
                put("message", "数据集文件损坏");
            }});
            return ResponseEntity.status(404).body(error);
        }else{
            try {
                File file = new File(model.getStorage_path());
                Path path = file.toPath();
                String  filename = path.getFileName().toString();

                // 使用 InputStreamResource 实现流式传输
                InputStreamResource resource = new InputStreamResource(Files.newInputStream(path));

                return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=model_" + filename)
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
}
