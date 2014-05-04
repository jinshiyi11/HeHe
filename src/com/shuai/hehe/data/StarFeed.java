package com.shuai.hehe.data;

/**
 * 收藏的新鲜事
 */
public class StarFeed extends Feed {
    /**
     * 收藏时间
     */
    private long mStarredTime;

    public long getStarredTime() {
        return mStarredTime;
    }

    public void setStarredTime(long starredTime) {
        this.mStarredTime = starredTime;
    }

}
