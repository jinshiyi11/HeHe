package com.shuai.hehe.protocol;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.shuai.hehe.data.Constants;
import com.shuai.hehe.data.VideoInfo;

import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * 根据视频的web url获取对应的视频流文件的url
 */
public class GetVideoUrlRequest extends JsonRequest<VideoInfo> {
    private final static String TAG = GetVideoUrlRequest.class.getSimpleName();

    /**
     * 
     * @param webUrl 视频对应的web页面
     * @param listener
     * @param errorListener
     */
    public GetVideoUrlRequest(Context context,String webUrl, Listener<VideoInfo> listener,
            ErrorListener errorListener) {
        super(Method.GET, getUrl(context,webUrl), null, listener, errorListener);
        if (Constants.DEBUG) {
            Log.d(TAG, getUrl(context,webUrl));
        }
    }

    /**
     * 根据视频的web url获取对应的视频信息
     *
     * @param webUrl
     * @return
     */
    private static String getUrl(Context context, String webUrl) {
        List<BasicNameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("webUrl", webUrl));

        return UrlHelper.getUrl(context,"api/getVideoUrl", params);
    }

    @Override
    protected Response<VideoInfo> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            
            if (Constants.DEBUG) {
                Log.d(TAG, json);
            }
            
            Gson gson=new Gson();
            VideoInfo result=gson.fromJson(json, VideoInfo.class);
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
