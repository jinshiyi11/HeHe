package com.shuai.hehe.data;

public class AlbumFeed extends Feed {
	/**
	 * 相册预览图片地址
	 */
	private transient String mThumbImgUrl;

	public AlbumFeed() {
	}

	public String getThumbImgUrl() {
		return mThumbImgUrl;
	}

	public void setThumbImgUrl(String thumbImgUrl) {
		this.mThumbImgUrl = thumbImgUrl;
	}
}
