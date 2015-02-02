package com.shuai.hehe.protocol;

import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.shuai.hehe.data.BlogInfo;
import com.shuai.hehe.data.Constants;

public class GetBlogInfoRequest extends JsonRequest<BlogInfo> {
    private final static String TAG = GetBlogInfoRequest.class.getSimpleName();

    public GetBlogInfoRequest(Context context,long feedId, Listener<BlogInfo> listener,
            ErrorListener errorListener) {
        super(Method.GET, UrlHelper.getBlogUrl(context,feedId,false), null, listener, errorListener);
        if (Constants.DEBUG) {
            Log.d(TAG, UrlHelper.getBlogUrl(context,feedId,false));
        }
    }

    @Override
    protected Response<BlogInfo> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            
            if (Constants.DEBUG) {
                Log.d(TAG, json);
            }
            
            Gson gson=new Gson();
            BlogInfo result=gson.fromJson(json, BlogInfo.class);
            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            //JsonSyntaxException
            //TODO:这个是否有必要
            return Response.error(new ParseError(e));
        }
        
    }

}
