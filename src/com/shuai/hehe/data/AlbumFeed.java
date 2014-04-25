package com.shuai.hehe.data;

public class AlbumFeed extends Feed {
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
}
