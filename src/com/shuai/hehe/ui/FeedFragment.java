package com.shuai.hehe.ui;

import org.json.JSONArray;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.JsonArray;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.shuai.hehe.HeHeApplication;
import com.shuai.hehe.R;
import com.shuai.hehe.adapter.FeedAdapter;
import com.shuai.hehe.adapter.FeedAdapter.FeedList;

public class FeedFragment extends Fragment {
    
    private PullToRefreshListView mListView;
    private FeedList mFeedList=new FeedList(); 
    private FeedAdapter mFeedAdapter;
    /**
     * 是否是启动之后的首次请求
     */ 
    private boolean mIsStartRequest=true;
    private RequestQueue mRequestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.feed_fragment, container,false);
        mRequestQueue=HeHeApplication.getRequestQueue();
        mListView=(PullToRefreshListView) view.findViewById(R.id.listview);
        mListView.setMode(Mode.BOTH);
        mFeedAdapter=new FeedAdapter(getActivity(), mFeedList);
        mListView.setAdapter(mFeedAdapter);
        
        mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData(true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData(false);
            }
        });
        
        mListView.setRefreshing();
        return view;
    }
    
    @Override
	public void onDestroyView() {
    	mRequestQueue.cancelAll(this);
		super.onDestroyView();
	}



	private void getData(boolean isPullDown){
    	String url="http://10.0.2.2:8080/hehe_server/getfeeds";
    	JsonArrayRequest request=new JsonArrayRequest(url, new Listener<JSONArray>(){

			@Override
			public void onResponse(JSONArray arg0) {
				mListView.onRefreshComplete();
			}
    		
    	}, new ErrorListener(){

			@Override
			public void onErrorResponse(VolleyError arg0) {
				mListView.onRefreshComplete();
			}
    		
    	});
    	
    	request.setTag(this);    	
    	mRequestQueue.add(request);
    }

    
    
    

}
