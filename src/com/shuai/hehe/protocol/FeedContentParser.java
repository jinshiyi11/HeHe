package com.shuai.hehe.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.shuai.hehe.data.AlbumFeed;
import com.shuai.hehe.data.BlogFeed;
import com.shuai.hehe.data.VideoFeed;

public class FeedContentParser {
    
    public static void parseAlbumFeedContent(AlbumFeed feed,String content) throws JSONException{
        JSONObject json = new JSONObject(content);
        feed.setThumbImgUrl(json.getString("thumbImgUrl"));
        feed.setBigImgUrl(json.getString("bigImgUrl"));
    }
    
    public static void parseVideoFeedContent(VideoFeed feed,String content) throws JSONException{
        JSONObject json = new JSONObject(content);
        feed.setFlashVideoUrl(json.getString("flashVideoUrl"));
        feed.setWebVideoUrl(json.getString("webVideoUrl"));
        feed.setThumbImgUrl(json.getString("videoThumbUrl"));
        
    }

    public static void parseBlogFeedContent(BlogFeed feed, String content) throws JSONException {
        JSONObject json = new JSONObject(content);
        feed.setSummary(json.getString("summary"));
        feed.setWebUrl(json.getString("webUrl"));
    }
}
