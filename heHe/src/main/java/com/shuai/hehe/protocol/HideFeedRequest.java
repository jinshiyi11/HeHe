package com.shuai.hehe.protocol;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;

/**
 * 不展示某条新鲜事，只能管理员权限才能使用
 */
public class HideFeedRequest extends StringRequest {

    public HideFeedRequest(Context context,long feedId, Listener<String> listener, ErrorListener errorListener) {
        super(UrlHelper.getHideFeedUrl(context,feedId), listener, errorListener);
    }

}