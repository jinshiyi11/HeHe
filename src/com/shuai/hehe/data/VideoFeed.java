package com.shuai.hehe.data;

public class VideoFeed extends Feed {
    /**
     * 视频对应的web页面，该页面不仅包含视频还包含评论，广告等其它东西
     */
    private transient String mWebVideoUrl;

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
	
	public String getWebVideoUrl() {
        return mWebVideoUrl;
    }

    public void setWebVideoUrl(String webVideoUrl) {
        this.mWebVideoUrl = webVideoUrl;
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
