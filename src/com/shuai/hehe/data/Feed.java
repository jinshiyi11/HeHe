package com.shuai.hehe.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 新鲜事
 */
public class Feed implements Parcelable {
	@SerializedName("id")
	private long mId;
	
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
	
	/**
     * 收藏时间,只有是收藏新鲜事该字段才有效
     */
    private long mStarTime;
	
	public Feed(){
	    
	}

    public long getId() {
        return mId;
    }

    public void setId(long id) {
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
    
    public long getStarTime() {
        return mStarTime;
    }

    public void setStarTime(long starTime) {
        this.mStarTime = starTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeInt(mType);
        dest.writeString(mTitle);
        dest.writeString(mContent);
        dest.writeInt(mFrom);
        dest.writeLong(mShowTime);
        dest.writeLong(mStarTime);
    }
    
    private Feed(Parcel in){
        mId=in.readLong();
        mType=in.readInt();
        mTitle=in.readString();
        mContent=in.readString();
        mFrom=in.readInt();
        mShowTime=in.readLong();
        mStarTime=in.readLong();
    }

    public static final Parcelable.Creator<Feed> CREATOR
    = new Parcelable.Creator<Feed>() {

        @Override
        public Feed createFromParcel(Parcel source) {
            return new Feed(source);
        }

        @Override
        public Feed[] newArray(int size) {
            return new Feed[size];
        }
    };

	
	
}
