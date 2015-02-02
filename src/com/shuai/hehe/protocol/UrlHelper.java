package com.shuai.hehe.protocol;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.app.Application;
import android.content.Context;
import android.os.Looper;

import com.shuai.hehe.data.Constants;
import com.shuai.hehe.data.DataManager;
import com.shuai.utils.AppUtils;

/**
 * 协议url辅助拼接类
 */
public class UrlHelper {	
	private static String getUrl(String relativePath, List<BasicNameValuePair> params) {
		StringBuilder builder = new StringBuilder(Constants.SERVER_ADDRESS);
		builder.append("/").append(relativePath);
		String query = params == null ? "" : URLEncodedUtils.format(params, "UTF-8");
		if (query.length() != 0)
			builder.append("?").append(query);

		return builder.toString();
	}
	
	/**
	 * 增加公共参数，如版本号，防止恶意攻击的hash等
	 * @param params
	 */
	private static void addCommonParameters(Context context,List<BasicNameValuePair> params){
	    params.add(new BasicNameValuePair("ver", Constants.PROTOCOL_VERSION));
	    params.add(new BasicNameValuePair("channel", AppUtils.getChannel(context)));
	}

	/**
	 * 获取新鲜事的url
	 * @param id
	 * @param count
	 * @return
	 */
	public static String getFeedsUrl(Context context,long id, int count) {
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("id", Long.toString(id)));
		params.add(new BasicNameValuePair("count", Integer.toString(count)));
		if(Constants.DEBUG){
		    params.add(new BasicNameValuePair(Constants.ADMIN_KEY, DataManager.getInstance().getAdminKey()));
		}
		addCommonParameters(context,params);

		return getUrl("getfeeds", params);
	}
	
	/**
	 * 获取相册的url
	 * @param feedId
	 * @return
	 */
    public static String getAlbumPicsUrl(Context context,long feedId) {
        List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("feedid", Long.toString(feedId)));
        addCommonParameters(context,params);

        return getUrl("getalbumpics", params);
    }
    
    public static String getHideFeedUrl(Context context,long feedId) {
        List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("feedid", Long.toString(feedId)));
        params.add(new BasicNameValuePair(Constants.ADMIN_KEY, DataManager.getInstance().getAdminKey()));
        addCommonParameters(context,params);

        return getUrl("hidefeed", params);
    }

    /**
     * 
     * @param feedId
     * @param getHtml 请求返回html还是json数据
     * @return
     */
    public static String getBlogUrl(Context context,long feedId,boolean getHtml) {
        List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("feedid", Long.toString(feedId)));
        addCommonParameters(context,params);

        return getUrl("getblog", params);
    }

    /**
     * 根据视频的web url获取对应的视频信息
     * @param webUrl
     * @return
     */
    public static String getVideoUrl(Context context,String webUrl) {
        List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("web_url", webUrl));
        addCommonParameters(context,params);
        
        return getUrl("get_video_url", params);
    }

}
