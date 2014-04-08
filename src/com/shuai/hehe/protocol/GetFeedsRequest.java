package com.shuai.hehe.protocol;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.shuai.hehe.data.Feed;
import com.shuai.hehe.data.FeedType;

public class GetFeedsRequest extends JsonRequest<ArrayList<Feed>> {

	public GetFeedsRequest(long id,int count,Listener<ArrayList<Feed>> listener, ErrorListener errorListener) {
		super(Method.GET, UrlHelper.getFeedsUrl(id, count), null, listener, errorListener);
	}

	@Override
	protected Response<ArrayList<Feed>> parseNetworkResponse(NetworkResponse response) {
	    try {
	        ArrayList<Feed> feedList=new ArrayList<Feed>();
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            
            JSONArray jsonArray=new JSONArray(jsonString);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                
                String jsonFeedString=jsonObject.toString();
                int feedType=jsonObject.getInt("type");
                switch (feedType) {
                case FeedType.TYPE_ALBUM:
                    
                    break;
                case FeedType.TYPE_VIDEO:
                    
                    break;
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
            return Response.error(new ParseError(e));
        }
	}

}
