package com.shuai.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.os.Environment;

public class StorageUtils {
    
    /**
     * 将assets下的资源文件读入字符串
     * @param context
     * @param assetFilePath
     * @return
     * @throws IOException
     */
    public static String loadAssetFileData(Context context,String assetFilePath) throws IOException{
        InputStream stream = context.getAssets().open(assetFilePath);
        return inputStreamToString(stream);
    }
    
    public static String inputStreamToString(InputStream input){
        String result=null;
        
        ByteArrayOutputStream output=new ByteArrayOutputStream();
        byte[] buf = new byte[512];
        int bytesRead;
        try {
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        
        result=output.toString();
        return result;
    }
    
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
