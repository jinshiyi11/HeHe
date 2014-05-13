package com.shuai.hehe.data;

public class Constants {
    public static boolean DEBUG = false;
    public static final String SERVER_ADDRESS;

    /**
     * 协议版本号
     */
    public static final String PROTOCOL_VERSION = "1.0";

    public static final String FEED_ID = "feed_id";
    public static final String VIDEO_URL = "video_url";
    
    //广播
    /**
     * 添加收藏
     */
    public static final String ACTION_ADD_STAR_FEED="action_add_star_feed";
    
    /**
     * 取消收藏
     */
    public static final String ACTION_DELETE_STAR_FEED="action_delete_star_feed";
    
    //
    public static final String PUT_EXTRA_FEED_ID="put_extra_feed_id";
    public static final String PUT_EXTRA_FEED="put_extra_feed";

    /**
	 * 
	 */
    public static final String APP_ID_QQ = "101076410";
    
    public static final String APP_ID_WEIXIN = "101076410";

    static {
        if (DEBUG) {
            SERVER_ADDRESS = "http://10.0.2.2:8080/hehe_server";
        } else {
            SERVER_ADDRESS = "http://hehe1.sinaapp.com";
        }
    }

}
