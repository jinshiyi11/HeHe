package com.shuai.hehe.protocol;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;

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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.shuai.hehe.data.Constants;
import com.shuai.hehe.data.PicInfo;

import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * 取相册包含的所有图片
 */
public class GetAlbumPicsRequest extends JsonRequest<ArrayList<PicInfo>> {
    private static final String TAG=GetAlbumPicsRequest.class.getSimpleName();

    /**
     * 
     * @param feedId 
     * @param listener
     * @param errorListener
     */
    public GetAlbumPicsRequest(Context context,long feedId, Listener<ArrayList<PicInfo>> listener, ErrorListener errorListener) {
        super(Method.GET, getUrl(context,feedId), null, listener, errorListener);
        if(Constants.DEBUG){
            Log.d(TAG, getUrl(context,feedId));
        }
    }

    /**
     * 获取相册的url
     *
     * @param feedId
     * @return
     */
    private static String getUrl(Context context, long feedId) {
        List<BasicNameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("feedId", Long.toString(feedId)));

        return UrlHelper.getUrl(context,"api/getAlbumPics", params);
    }

    @Override
    protected Response<ArrayList<PicInfo>> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            if(Constants.DEBUG){
                Log.d(TAG, jsonString);
            }

            JsonParser parser=new JsonParser();
            JsonObject root = parser.parse(jsonString).getAsJsonObject();
            ErrorInfo error = ProtocolUtils.getProtocolInfo(root);
            if (error.getErrorCode() != 0) {
                return Response.error(error);
            }

            Gson gson=new Gson();
            Type type = new TypeToken<ArrayList<PicInfo>>(){}.getType();
            ArrayList<PicInfo> result=gson.fromJson(root.getAsJsonArray(ProtocolUtils.DATA), type);
            
            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }

    }

}
