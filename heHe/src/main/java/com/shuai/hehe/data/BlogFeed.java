package com.shuai.hehe.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 日志新鲜事
 */
public class BlogFeed extends Feed {
    /**
     * 日志摘要
     */
    private transient String mSummary;
    
    /**
     * 该日志对应的url页面
     */
    private transient String mWebUrl;

    public BlogFeed() {
        // TODO Auto-generated constructor stub
    }

    public BlogFeed(Parcel in) {
        super(in);
        mSummary=in.readString();
        mWebUrl=in.readString();
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mSummary);
        dest.writeString(mWebUrl);
    }
    
    public static final Parcelable.Creator<BlogFeed> CREATOR
    = new Parcelable.Creator<BlogFeed>() {

        @Override
        public BlogFeed createFromParcel(Parcel source) {
            return new BlogFeed(source);
        }

        @Override
        public BlogFeed[] newArray(int size) {
            return new BlogFeed[size];
        }
        
    };

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        this.mSummary = summary;
    }

    public String getWebUrl() {
        return mWebUrl;
    }

    public void setWebUrl(String webUrl) {
        this.mWebUrl = webUrl;
    }

}
