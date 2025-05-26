package com.example.demo.utils;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;

// 路径工具类
@Component
public class FilePathUtils {
    private static String rootPath;

//    @Value("${file.storage.root}")
//    public void setRootPath(String path) {
//        this.rootPath = Paths.get(path).normalize().toString();
//        createDirs();
//    }

//    private static void createDirs() {
//        new File(getModelPath()).mkdirs();
//        new File(getDatasetPath()).mkdirs();
//    }

    public static String getModelPath() {
        return Paths.get(rootPath, "ModelFiles").toString();
    }

    public static String getDatasetPath() {
        return Paths.get(rootPath, "Datasets").toString();
    }

    public static String generateUniqueFilename(String originalFilename) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return timestamp + "-" + originalFilename;
    }

    public static String getModelStoragePath(String filename) {
        return Paths.get(getModelPath(), filename).toString();
    }
}
