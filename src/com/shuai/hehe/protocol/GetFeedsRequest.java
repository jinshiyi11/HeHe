package com.shuai.hehe.protocol;

import java.util.ArrayList;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonRequest;
import com.shuai.hehe.data.Feed;

public class GetFeedsRequest extends JsonRequest<ArrayList<Feed>> {

	public GetFeedsRequest(long id,int count,Listener<ArrayList<Feed>> listener, ErrorListener errorListener) {
		super(Method.GET, UrlHelper.getFeedsUrl(id, count), null, listener, errorListener);
	}

	@Override
	protected Response<ArrayList<Feed>> parseNetworkResponse(NetworkResponse response) {
		return null;
	}

}
