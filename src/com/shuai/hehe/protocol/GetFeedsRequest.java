package com.shuai.hehe.protocol;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.shuai.hehe.data.AlbumFeed;
import com.shuai.hehe.data.Constants;
import com.shuai.hehe.data.Feed;
import com.shuai.hehe.data.FeedType;
import com.shuai.hehe.data.VideoFeed;

public class GetFeedsRequest extends JsonRequest<ArrayList<Feed>> {
    private final String TAG=getClass().getSimpleName();

	public GetFeedsRequest(long id,int count,Listener<ArrayList<Feed>> listener, ErrorListener errorListener) {
		super(Method.GET, UrlHelper.getFeedsUrl(id, count), null, listener, errorListener);
		if(Constants.DEBUG){
		    Log.d(TAG, UrlHelper.getFeedsUrl(id, count));
		}
	}

	@Override
	protected Response<ArrayList<Feed>> parseNetworkResponse(NetworkResponse response) {
	    try {
	        ArrayList<Feed> feedList=new ArrayList<Feed>();
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            if(Constants.DEBUG){
                Log.d(TAG, jsonString);
            }
            
			JSONArray jsonArray = new JSONArray(jsonString);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);

				String jsonFeedString = jsonObject.toString();
				int feedType = jsonObject.getInt("type");
				switch (feedType) {
				case FeedType.TYPE_ALBUM: {
					Gson gson = new Gson();
					AlbumFeed feed = gson.fromJson(jsonFeedString, AlbumFeed.class);

					JSONObject content = new JSONObject(feed.getContent());
					feed.setThumbImgUrl(content.getString("thumbImgUrl"));

					feedList.add(feed);
					break;
				}
				case FeedType.TYPE_VIDEO: {
					Gson gson = new Gson();
					VideoFeed feed = gson.fromJson(jsonFeedString, VideoFeed.class);

					JSONObject content = new JSONObject(feed.getContent());
					feed.setVideoUrl(content.getString("videoUrl"));
					feed.setThumbImgUrl(content.getString("thumbImgUrl"));

					feedList.add(feed);
					break;
				}
				default:
					break;
				}
			}
            
            return Response.success(feedList, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        } catch (Exception e) {
        	//TODO:这个是否有必要
            return Response.error(new ParseError(e));
        }
	}

}
