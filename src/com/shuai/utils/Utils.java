package com.shuai.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringBufferInputStream;
import com.shuai.io.StringBufferOutputStream;

public class Utils {
    
    /**
     * 移除java类型注释
     * @param data
     * @return
     */
    public static String removeComment(String data) {
        InputStream in=new StringBufferInputStream(data);
        OutputStream out=new StringBufferOutputStream();
        try {
            removeComment(in, out);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return out.toString();
    }

    /**
     * 移除java类型注释
     * @param inStream
     * @param outStream
     * @throws IOException
     */
    public static void removeComment(InputStream inStream, OutputStream outStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
        boolean inBlockComment = false;
        boolean inSlashSlashComment = false;
        int char1 = reader.read();
        if (char1 != -1) {
            int char2;
            while (char1 != -1) {
                if ((char2 = reader.read()) == -1) {
                    writer.write(char1);
                    break;
                }
                if (char1 == '/' && char2 == '*') {
                    inBlockComment = true;
                    char1 = reader.read();
                    continue;
                } else if (char1 == '*' && char2 == '/') {
                    inBlockComment = false;
                    char1 = reader.read();
                    continue;
                } else if (char1 == '/' && char2 == '/' && !inBlockComment) {
                    inSlashSlashComment = true;
                    char1 = reader.read();
                    continue;
                }
                if (inBlockComment) {
                    char1 = char2;
                    continue;
                }
                if (inSlashSlashComment) {
                    if (char2 == '\n') {
                        inSlashSlashComment = false;
                        writer.write(char2);
                        char1 = reader.read();
                        continue;
                    } else if (char1 == '\n') {
                        inSlashSlashComment = false;
                        writer.write(char1);
                        char1 = char2;
                        continue;
                    } else {
                        char1 = reader.read();
                        continue;
                    }
                }
                writer.write(char1);
                char1 = char2;
            }
            writer.flush();
            writer.close();
        }
    }   



}
