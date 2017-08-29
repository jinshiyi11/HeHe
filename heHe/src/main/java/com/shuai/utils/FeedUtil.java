package com.shuai.utils;

import android.util.Log;

import com.shuai.hehe.data.Feed;

import org.json.JSONObject;

public class FeedUtil {
    private static final String TAG=FeedUtil.class.getSimpleName();

    public static String getFeedVideoId(Feed feed){
        String videoId=null;
        try{
            JSONObject json=new JSONObject(feed.getContent());
            videoId=json.getString("videoId");
        }catch (Exception e){
            Log.e(TAG,"",e);
        }
        return videoId;
    }
}
