package com.shuai.hehe.protocol;

import android.content.Context;
import android.util.Pair;

import com.shuai.hehe.data.Constants;
import com.shuai.hehe.data.DataManager;
import com.shuai.utils.AppUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

/**
 * 协议url辅助拼接类
 */
public class UrlHelper {	
	private static String getUrl(String relativePath, List<Pair<String,String>> params) {
		StringBuilder builder = new StringBuilder(Constants.SERVER_ADDRESS);
		builder.append("/").append(relativePath);
		String query = params == null ? "" : format(params, "UTF-8");
		if (query.length() != 0)
			builder.append("?").append(query);

		return builder.toString();
	}

	private static String format (
			final List <Pair<String,String>> parameters,
			final String encoding) {
		final StringBuilder result = new StringBuilder();
		for (final Pair<String,String> parameter : parameters) {
			final String encodedName = encode(parameter.first, encoding);
			final String value = parameter.second;
			final String encodedValue = value != null ? encode(value, encoding) : "";
			if (result.length() > 0)
				result.append("&");
			result.append(encodedName);
			result.append("=");
			result.append(encodedValue);
		}
		return result.toString();
	}

	private static String encode (final String content, final String encoding) {
		try {
			return URLEncoder.encode(content,
					encoding != null ? encoding : "UTF-8");
		} catch (UnsupportedEncodingException problem) {
			throw new IllegalArgumentException(problem);
		}
	}
	
	/**
	 * 增加公共参数，如版本号，防止恶意攻击的hash等
	 * @param params
	 */
	private static void addCommonParameters(Context context,List<Pair<String, String>> params){
	    params.add(new Pair("ver", Constants.PROTOCOL_VERSION));
	    params.add(new Pair("channel", AppUtils.getChannel(context)));
	}

	/**
	 * 获取新鲜事的url
	 * @param id
	 * @param count
	 * @return
	 */
	public static String getFeedsUrl(Context context,long id, int count) {
		List<Pair<String, String>> params = new LinkedList<>();
		params.add(new Pair<>("id", Long.toString(id)));
		params.add(new Pair<>("count", Integer.toString(count)));
		if(DataManager.getInstance().isAdmin()){
		    params.add(new Pair(Constants.ADMIN_KEY, DataManager.getInstance().getAdminKey()));
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
        List<Pair<String, String>> params = new LinkedList<>();
        params.add(new Pair("feedid", Long.toString(feedId)));
        addCommonParameters(context,params);

        return getUrl("getalbumpics", params);
    }
    
    public static String getHideFeedUrl(Context context,long feedId) {
        List<Pair<String, String>> params = new LinkedList<>();
        params.add(new Pair("feedid", Long.toString(feedId)));
        params.add(new Pair(Constants.ADMIN_KEY, DataManager.getInstance().getAdminKey()));
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
        List<Pair<String, String>> params = new LinkedList<>();
        params.add(new Pair("feedid", Long.toString(feedId)));
        addCommonParameters(context,params);

        return getUrl("getblog", params);
    }

    /**
     * 根据视频的web url获取对应的视频信息
     * @param webUrl
     * @return
     */
    public static String getVideoUrl(Context context,String webUrl) {
        List<Pair<String, String>> params = new LinkedList<>();
        params.add(new Pair("web_url", webUrl));
        addCommonParameters(context,params);
        
        return getUrl("get_video_url", params);
    }

    public static String getVideoDetailUrl(Context context, String videoId) {
        List<Pair<String, String>> params = new LinkedList<>();
        params.add(new Pair("video_id", videoId));
        addCommonParameters(context,params);

        return getUrl("get_video_detail", params);
    }
}
