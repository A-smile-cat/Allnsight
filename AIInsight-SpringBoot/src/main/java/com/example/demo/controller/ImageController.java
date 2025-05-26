package com.example.demo.controller;

import com.example.demo.dto.ClsProcessResult;
import com.example.demo.dto.SegProcessResult;
import com.example.demo.dto.TempProcessRequest;
import com.example.demo.service.ImageProcessingService;
import com.example.demo.service.client.PythonServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/image")
public class ImageController {
    @Autowired
    private ImageProcessingService imageProcessingService;

    @Autowired
    private PythonServiceClient pythonServiceClient;

    @PostMapping("/segProcess")
    public ResponseEntity<Map<String, String>> tempSegProcessImage(
            @RequestBody TempProcessRequest request, HttpSession session) {
        if( session == null ||  session.getAttribute("user") == null){
            return ResponseEntity.badRequest().build();
        }

        // 1. 验证输入
        if (request.getImage() == null || request.getModel() == null) {
            return ResponseEntity.badRequest().build();
        }

        // 2. 调用Python服务（不存储任何数据）
        SegProcessResult result = pythonServiceClient.processSegProcess(
                request.getImage(),
                request.getModel());

        // 3. 返回Base64编码的结果
        Map<String, String> response = new HashMap<>();
        response.put("mask", result.getMaskImage());
        response.put("overlay", result.getOverlayImage());
        response.put("code","200");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/clsProcess")
    public ResponseEntity<Map<String, String>> tempClsProcessImage(
            @RequestBody TempProcessRequest request,HttpSession session) {
        if( session == null ||  session.getAttribute("user") == null){
            return ResponseEntity.badRequest().build();
        }

        // 1. 验证输入
        if (request.getImage() == null || request.getModel() == null) {
            return ResponseEntity.badRequest().build();
        }

        // 2. 调用Python服务（不存储任何数据）
          ClsProcessResult result = pythonServiceClient.processClsProcess(
                  request.getImage(),
                  request.getModel());

        // 3. 返回Base64编码的结果
        Map<String, String> response = new HashMap<>();
        response.put("labels", result.getlabels());
        response.put("confidence", result.getconfidence());
        response.put("code","200");

        return ResponseEntity.ok(response);

    }
}
