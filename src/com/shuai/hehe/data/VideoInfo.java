package com.shuai.hehe.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 视频信息。有些网站会把一个视频分割为多个文件，比如优酷
 */
public class VideoInfo {
    /**
     * 视频名
     */
    @SerializedName("title")
    private String mTitle;
    
    /**
     * 视频时长，单位秒
     */
    @SerializedName("duration")
    private double mDuration;
    
    /**
     * 有些网站会把一个视频分割为多个文件，比如优酷
     */
    @SerializedName("parts")
    private List<Part> mParts;
    
    public static class Part{
        //该段视频时长，单位秒.有些网站会把一个视频分割为多个文件，比如优酷
        @SerializedName("duration")
        private double mDuration;
        
        //视频文件地址
        @SerializedName("videoUrl")
        private String mVideoUrl;
        
        public double getDuration() {
            return mDuration;
        }

        public void setDuration(double duration) {
            this.mDuration = duration;
        }
        
        public String getVideoUrl() {
            return mVideoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.mVideoUrl = videoUrl;
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public double getDuration() {
        return mDuration;
    }

    public void setDuration(double duration) {
        this.mDuration = duration;
    }

    public List<Part> getParts() {
        return mParts;
    }

    public void setParts(List<Part> parts) {
        this.mParts = parts;
    }
    
}
