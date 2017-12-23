package com.shuai.hehe.protocol;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.shuai.hehe.data.Constants;
import com.shuai.hehe.data.VideoDetail;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * 获取视频详细信息
 * 见http://ib.365yg.com/video/urls/v/1/toutiao/mp4/4607a7f153184655a520e831b9e54bf0?r=4735340173517406&s=2469716497
 */
public class GetVideoDetailRequest extends JsonRequest<VideoDetail> {
    private static final String TAG = GetVideoDetailRequest.class.getSimpleName();

    public GetVideoDetailRequest(Context context, String videoId, Response.Listener<VideoDetail> listener, Response.ErrorListener errorListener) {
        super(Method.GET, getUrl(context, videoId), null, listener, errorListener);
        if (Constants.DEBUG) {
            Log.d(TAG, getUrl(context, videoId));
        }
    }

    private static String getUrl(Context context, String videoId) {
        List<BasicNameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("videoId", videoId));

        return UrlHelper.getUrl(context,"api/getVideoDetail", params);
    }

    @Override
    protected Response<VideoDetail> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            if (Constants.DEBUG) {
                Log.d(TAG, jsonString);
            }

            JSONObject json = new JSONObject(jsonString);
            int code = json.getInt("code");
            if (code != 0) {
                //code如果为1表示"未通过审核，视频无法播放"
                String message=json.optString("message");
                return Response.error(new ErrorInfo(ErrorInfo.ERROR_DATA,message));
            }

            JSONObject dataJson=json.getJSONObject("data");
            VideoDetail result = new VideoDetail();
            result.mDuration = dataJson.optDouble("video_duration");
            JSONObject videoList = dataJson.getJSONObject("video_list");

            JSONObject video;
            if (videoList.has("video_3")) {
                video = videoList.getJSONObject("video_3");
            } else if (videoList.has("video_2")) {
                video = videoList.getJSONObject("video_2");
            } else {
                video = videoList.getJSONObject("video_1");
            }

            result.mAddressUrl = new String(Base64.decode(video.getString("main_url"), Base64.DEFAULT));

            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

}
