package com.shuai.utils;

import java.io.File;

public class FileUtils {

    /**
     * 获取文件的扩展名
     * @param file
     * @return
     */
    public static String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }
    
    /**
     * 获取文件的名称，不包含扩展名
     * @param file
     * @return
     */
    public static String getFileBasename(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1) {
            return fileName.substring(0,fileName.lastIndexOf("."));
        } else {
            return fileName;
        }
    }

}
