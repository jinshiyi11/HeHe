package com.shuai.hehe.protocol;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.shuai.hehe.data.Constants;
import com.shuai.hehe.data.DataManager;

import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * 不展示某条新鲜事，只能管理员权限才能使用
 */
public class HideFeedRequest extends StringRequest {

    public HideFeedRequest(Context context,long feedId, Listener<String> listener, ErrorListener errorListener) {
        super(getUrl(context,feedId), listener, errorListener);
    }

    public static String getUrl(Context context, long feedId) {
        List<BasicNameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("feedid", Long.toString(feedId)));

        return UrlHelper.getUrl(context,"hidefeed", params);
    }

}
