package com.shuai.hehe.data;

import com.google.gson.annotations.SerializedName;

/**
 * 相册中单个图片的信息
 */
public class PicInfo {
    @SerializedName("id")
    private int mId;
    
    /**
     * 该图片大图的url
     */
    @SerializedName("bigUrl")
    private String mBigUrl;
    
    /**
     * 该图片的描述
     */
    @SerializedName("description")
    private String mDescription;

    public PicInfo() {
    }
    
    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getBigPicUrl() {
        return mBigUrl;
    }

    public void setBigPicUrl(String bigUrl) {
        this.mBigUrl = bigUrl;
    }

    public String getPicDescription() {
        return mDescription;
    }

    public void setPicDescription(String description) {
        this.mDescription = description;
    }

}
