package com.shuai.hehe.data;

public class VideoFeed extends Feed {
	/**
	 * 视频地址
	 */
	private transient String mVideoUrl;
	
	/**
	 * 视频预览图片地址
	 */
	private transient String mThumbImgUrl;

	public VideoFeed() {
	}

	public String getVideoUrl() {
		return mVideoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.mVideoUrl = videoUrl;
	}

	public String getThumbImgUrl() {
		return mThumbImgUrl;
	}

	public void setThumbImgUrl(String thumbImgUrl) {
		this.mThumbImgUrl = thumbImgUrl;
	}
	
	

}
