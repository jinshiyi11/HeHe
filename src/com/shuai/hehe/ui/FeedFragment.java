package com.shuai.hehe.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.shuai.hehe.HeHeApplication;
import com.shuai.hehe.R;
import com.shuai.hehe.adapter.FeedAdapter;
import com.shuai.hehe.adapter.FeedAdapter.FeedList;
import com.shuai.hehe.data.Feed;
import com.shuai.hehe.protocol.GetFeedsRequest;

public class FeedFragment extends Fragment {
    
    private PullToRefreshListView mListView;
    private FeedList mFeedList=new FeedList(); 
    private FeedAdapter mFeedAdapter;
    
    /**
     * 每次获取的feed个数
     */
	private static final int PAGE_COUNT = 30;
	
	/**
	 * 起始feed的id
	 */
	private static final int START_ID = -1;
	
    /**
     * 是否是启动之后的首次请求
     */ 
    private boolean mIsStartRequest=true;
    
    /**
     * 异步请求队列
     */
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
    
	private void getData(final boolean isPullDown){
		long id=START_ID;
		if(mFeedAdapter.getCount()>0){
			if (isPullDown) {
				id=mFeedAdapter.getItem(0).getShowTime();
			}else{
				id=mFeedAdapter.getItem(mFeedAdapter.getCount()-1).getShowTime();
			}
		}
		
		GetFeedsRequest request=new GetFeedsRequest(id,PAGE_COUNT, new Listener<ArrayList<Feed>>(){

			@Override
			public void onResponse(ArrayList<Feed> feedList) {
				mListView.onRefreshComplete();
				if(mIsStartRequest){
					//成功完成首次请求，clear启动时加载的cache数据
					mFeedAdapter.clear();
				}
				
				if(isPullDown){
					mFeedList.addAll(0, feedList);
				}else{
					mFeedList.addAll(feedList);
				}
			}
    		
    	}, new ErrorListener(){

			@Override
			public void onErrorResponse(VolleyError error) {
				mListView.onRefreshComplete();
				
				Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
			}
    		
    	});
    	
    	request.setTag(this);    	
    	mRequestQueue.add(request);
    }

    
    
    

}
