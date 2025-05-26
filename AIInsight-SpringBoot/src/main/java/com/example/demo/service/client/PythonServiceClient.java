package com.example.demo.service.client;

import com.example.demo.dto.ClsProcessResult;
import com.example.demo.dto.SegProcessResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class PythonServiceClient {

    @Value("${python.service.url}")
    private String pythonServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public SegProcessResult processSegProcess(String imageBase64, Integer model) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("image", imageBase64);
        requestBody.put("model", model);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // 调用Python服务
            ResponseEntity<SegProcessResult> response = restTemplate.postForEntity(
                    pythonServiceUrl + "/segProcess",
                    requestEntity,
                    SegProcessResult.class);

            return response.getBody();
        } catch (Exception e) {
            SegProcessResult errorResult = new SegProcessResult();
            errorResult.setError("Python服务调用失败: " + e.getMessage());
            return errorResult;
        }
    }


    public ClsProcessResult processClsProcess(String imageBase64, Integer model) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("image", imageBase64);
        requestBody.put("model", model);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // 调用Python服务
            ResponseEntity<ClsProcessResult> response = restTemplate.postForEntity(
                    pythonServiceUrl + "/clsProcess",
                    requestEntity,
                    ClsProcessResult.class);

            return response.getBody();
        } catch (Exception e) {
            ClsProcessResult errorResult = new ClsProcessResult();
            errorResult.setError("Python服务调用失败: " + e.getMessage());
            return errorResult;
        }
    }
}