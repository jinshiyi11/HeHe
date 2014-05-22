package com.shuai.hehe.data;

import javax.security.auth.Destroyable;

import android.os.Parcel;
import android.os.Parcelable;

public class AlbumFeed extends Feed implements Parcelable {
	/**
	 * 相册封面缩略图地址
	 */
	private transient String mThumbImgUrl;
	
	/**
     * 相册封面大图地址
     */
	private transient String mBigImgUrl;

	public AlbumFeed() {
	}

    public String getThumbImgUrl() {
		return mThumbImgUrl;
	}

	public void setThumbImgUrl(String thumbImgUrl) {
		this.mThumbImgUrl = thumbImgUrl;
	}
	
	public String getBigImgUrl() {
        return mBigImgUrl;
    }

    public void setBigImgUrl(String bigImgUrl) {
        this.mBigImgUrl = bigImgUrl;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mThumbImgUrl);
        dest.writeString(mBigImgUrl);
    }
    
    public AlbumFeed(Parcel source) {
        super(source);
        mThumbImgUrl=source.readString();
        mBigImgUrl=source.readString();
    }
    
    public static final Parcelable.Creator<AlbumFeed> CREATOR
    = new Parcelable.Creator<AlbumFeed>() {

        @Override
        public AlbumFeed createFromParcel(Parcel source) {
            return new AlbumFeed(source);
        }

        @Override
        public AlbumFeed[] newArray(int size) {
            return new AlbumFeed[size];
        }
        
    };
}
