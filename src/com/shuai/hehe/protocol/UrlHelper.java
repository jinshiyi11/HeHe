package com.shuai.hehe.protocol;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import com.shuai.hehe.data.Constants;

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
	private static void addCommonParameters(List<BasicNameValuePair> params){
	    params.add(new BasicNameValuePair("ver", Constants.PROTOCOL_VERSION));
	}

	/**
	 * 获取新鲜事的url
	 * @param id
	 * @param count
	 * @return
	 */
	public static String getFeedsUrl(long id, int count) {
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("id", Long.toString(id)));
		params.add(new BasicNameValuePair("count", Integer.toString(count)));
		addCommonParameters(params);

		return getUrl("getfeeds", params);
	}
	
	/**
	 * 获取相册的url
	 * @param feedId
	 * @return
	 */
    public static String getAlbumPicsUrl(long feedId) {
        List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("feedid", Long.toString(feedId)));
        addCommonParameters(params);

        return getUrl("getalbumpics", params);
    }

}
