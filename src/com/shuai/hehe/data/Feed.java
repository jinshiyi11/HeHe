package com.shuai.hehe.data;

import com.google.gson.annotations.SerializedName;

/**
 * 新鲜事
 */
public class Feed {
	@SerializedName("id")
	private int mId;
	
	@SerializedName("type")
	private int mType;
	
	@SerializedName("title")
	private String mTitle;
	
	@SerializedName("content")
	private String mContent;
	
	@SerializedName("from")
	private int mFrom;
	
	@SerializedName("showTime")
	private long mShowTime;
	
	//public int mState;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public int getFrom() {
        return mFrom;
    }

    public void setFrom(int from) {
        this.mFrom = from;
    }

    public long getShowTime() {
        return mShowTime;
    }

    public void setShowTime(long showTime) {
        this.mShowTime = showTime;
    }
	
	
}
