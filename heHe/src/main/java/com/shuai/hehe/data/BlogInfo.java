package com.shuai.hehe.data;

import com.google.gson.annotations.SerializedName;

/**
 * 日志信息
 */
public class BlogInfo {

    /**
     * 日志id
     */
    @SerializedName("id")
    private int mId;
    
    /**
     * 新鲜事id
     */
    @SerializedName("feedId")
    private int mFeedId;
    
    /**
     * 日志的html内容
     */
    @SerializedName("htmlContent")
    private String mHtmlContent;
    
    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }
    
    public int getFeedId() {
        return mFeedId;
    }

    public void setFeedId(int feedId) {
        this.mFeedId = feedId;
    }

    public String getHtmlContent() {
        return mHtmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.mHtmlContent = htmlContent;
    }
    
}
