package com.shuai.hehe.protocol;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.json.JSONException;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuai.hehe.data.PicInfo;

/**
 * 取相册包含的所有图片
 */
public class GetAlbumPicsRequest extends JsonRequest<ArrayList<PicInfo>> {

    /**
     * 
     * @param feedId 
     * @param listener
     * @param errorListener
     */
    public GetAlbumPicsRequest(int feedId, Listener<ArrayList<PicInfo>> listener, ErrorListener errorListener) {
        super(Method.GET, UrlHelper.getAlbumPicsUrl(feedId), null, listener, errorListener);
    }

    @Override
    protected Response<ArrayList<PicInfo>> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Gson gson=new Gson();

            Type type = new TypeToken<ArrayList<PicInfo>>(){}.getType();
            ArrayList<PicInfo> result=gson.fromJson(jsonString, type);
            
            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            //TODO:这个是否有必要
            return Response.error(new ParseError(e));
        }

    }

}
