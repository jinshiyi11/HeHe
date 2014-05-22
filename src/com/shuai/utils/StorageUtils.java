package com.shuai.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.os.Environment;

public class StorageUtils {
    /**
     * 复制文件
     * @param source
     * @param dest
     * @throws IOException
     */
    public static void copyFile(File source, File dest)
            throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }
    
    public static File getExternalPicturesDirectory(){
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        
        return path;
    }
    
    public static File getMyPicturesDirectory(){
        File path = getExternalPicturesDirectory();
        
        File file = new File(path, "hehe");
        if(!file.exists())
            file.mkdirs();
        
        return file;
    }


}
