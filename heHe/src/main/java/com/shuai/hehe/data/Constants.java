package com.shuai.hehe.data;

public class Constants {
    public static boolean DEBUG = false;
    /**
     * 是使用线上服务还是开发环境的服务
     */
    public static boolean SERVER_ONLINE = true;
    
    /**
     * jsoup的USER_AGENT
     */
    public static final String USER_AGENT="Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36";
    
    /**
     * jsoup访问网络的超时时间，单位ms
     */
    public static final int JSOUP_TIMEOUT=30*1000;
    
    public static final String SERVER_ADDRESS;
    /**
     * 用来去除视频网页中非视频元素的javascript
     */
    public static final String VIDEO_JS_URL;
    public static final String VIDEO_JS_FILENAME="video.js";

    /**
     * 协议版本号
     */
    public static final String PROTOCOL_VERSION = "1.3";
    
    public static final String ADMIN_KEY="admin";
    
    public static final String PREF_NAME="preferences";

    public static final String FEED = "feed";
    public static final String FEED_ID = "feed_id";
    public static final String FEED_ALBUM = "feed_album";
    public static final String FEED_BLOG = "feed_blog";
    /**
     * 视频对应的网页url
     */
    public static final String WEB_VIDEO_URL = "web_video_url";
    public static final String VIDEO_TITLE = "video_title";
    
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
	 * QQ appid
	 */
    public static final String APP_ID_QQ = "101076410";
    
    /**
     * QQ app key
     */
    public static final String APP_KEY_QQ = "6f215d9a24ad784cb8607cdf7c4b50d0";
    
    /**
     * 微信appid
     */
    public static final String APP_ID_WEIXIN = "wx4d1680ae1b14a8a9";
    
    /**
     * 微信app secret
     */
    public static final String APP_SECRET_WEIXIN="1e18f79f3116e282680e38db19d750d3";
    
    /**
     * renren appid
     */
    public static final String APP_ID_RENREN = "269775";
    
    /**
     * renren app key
     */
    public static final String APP_KEY_RENREN = "213c1e7e8cec472c80c35f085a03dbe2";
    
    /**
     * renren app secret
     */
    public static final String APP_SECRET_RENREN = "84353fbcbe624317a17f47870e87457b";
    

    static {
        if (!SERVER_ONLINE) {
            SERVER_ADDRESS = "http://10.0.2.2:8080/hehe_server";
            VIDEO_JS_URL = "http://10.0.2.2:8080/hehe_server/js/"+VIDEO_JS_FILENAME;
        } else {
            //SERVER_ADDRESS = "http://hehe1.sinaapp.com";
        	SERVER_ADDRESS = "http://hehedream.duapp.com";
        	VIDEO_JS_URL= "http://bcs.duapp.com/hehe-data/"+VIDEO_JS_FILENAME;
        }
    }

}
