package com.example.demo.dto;

public class ClsProcessResult {
    private String originalImage;  // 原始图片Base64（可选）
    private String labels;      //  类别标签
    private String confidence;   // 置信度
    private int code;
    private String error;          // 错误信息（处理失败时）
    private Long processingTime;   // 处理耗时(毫秒)

    // 构造器
    public ClsProcessResult() {}

    public ClsProcessResult(String label, String confidence) {
        this.labels = label;
        this.confidence = confidence;
    }

    // Getter和Setter
    public String getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(String originalImage) {
        this.originalImage = originalImage;
    }

    public String getlabels() {
        return labels;
    }

    public void setlabels(String label) {
        this.labels = label;
    }

    public int getCode(){
        return code;
    }

    public void setCode(int code){
        this.code = code;
    }

    public String getconfidence() {
        return confidence;
    }

    public void setconfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Long getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(Long processingTime) {
        this.processingTime = processingTime;
    }
}