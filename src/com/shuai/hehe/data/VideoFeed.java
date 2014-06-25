package com.shuai.hehe.data;

public class VideoFeed extends Feed {
    /**
     * 视频对应的web页面，该页面不仅包含视频还包含评论，广告等其它东西
     */
    private transient String mWebVideoUrl;

    /**
	 * flash格式视频的url
	 */
	private transient String mFlashVideoUrl;

	/**
	 * 视频预览图片地址
	 */
	private transient String mVideoThumbUrl;

	public VideoFeed() {
	}
	
	public String getWebVideoUrl() {
        return mWebVideoUrl;
    }

    public void setWebVideoUrl(String webVideoUrl) {
        this.mWebVideoUrl = webVideoUrl;
    }

	public String getFlashVideoUrl() {
		return mFlashVideoUrl;
	}

	public void setFlashVideoUrl(String videoUrl) {
		this.mFlashVideoUrl = videoUrl;
	}

	public String getThumbImgUrl() {
		return mVideoThumbUrl;
	}

	public void setThumbImgUrl(String thumbImgUrl) {
		this.mVideoThumbUrl = thumbImgUrl;
	}
	
	

}
