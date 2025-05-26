package com.example.demo.dto;

public class SegProcessResult {
    private String originalImage;  // 原始图片Base64（可选）
    private String maskImage;      // 分割掩码图Base64
    private String overlayImage;   // 叠加结果图Base64
    private int code;
    private String error;          // 错误信息（处理失败时）
    private Long processingTime;   // 处理耗时(毫秒)

    // 构造器
    public SegProcessResult() {}

    public SegProcessResult(String maskImage, String overlayImage) {
        this.maskImage = maskImage;
        this.overlayImage = overlayImage;
    }

    // Getter和Setter
    public String getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(String originalImage) {
        this.originalImage = originalImage;
    }

    public String getMaskImage() {
        return maskImage;
    }

    public void setMaskImage(String maskImage) {
        this.maskImage = maskImage;
    }

    public int getCode(){
        return code;
    }

    public void setCode(int code){
        this.code = code;
    }

    public String getOverlayImage() {
        return overlayImage;
    }

    public void setOverlayImage(String overlayImage) {
        this.overlayImage = overlayImage;
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